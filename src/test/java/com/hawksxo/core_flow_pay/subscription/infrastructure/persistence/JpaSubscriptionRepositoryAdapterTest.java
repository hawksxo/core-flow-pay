package com.hawksxo.core_flow_pay.subscription.infrastructure.persistence;

import com.hawksxo.core_flow_pay.subscription.domain.DuplicateIdempotencyKeyException;
import com.hawksxo.core_flow_pay.subscription.domain.Subscription;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class JpaSubscriptionRepositoryAdapterTest {
    @Autowired
    private SpringDataSubscriptionRepository jpaRepository;

    private JpaSubscriptionRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JpaSubscriptionRepositoryAdapter(jpaRepository);
    }

    @Test
    void saveAndFindById_roundTrip() {
        Subscription sub = Subscription.create("user-1", "plan-1", "idem-1", Instant.parse("2026-01-01T00:00:00Z"));
        Subscription saved = adapter.save(sub);

        Optional<Subscription> found = adapter.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("user-1", found.get().getUserId());
        assertEquals("plan-1", found.get().getPlanId());
        assertEquals(SubscriptionStatus.PENDING, found.get().getStatus());
        assertEquals("idem-1", found.get().getIdempotencyKey());
    }

    @Test
    void findByIdempotencyKey_returnsSubscription() {
        Subscription sub = Subscription.create("user-1", "plan-1", "idem-unique", Instant.parse("2026-01-01T00:00:00Z"));
        adapter.save(sub);

        Optional<Subscription> found = adapter.findByIdempotencyKey("idem-unique");

        assertTrue(found.isPresent());
        assertEquals("idem-unique", found.get().getIdempotencyKey());
    }

    @Test
    void existsByUserIdAndStatus_returnsTrueWhenActive() {
        Subscription sub = Subscription.create("user-1", "plan-1", "idem-1", Instant.parse("2026-01-01T00:00:00Z"));
        sub.activate(Instant.parse("2026-01-01T00:00:00Z"));
        adapter.save(sub);

        boolean exists = adapter.existsByUserIdAndStatus("user-1", SubscriptionStatus.ACTIVE);

        assertTrue(exists);
    }

    @Test
    void existsByUserIdAndStatus_returnsFalseWhenNotActive() {
        Subscription sub = Subscription.create("user-1", "plan-1", "idem-1", Instant.parse("2026-01-01T00:00:00Z"));
        adapter.save(sub);

        boolean exists = adapter.existsByUserIdAndStatus("user-1", SubscriptionStatus.ACTIVE);

        assertFalse(exists);
    }

    @Test
    void save_duplicateIdempotencyKey_throwsDomainException() {
        Subscription sub1 = Subscription.create("user-1", "plan-1", "same-key", Instant.parse("2026-01-01T00:00:00Z"));
        adapter.save(sub1);

        Subscription sub2 = Subscription.create("user-2", "plan-1", "same-key", Instant.parse("2026-01-01T00:00:00Z"));

        assertThrows(DuplicateIdempotencyKeyException.class, () -> adapter.save(sub2));
    }
}