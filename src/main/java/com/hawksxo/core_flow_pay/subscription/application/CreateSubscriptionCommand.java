package com.hawksxo.core_flow_pay.subscription.application;

public record CreateSubscriptionCommand(String userId, String planId, String idempotencyKey) {
}