package com.hawksxo.core_flow_pay.subscription.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Subscription {
    private final String id;
    private final String userId;
    private SubscriptionStatus status;
    private final String idempotencyKey;
    private final Instant createdAt;
    private Instant expiredAt;

    private Subscription(String id, String userId, SubscriptionStatus status, String idempotencyKey, Instant createdAt, Instant expiredAt) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public static Subscription create(String userId, String idempotencyKey, Instant now) {
        return new Subscription(
            UUID.randomUUID().toString(),
            userId,
            SubscriptionStatus.PENDING,
            idempotencyKey,
            now,
            null
        );
    }

    public static Subscription reconstruct(String id, String userId, SubscriptionStatus status, String idempotencyKey, Instant createdAt, Instant expiredAt) {
        return new Subscription(id, userId, status, idempotencyKey, createdAt, expiredAt);
    }

    public void activate(Instant now) {
        if (this.status != SubscriptionStatus.PENDING) {
            throw new InvalidSubscriptionStateTransitionException(this.id, this.status, SubscriptionStatus.ACTIVE);
        }
        this.status = SubscriptionStatus.ACTIVE;
        this.expiredAt = now.plusSeconds(30 * 24 * 60 * 60);
    }

    public void cancel() {
        if (this.status == SubscriptionStatus.CANCELLED) {
            return;
        }
        this.status = SubscriptionStatus.CANCELLED;
    }

    public boolean isExpired(Instant now) {
        return this.expiredAt != null && now.isAfter(this.expiredAt);
    }

    public void checkAndExpire(Instant now) {
        if (isExpired(now) && this.status == SubscriptionStatus.ACTIVE) {
            this.status = SubscriptionStatus.EXPIRED;
        }
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiredAt() {
        return expiredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}