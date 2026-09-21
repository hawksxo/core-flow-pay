package com.hawksxo.core_flow_pay.subscription.domain;

public class SubscriptionNotFoundException extends RuntimeException {
    public SubscriptionNotFoundException(String subscriptionId) {
        super("Subscription with id '%s' not found".formatted(subscriptionId));
    }
}