package com.hawksxo.core_flow_pay.subscription.application;

import com.hawksxo.core_flow_pay.subscription.domain.Subscription;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionNotFoundException;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionRepository;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

public class CancelSubscriptionUseCase {
    private final SubscriptionRepository repository;

    public CancelSubscriptionUseCase(SubscriptionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SubscriptionResult execute(String subscriptionId) {
        Subscription subscription = repository.findById(subscriptionId)
            .orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId));

        subscription.cancel();
        Subscription saved = repository.save(subscription);
        return new SubscriptionResult(saved.getId(), saved.getUserId(), saved.getPlanId(), saved.getStatus(), saved.getIdempotencyKey(), saved.getCreatedAt(), saved.getExpiredAt());
    }

    public record SubscriptionResult(String id, String userId, String planId, SubscriptionStatus status, String idempotencyKey, Instant createdAt, Instant expiredAt) {
    }
}