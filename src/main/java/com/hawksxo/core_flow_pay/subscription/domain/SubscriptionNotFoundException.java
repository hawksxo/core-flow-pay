package com.hawksxo.core_flow_pay.subscription.domain;

public class SubscriptionNotFoundException extends RuntimeException {
    public SubscriptionNotFoundException(String id) {
        super("Subscription with ID '" + id + "' was not found.");
    }
}