package com.hawksxo.core_flow_pay.domain.models;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.hawksxo.core_flow_pay.domain.exceptions.SubscriptionInvalidStateException;

class SubscriptionTest {

    @Test
    void shouldThrowWhenActivatingCancelledSubscription() {
        Subscription subscription = new Subscription("id", "user-1", "plan-1", "idem-1");
        subscription.cancel();

        assertThrows(SubscriptionInvalidStateException.class, subscription::activate);
    }
}
