package com.hawksxo.core_flow_pay.domain.interfaces;

import com.hawksxo.core_flow_pay.domain.models.Subscription;
import java.util.Optional;

public interface SubscriptionRepository {
    void save(Subscription subscription);
    Optional<Subscription> findById(String id);
    Optional<Subscription> findByIdempotencyKey(String idempotencyKey);
    boolean existsActiveSubscriptionByUserId(String userId);
}
