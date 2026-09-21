package com.hawksxo.core_flow_pay.subscription.api;

import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import java.time.Instant;

public record SubscriptionResponse(
    String id,
    String userId,
    SubscriptionStatus status,
    String idempotencyKey,
    Instant createdAt,
    Instant expiredAt
) {
    public static SubscriptionResponse from(com.hawksxo.core_flow_pay.subscription.application.CreateSubscriptionUseCase.SubscriptionResult result) {
        return new SubscriptionResponse(result.id(), result.userId(), result.status(), result.idempotencyKey(), result.createdAt(), result.expiredAt());
    }

    public static SubscriptionResponse from(com.hawksxo.core_flow_pay.subscription.application.ActivateSubscriptionUseCase.SubscriptionResult result) {
        return new SubscriptionResponse(result.id(), result.userId(), result.status(), result.idempotencyKey(), result.createdAt(), result.expiredAt());
    }

    public static SubscriptionResponse from(com.hawksxo.core_flow_pay.subscription.application.CancelSubscriptionUseCase.SubscriptionResult result) {
        return new SubscriptionResponse(result.id(), result.userId(), result.status(), result.idempotencyKey(), result.createdAt(), result.expiredAt());
    }
}