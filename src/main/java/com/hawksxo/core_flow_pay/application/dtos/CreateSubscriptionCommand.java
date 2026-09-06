package com.hawksxo.core_flow_pay.application.dtos;

public record CreateSubscriptionCommand(String userId, String planId, String idempotencyKey) {}
