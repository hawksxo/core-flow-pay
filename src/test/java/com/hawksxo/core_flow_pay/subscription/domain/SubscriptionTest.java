package com.hawksxo.core_flow_pay.subscription.domain;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionTest {
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final String USER_ID = "user-123";
    private static final String PLAN_ID = "plan-basic";
    private static final String IDEMPOTENCY_KEY = "idem-key-1";

    @Test
    void create_returnsPendingSubscription() {
        Subscription sub = Subscription.create(USER_ID, PLAN_ID, IDEMPOTENCY_KEY, NOW);

        assertEquals(SubscriptionStatus.PENDING, sub.getStatus());
        assertEquals(USER_ID, sub.getUserId());
        assertEquals(PLAN_ID, sub.getPlanId());
        assertEquals(IDEMPOTENCY_KEY, sub.getIdempotencyKey());
        assertEquals(NOW, sub.getCreatedAt());
        assertNull(sub.getExpiredAt());
        assertNotNull(sub.getId());
    }

    @Test
    void activate_fromPending_setsActiveAndExpiry() {
        Subscription sub = Subscription.create(USER_ID, PLAN_ID, IDEMPOTENCY_KEY, NOW);

        sub.activate(NOW);

        assertEquals(SubscriptionStatus.ACTIVE, sub.getStatus());
        assertEquals(NOW.plusSeconds(30L * 24 * 60 * 60), sub.getExpiredAt());
    }

    @Test
    void activate_fromCancelled_throwsSubscriptionInvalidStateException() {
        Subscription sub = Subscription.create(USER_ID, PLAN_ID, IDEMPOTENCY_KEY, NOW);
        sub.cancel();

        assertThrows(SubscriptionInvalidStateException.class, () -> sub.activate(NOW));
    }

    @Test
    void activate_fromActive_throwsInvalidStateTransitionException() {
        Subscription sub = Subscription.create(USER_ID, PLAN_ID, IDEMPOTENCY_KEY, NOW);
        sub.activate(NOW);

        assertThrows(InvalidSubscriptionStateTransitionException.class, () -> sub.activate(NOW));
    }

    @Test
    void cancel_fromAnyState_setsCancelled() {
        Subscription pending = Subscription.create(USER_ID, PLAN_ID, IDEMPOTENCY_KEY, NOW);
        pending.cancel();
        assertEquals(SubscriptionStatus.CANCELLED, pending.getStatus());

        Subscription active = Subscription.create(USER_ID, PLAN_ID, "idem-2", NOW);
        active.activate(NOW);
        active.cancel();
        assertEquals(SubscriptionStatus.CANCELLED, active.getStatus());
    }

    @Test
    void isExpired_beforeExpiry_returnsFalse() {
        Subscription sub = Subscription.reconstruct("id", USER_ID, PLAN_ID, SubscriptionStatus.ACTIVE, IDEMPOTENCY_KEY, NOW, NOW.plusSeconds(3600));
        assertFalse(sub.isExpired(NOW));
    }

    @Test
    void isExpired_afterExpiry_returnsTrue() {
        Subscription sub = Subscription.reconstruct("id", USER_ID, PLAN_ID, SubscriptionStatus.ACTIVE, IDEMPOTENCY_KEY, NOW, NOW.minusSeconds(3600));
        assertTrue(sub.isExpired(NOW));
    }

    @Test
    void checkAndExpire_activeAndExpired_setsExpired() {
        Subscription sub = Subscription.reconstruct("id", USER_ID, PLAN_ID, SubscriptionStatus.ACTIVE, IDEMPOTENCY_KEY, NOW, NOW.minusSeconds(3600));
        sub.checkAndExpire(NOW);
        assertEquals(SubscriptionStatus.EXPIRED, sub.getStatus());
    }

    @Test
    void reconstruct_preservesAllFields() {
        Instant expiredAt = NOW.plusSeconds(86400);
        Subscription sub = Subscription.reconstruct("fixed-id", USER_ID, PLAN_ID, SubscriptionStatus.ACTIVE, IDEMPOTENCY_KEY, NOW, expiredAt);

        assertEquals("fixed-id", sub.getId());
        assertEquals(USER_ID, sub.getUserId());
        assertEquals(PLAN_ID, sub.getPlanId());
        assertEquals(SubscriptionStatus.ACTIVE, sub.getStatus());
        assertEquals(IDEMPOTENCY_KEY, sub.getIdempotencyKey());
        assertEquals(NOW, sub.getCreatedAt());
        assertEquals(expiredAt, sub.getExpiredAt());
    }
}