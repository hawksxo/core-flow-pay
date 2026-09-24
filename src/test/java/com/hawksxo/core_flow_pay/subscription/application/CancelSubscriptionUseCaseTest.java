package com.hawksxo.core_flow_pay.subscription.application;

import com.hawksxo.core_flow_pay.subscription.domain.Subscription;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionRepository;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CancelSubscriptionUseCaseTest {
    private SubscriptionRepository repository;
    private CancelSubscriptionUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(SubscriptionRepository.class);
        useCase = new CancelSubscriptionUseCase(repository);
    }

    @Test
    void execute_success_returnsCancelledSubscription() {
        Subscription pending = Subscription.reconstruct("sub-1", "user-1", SubscriptionStatus.PENDING, "idem-1", Instant.parse("2026-01-01T00:00:00Z"), null);
        when(repository.findById("sub-1")).thenReturn(Optional.of(pending));
        when(repository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute("sub-1");

        assertEquals("sub-1", result.id());
        assertEquals(SubscriptionStatus.CANCELLED, result.status());
    }

    @Test
    void execute_notFound_throwsException() {
        when(repository.findById("sub-1")).thenReturn(Optional.empty());

        assertThrows(com.hawksxo.core_flow_pay.subscription.domain.SubscriptionNotFoundException.class,
            () -> useCase.execute("sub-1"));
    }

    @Test
    void execute_alreadyCancelled_returnsCancelledIdempotently() {
        Subscription cancelled = Subscription.reconstruct("sub-1", "user-1", SubscriptionStatus.CANCELLED, "idem-1", Instant.parse("2026-01-01T00:00:00Z"), null);
        when(repository.findById("sub-1")).thenReturn(Optional.of(cancelled));
        when(repository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute("sub-1");

        assertEquals(SubscriptionStatus.CANCELLED, result.status());
    }
}