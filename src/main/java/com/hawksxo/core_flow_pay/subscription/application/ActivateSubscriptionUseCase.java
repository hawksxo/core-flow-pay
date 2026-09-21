package com.hawksxo.core_flow_pay.subscription.application;

import com.hawksxo.core_flow_pay.subscription.domain.Subscription;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionRepository;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.Instant;

public class ActivateSubscriptionUseCase {
    private final SubscriptionRepository repository;
    private final Clock clock;

    public ActivateSubscriptionUseCase(SubscriptionRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public SubscriptionResult execute(String subscriptionId) {
        Subscription subscription = repository.findById(subscriptionId)
            .orElseThrow(() -> new com.hawksxo.core_flow_pay.subscription.domain.SubscriptionNotFoundException(subscriptionId));

        if (subscription.getStatus() != SubscriptionStatus.PENDING) {
            throw new com.hawksxo.core_flow_pay.subscription.domain.InvalidSubscriptionStateTransitionException(subscriptionId, subscription.getStatus(), SubscriptionStatus.ACTIVE);
        }

        subscription.activate(clock.instant());
        Subscription saved = repository.save(subscription);
        return new SubscriptionResult(saved.getId(), saved.getUserId(), saved.getStatus(), saved.getIdempotencyKey(), saved.getCreatedAt(), saved.getExpiredAt());
    }

    public record SubscriptionResult(String id, String userId, SubscriptionStatus status, String idempotencyKey, Instant createdAt, Instant expiredAt) {
    }
}