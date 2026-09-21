package com.hawksxo.core_flow_pay.subscription.application;

import com.hawksxo.core_flow_pay.subscription.domain.Subscription;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionRepository;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivateSubscriptionUseCaseTest {
    private SubscriptionRepository repository;
    private Clock clock;
    private ActivateSubscriptionUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(SubscriptionRepository.class);
        clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        useCase = new ActivateSubscriptionUseCase(repository, clock);
    }

    @Test
    void execute_success_returnsActivatedSubscription() {
        Subscription pending = Subscription.reconstruct("sub-1", "user-1", SubscriptionStatus.PENDING, "idem-1", Instant.parse("2026-01-01T00:00:00Z"), null);
        when(repository.findById("sub-1")).thenReturn(Optional.of(pending));
        when(repository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute("sub-1");

        assertEquals("sub-1", result.id());
        assertEquals(SubscriptionStatus.ACTIVE, result.status());
        assertNotNull(result.expiredAt());
    }

    @Test
    void execute_notFound_throwsException() {
        when(repository.findById("sub-1")).thenReturn(Optional.empty());

        assertThrows(com.hawksxo.core_flow_pay.subscription.domain.SubscriptionNotFoundException.class,
            () -> useCase.execute("sub-1"));
    }

    @Test
    void execute_alreadyActive_throwsException() {
        Subscription active = Subscription.reconstruct("sub-1", "user-1", SubscriptionStatus.ACTIVE, "idem-1", Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-01-31T00:00:00Z"));
        when(repository.findById("sub-1")).thenReturn(Optional.of(active));

        assertThrows(com.hawksxo.core_flow_pay.subscription.domain.InvalidSubscriptionStateTransitionException.class,
            () -> useCase.execute("sub-1"));
    }

    @Test
    void execute_cancelled_throwsException() {
        Subscription cancelled = Subscription.reconstruct("sub-1", "user-1", SubscriptionStatus.CANCELLED, "idem-1", Instant.parse("2026-01-01T00:00:00Z"), null);
        when(repository.findById("sub-1")).thenReturn(Optional.of(cancelled));

        assertThrows(com.hawksxo.core_flow_pay.subscription.domain.InvalidSubscriptionStateTransitionException.class,
            () -> useCase.execute("sub-1"));
    }
}