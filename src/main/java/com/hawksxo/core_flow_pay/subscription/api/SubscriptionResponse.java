package com.hawksxo.core_flow_pay.subscription.api;

import com.hawksxo.core_flow_pay.subscription.application.ActivateSubscriptionUseCase;
import com.hawksxo.core_flow_pay.subscription.application.CancelSubscriptionUseCase;
import com.hawksxo.core_flow_pay.subscription.application.CreateSubscriptionUseCase;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import java.time.Instant;

public record SubscriptionResponse(
    String id,
    String userId,
    String planId,
    SubscriptionStatus status,
    String idempotencyKey,
    Instant createdAt,
    Instant expiredAt
) {
    public static SubscriptionResponse from(CreateSubscriptionUseCase.SubscriptionResult result) {
        return new SubscriptionResponse(result.id(), result.userId(), result.planId(), result.status(), result.idempotencyKey(), result.createdAt(), result.expiredAt());
    }

    public static SubscriptionResponse from(ActivateSubscriptionUseCase.SubscriptionResult result) {
        return new SubscriptionResponse(result.id(), result.userId(), result.planId(), result.status(), result.idempotencyKey(), result.createdAt(), result.expiredAt());
    }

    public static SubscriptionResponse from(CancelSubscriptionUseCase.SubscriptionResult result) {
        return new SubscriptionResponse(result.id(), result.userId(), result.planId(), result.status(), result.idempotencyKey(), result.createdAt(), result.expiredAt());
    }
}