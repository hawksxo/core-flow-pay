package com.hawksxo.core_flow_pay.infrastructure.adapters.persistence;

import java.time.LocalDateTime;
import com.hawksxo.core_flow_pay.domain.models.SubscriptionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "subscriptions")
public class SubscriptionEntity {
    @Id String id;
    @Column(name = "user_id", nullable = false) String userId;
    @Column(name = "plan_id", nullable = false) String planId;
    @Enumerated(EnumType.STRING) SubscriptionStatus status;
    @Column(name = "idempotency_key", unique = true, nullable = false) String idempotencyKey;
    LocalDateTime createdAt;
    LocalDateTime expiresAt;

    public SubscriptionEntity() {}

    public SubscriptionEntity(String id, String userId, String planId, SubscriptionStatus status, String idempotencyKey, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.planId = planId;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlanId() {
        return this.planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public SubscriptionStatus getStatus() {
        return this.status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public String getIdempotencyKey() {
        return this.idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return this.expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
