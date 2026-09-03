package com.hawksxo.core_flow_pay.domain.models;

import java.time.LocalDateTime;

public class Subscription {
    private String id;
    private String userId;
    private String planId;
    private SubscriptionStatus status;
    private String idempotencyKey;
    private LocalDateTime createdAt;
    private LocalDateTime expiresdAt;

    public Subscription(String id, String userId, String planId, String idempotencyKey) {
        this.id = id;
        this.userId = userId;
        this.planId = planId;
        this.status = SubscriptionStatus.PENDING;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = LocalDateTime.now();
        this.expiresdAt = this.createdAt.plusDays(30);
    }

    public void activate() {
        if (this.status == SubscriptionStatus.CANCELLED) {
            throw new IllegalStateException("No se puede activar una suscripción cancelada.");
        }
        this.status = SubscriptionStatus.ACTIVE;
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getPlanId() {
        return planId;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresdAt;
    }
}
