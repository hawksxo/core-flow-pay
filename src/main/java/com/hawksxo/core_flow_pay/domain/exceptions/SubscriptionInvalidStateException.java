package com.hawksxo.core_flow_pay.domain.exceptions;

public class SubscriptionInvalidStateException extends RuntimeException {
    public SubscriptionInvalidStateException(String message) {
        super(message);
    }
}
