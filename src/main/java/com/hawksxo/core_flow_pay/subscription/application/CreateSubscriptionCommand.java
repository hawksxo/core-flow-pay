package com.hawksxo.core_flow_pay.subscription.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSubscriptionCommand(
    @NotNull @NotBlank String userId,
    @NotNull @NotBlank String idempotencyKey
) {
}