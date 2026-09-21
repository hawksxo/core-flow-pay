package com.hawksxo.core_flow_pay.infrastructure.adapters.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hawksxo.core_flow_pay.domain.models.SubscriptionStatus;


public interface SpringDataSubscriptionRepository extends JpaRepository<SubscriptionEntity, String> {
    Optional<SubscriptionEntity> findByIdempotencyKey(String idempotencyKey);
    boolean existsByUserIdAndStatus(String userId, SubscriptionStatus status);
}
