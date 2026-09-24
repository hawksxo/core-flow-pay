package com.hawksxo.core_flow_pay.subscription.infrastructure.persistence;

import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(name = "subscriptions", uniqueConstraints = @UniqueConstraint(name = "uk_subscriptions_idempotency_key", columnNames = "idempotency_key"))
public class SubscriptionEntity {
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SubscriptionStatus status;

    @Column(name = "idempotency_key", nullable = false, length = 64)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expired_at")
    private Instant expiredAt;

    protected SubscriptionEntity() {}

    public SubscriptionEntity(String id, String userId, SubscriptionStatus status, String idempotencyKey, Instant createdAt, Instant expiredAt) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getExpiredAt() { return expiredAt; }
    public void setExpiredAt(Instant expiredAt) { this.expiredAt = expiredAt; }
}