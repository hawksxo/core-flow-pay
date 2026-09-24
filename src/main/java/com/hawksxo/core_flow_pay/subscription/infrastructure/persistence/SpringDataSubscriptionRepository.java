package com.hawksxo.core_flow_pay.subscription.infrastructure.persistence;

import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SpringDataSubscriptionRepository extends JpaRepository<SubscriptionEntity, String> {
    Optional<SubscriptionEntity> findByIdempotencyKey(String idempotencyKey);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SubscriptionEntity s WHERE s.userId = :userId AND s.status = :status")
    boolean existsByUserIdAndStatus(@Param("userId") String userId, @Param("status") SubscriptionStatus status);
}