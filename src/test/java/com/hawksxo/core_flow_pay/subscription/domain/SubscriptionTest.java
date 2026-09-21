package com.hawksxo.core_flow_pay.subscription.domain;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionTest {
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final String USER_ID = "user-123";
    private static final String IDEMPOTENCY_KEY = "idem-key-1";

    @Test
    void create_returnsPendingSubscription() {
        Subscription sub = Subscription.create(USER_ID, IDEMPOTENCY_KEY, NOW);

        assertEquals(SubscriptionStatus.PENDING, sub.getStatus());
        assertEquals(USER_ID, sub.getUserId());
        assertEquals(IDEMPOTENCY_KEY, sub.getIdempotencyKey());
        assertEquals(NOW, sub.getCreatedAt());
        assertNull(sub.getExpiredAt());
        assertNotNull(sub.getId());
    }

    @Test
    void activate_fromPending_setsActiveAndExpiry() {
        Subscription sub = Subscription.create(USER_ID, IDEMPOTENCY_KEY, NOW);

        sub.activate(NOW);

        assertEquals(SubscriptionStatus.ACTIVE, sub.getStatus());
        assertEquals(NOW.plusSeconds(30 * 24 * 60 * 60), sub.getExpiredAt());
    }

    @Test
    void activate_fromNonPending_throwsException() {
        Subscription sub = Subscription.create(USER_ID, IDEMPOTENCY_KEY, NOW);
        sub.activate(NOW);

        assertThrows(InvalidSubscriptionStateTransitionException.class, () -> sub.activate(NOW));
    }

    @Test
    void cancel_fromAnyState_setsCancelled() {
        Subscription pending = Subscription.create(USER_ID, IDEMPOTENCY_KEY, NOW);
        pending.cancel();
        assertEquals(SubscriptionStatus.CANCELLED, pending.getStatus());

        Subscription active = Subscription.create(USER_ID, "idem-2", NOW);
        active.activate(NOW);
        active.cancel();
        assertEquals(SubscriptionStatus.CANCELLED, active.getStatus());

        Subscription expired = Subscription.reconstruct("id", USER_ID, SubscriptionStatus.EXPIRED, "idem-3", NOW, NOW.minusSeconds(1));
        expired.cancel();
        assertEquals(SubscriptionStatus.CANCELLED, expired.getStatus());
    }

    @Test
    void cancel_alreadyCancelled_isIdempotent() {
        Subscription sub = Subscription.create(USER_ID, IDEMPOTENCY_KEY, NOW);
        sub.cancel();
        sub.cancel();
        assertEquals(SubscriptionStatus.CANCELLED, sub.getStatus());
    }

    @Test
    void isExpired_beforeExpiry_returnsFalse() {
        Subscription sub = Subscription.reconstruct("id", USER_ID, SubscriptionStatus.ACTIVE, IDEMPOTENCY_KEY, NOW, NOW.plusSeconds(3600));
        assertFalse(sub.isExpired(NOW));
    }

    @Test
    void isExpired_afterExpiry_returnsTrue() {
        Subscription sub = Subscription.reconstruct("id", USER_ID, SubscriptionStatus.ACTIVE, IDEMPOTENCY_KEY, NOW, NOW.minusSeconds(3600));
        assertTrue(sub.isExpired(NOW));
    }

    @Test
    void isExpired_noExpiry_returnsFalse() {
        Subscription sub = Subscription.create(USER_ID, IDEMPOTENCY_KEY, NOW);
        assertFalse(sub.isExpired(NOW));
    }

    @Test
    void checkAndExpire_activeAndExpired_setsExpired() {
        Subscription sub = Subscription.reconstruct("id", USER_ID, SubscriptionStatus.ACTIVE, IDEMPOTENCY_KEY, NOW, NOW.minusSeconds(3600));
        sub.checkAndExpire(NOW);
        assertEquals(SubscriptionStatus.EXPIRED, sub.getStatus());
    }

    @Test
    void checkAndExpire_notExpired_unchanged() {
        Subscription sub = Subscription.reconstruct("id", USER_ID, SubscriptionStatus.ACTIVE, IDEMPOTENCY_KEY, NOW, NOW.plusSeconds(3600));
        sub.checkAndExpire(NOW);
        assertEquals(SubscriptionStatus.ACTIVE, sub.getStatus());
    }

    @Test
    void checkAndExpire_notActive_unchanged() {
        Subscription sub = Subscription.reconstruct("id", USER_ID, SubscriptionStatus.PENDING, IDEMPOTENCY_KEY, NOW, NOW.minusSeconds(3600));
        sub.checkAndExpire(NOW);
        assertEquals(SubscriptionStatus.PENDING, sub.getStatus());
    }

    @Test
    void reconstruct_preservesAllFields() {
        Instant expiredAt = NOW.plusSeconds(86400);
        Subscription sub = Subscription.reconstruct("fixed-id", USER_ID, SubscriptionStatus.ACTIVE, IDEMPOTENCY_KEY, NOW, expiredAt);

        assertEquals("fixed-id", sub.getId());
        assertEquals(USER_ID, sub.getUserId());
        assertEquals(SubscriptionStatus.ACTIVE, sub.getStatus());
        assertEquals(IDEMPOTENCY_KEY, sub.getIdempotencyKey());
        assertEquals(NOW, sub.getCreatedAt());
        assertEquals(expiredAt, sub.getExpiredAt());
    }
}