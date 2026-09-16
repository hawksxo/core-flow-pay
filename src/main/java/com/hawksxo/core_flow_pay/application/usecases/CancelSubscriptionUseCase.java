package com.hawksxo.core_flow_pay.application.usecases;

import com.hawksxo.core_flow_pay.domain.exceptions.SubscriptionNotFoundException;
import com.hawksxo.core_flow_pay.domain.interfaces.SubscriptionRepository;
import com.hawksxo.core_flow_pay.domain.models.Subscription;

public class CancelSubscriptionUseCase {
    private final SubscriptionRepository subscriptionRepository;

    public CancelSubscriptionUseCase(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public void execute(String subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new SubscriptionNotFoundException("No existe"));

        subscription.cancel();

        subscriptionRepository.save(subscription);
    }
}
