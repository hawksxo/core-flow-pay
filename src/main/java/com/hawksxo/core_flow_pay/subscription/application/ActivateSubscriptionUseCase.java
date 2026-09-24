package com.hawksxo.core_flow_pay.subscription.application;

import com.hawksxo.core_flow_pay.subscription.domain.Subscription;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionNotFoundException;
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
            .orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId));

        subscription.activate(clock.instant());
        Subscription saved = repository.save(subscription);
        return new SubscriptionResult(saved.getId(), saved.getUserId(), saved.getPlanId(), saved.getStatus(), saved.getIdempotencyKey(), saved.getCreatedAt(), saved.getExpiredAt());
    }

    public record SubscriptionResult(String id, String userId, String planId, SubscriptionStatus status, String idempotencyKey, Instant createdAt, Instant expiredAt) {
    }
}