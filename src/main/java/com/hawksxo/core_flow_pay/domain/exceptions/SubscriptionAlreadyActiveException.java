package com.hawksxo.core_flow_pay.domain.exceptions;

public class SubscriptionAlreadyActiveException extends RuntimeException {
    public SubscriptionAlreadyActiveException(String message) {
        super(message);
    }
}
