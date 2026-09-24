package com.hawksxo.core_flow_pay.subscription.domain;

public class InvalidSubscriptionStateTransitionException extends RuntimeException {
    public InvalidSubscriptionStateTransitionException(String subscriptionId, SubscriptionStatus currentStatus, SubscriptionStatus expectedStatus) {
        super("Subscription '%s' cannot be transitioned from %s to %s".formatted(subscriptionId, currentStatus, expectedStatus));
    }
}