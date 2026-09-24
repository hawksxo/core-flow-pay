package com.hawksxo.core_flow_pay.subscription.application;

import com.hawksxo.core_flow_pay.subscription.domain.InvalidSubscriptionStateTransitionException;
import com.hawksxo.core_flow_pay.subscription.domain.Subscription;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionNotFoundException;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionRepository;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivateSubscriptionUseCaseTest {

    @Mock
    private SubscriptionRepository repository;

    private Clock fixedClock;
    private ActivateSubscriptionUseCase useCase;

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(NOW, ZoneId.of("UTC"));
        useCase = new ActivateSubscriptionUseCase(repository, fixedClock);
    }

    @Test
    void execute_successfulActivation() {
        Subscription pending = Subscription.create("user-1", "plan-1", "idem-1", NOW);
        when(repository.findById(pending.getId())).thenReturn(Optional.of(pending));
        when(repository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(pending.getId());

        assertEquals(SubscriptionStatus.ACTIVE, result.status());
        assertEquals(NOW.plusSeconds(30L * 24 * 60 * 60), result.expiredAt());
    }

    @Test
    void execute_subscriptionNotFound_throwsException() {
        when(repository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(SubscriptionNotFoundException.class, () -> useCase.execute("non-existent"));
    }

    @Test
    void execute_alreadyActive_throwsException() {
        Subscription active = Subscription.create("user-1", "plan-1", "idem-1", NOW);
        active.activate(NOW);

        when(repository.findById(active.getId())).thenReturn(Optional.of(active));

        assertThrows(InvalidSubscriptionStateTransitionException.class, () -> useCase.execute(active.getId()));
    }
}