package com.hawksxo.core_flow_pay.subscription.domain;

public class SubscriptionAlreadyActiveException extends RuntimeException {
    public SubscriptionAlreadyActiveException(String userId) {
        super("User '%s' already has an active subscription".formatted(userId));
    }
}