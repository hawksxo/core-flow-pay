package com.hawksxo.core_flow_pay.subscription.domain;

public class InvalidSubscriptionStateTransitionException extends RuntimeException {
    public InvalidSubscriptionStateTransitionException(String id, SubscriptionStatus currentStatus, SubscriptionStatus targetStatus) {
        super("Cannot transition subscription '" + id + "' from state " + currentStatus + " to " + targetStatus + ".");
    }
}