package com.hawksxo.core_flow_pay.subscription.domain;

import java.util.Optional;

public interface SubscriptionRepository {
    Subscription save(Subscription subscription);

    Optional<Subscription> findById(String id);

    Optional<Subscription> findByIdempotencyKey(String idempotencyKey);

    boolean existsByUserIdAndStatus(String userId, SubscriptionStatus status);
}