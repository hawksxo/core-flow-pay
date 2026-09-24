package com.hawksxo.core_flow_pay.subscription.application;

import com.hawksxo.core_flow_pay.subscription.domain.DuplicateIdempotencyKeyException;
import com.hawksxo.core_flow_pay.subscription.domain.Subscription;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionAlreadyActiveException;
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
class CreateSubscriptionUseCaseTest {

    @Mock
    private SubscriptionRepository repository;

    private Clock fixedClock;
    private CreateSubscriptionUseCase useCase;

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(NOW, ZoneId.of("UTC"));
        useCase = new CreateSubscriptionUseCase(repository, fixedClock);
    }

    @Test
    void execute_successfulCreation() {
        CreateSubscriptionCommand command = new CreateSubscriptionCommand("user-1", "plan-1", "idem-1");

        when(repository.findByIdempotencyKey("idem-1")).thenReturn(Optional.empty());
        when(repository.existsByUserIdAndStatus("user-1", SubscriptionStatus.ACTIVE)).thenReturn(false);
        when(repository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(command);

        assertNotNull(result.id());
        assertEquals("user-1", result.userId());
        assertEquals("plan-1", result.planId());
        assertEquals(SubscriptionStatus.PENDING, result.status());
        assertEquals("idem-1", result.idempotencyKey());
        assertEquals(NOW, result.createdAt());
        assertNull(result.expiredAt());
    }

    @Test
    void execute_duplicateIdempotencyKey_throwsException() {
        CreateSubscriptionCommand command = new CreateSubscriptionCommand("user-1", "plan-1", "idem-1");
        Subscription existing = Subscription.create("user-1", "plan-1", "idem-1", NOW);

        when(repository.findByIdempotencyKey("idem-1")).thenReturn(Optional.of(existing));

        assertThrows(DuplicateIdempotencyKeyException.class, () -> useCase.execute(command));
    }

    @Test
    void execute_userHasActiveSubscription_throwsException() {
        CreateSubscriptionCommand command = new CreateSubscriptionCommand("user-1", "plan-1", "idem-2");

        when(repository.findByIdempotencyKey("idem-2")).thenReturn(Optional.empty());
        when(repository.existsByUserIdAndStatus("user-1", SubscriptionStatus.ACTIVE)).thenReturn(true);

        assertThrows(SubscriptionAlreadyActiveException.class, () -> useCase.execute(command));
    }
}