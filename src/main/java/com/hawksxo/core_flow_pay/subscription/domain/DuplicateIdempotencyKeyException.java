package com.hawksxo.core_flow_pay.subscription.domain;

public class DuplicateIdempotencyKeyException extends RuntimeException {
    public DuplicateIdempotencyKeyException(String idempotencyKey) {
        super("Subscription request with idempotency key '" + idempotencyKey + "' already exists.");
    }
}