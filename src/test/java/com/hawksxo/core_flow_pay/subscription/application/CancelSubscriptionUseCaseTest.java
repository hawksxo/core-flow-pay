package com.hawksxo.core_flow_pay.subscription.application;

import com.hawksxo.core_flow_pay.subscription.domain.Subscription;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionNotFoundException;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionRepository;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelSubscriptionUseCaseTest {

    @Mock
    private SubscriptionRepository repository;

    private CancelSubscriptionUseCase useCase;

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    @BeforeEach
    void setUp() {
        useCase = new CancelSubscriptionUseCase(repository);
    }

    @Test
    void execute_successfulCancellation() {
        Subscription active = Subscription.create("user-1", "plan-1", "idem-1", NOW);
        active.activate(NOW);

        when(repository.findById(active.getId())).thenReturn(Optional.of(active));
        when(repository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(active.getId());

        assertEquals(SubscriptionStatus.CANCELLED, result.status());
    }

    @Test
    void execute_subscriptionNotFound_throwsException() {
        when(repository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(SubscriptionNotFoundException.class, () -> useCase.execute("non-existent"));
    }
}