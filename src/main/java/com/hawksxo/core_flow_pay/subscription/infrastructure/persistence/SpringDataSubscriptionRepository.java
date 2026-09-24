package com.hawksxo.core_flow_pay.subscription.infrastructure.persistence;

import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataSubscriptionRepository extends JpaRepository<SubscriptionEntity, String> {
    Optional<SubscriptionEntity> findByIdempotencyKey(String idempotencyKey);
    boolean existsByUserIdAndStatus(String userId, SubscriptionStatus status);
}