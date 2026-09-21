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

class CreateSubscriptionUseCaseTest {
    private SubscriptionRepository repository;
    private Clock clock;
    private CreateSubscriptionUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(SubscriptionRepository.class);
        clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        useCase = new CreateSubscriptionUseCase(repository, clock);
    }

    @Test
    void execute_success_returnsCreatedSubscription() {
        when(repository.findByIdempotencyKey("idem-1")).thenReturn(Optional.empty());
        when(repository.existsByUserIdAndStatus("user-1", SubscriptionStatus.ACTIVE)).thenReturn(false);
        when(repository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(new CreateSubscriptionCommand("user-1", "idem-1"));

        assertEquals("user-1", result.userId());
        assertEquals("idem-1", result.idempotencyKey());
        assertEquals(SubscriptionStatus.PENDING, result.status());
        assertNotNull(result.id());
        assertNotNull(result.createdAt());
    }

    @Test
    void execute_duplicateIdempotencyKey_throwsException() {
        when(repository.findByIdempotencyKey("idem-1")).thenReturn(Optional.of(mock(Subscription.class)));

        assertThrows(com.hawksxo.core_flow_pay.subscription.domain.DuplicateIdempotencyKeyException.class,
            () -> useCase.execute(new CreateSubscriptionCommand("user-1", "idem-1")));
    }

    @Test
    void execute_userAlreadyActive_throwsException() {
        when(repository.findByIdempotencyKey("idem-1")).thenReturn(Optional.empty());
        when(repository.existsByUserIdAndStatus("user-1", SubscriptionStatus.ACTIVE)).thenReturn(true);

        assertThrows(com.hawksxo.core_flow_pay.subscription.domain.SubscriptionAlreadyActiveException.class,
            () -> useCase.execute(new CreateSubscriptionCommand("user-1", "idem-1")));
    }
}