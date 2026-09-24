package com.hawksxo.core_flow_pay.subscription.domain;

public class SubscriptionInvalidStateException extends RuntimeException {
    public SubscriptionInvalidStateException(String message) {
        super(message);
    }
}
