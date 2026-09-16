package com.hawksxo.core_flow_pay.application.usecases;

import com.hawksxo.core_flow_pay.domain.exceptions.SubscriptionNotFoundException;
import com.hawksxo.core_flow_pay.domain.interfaces.SubscriptionRepository;
import com.hawksxo.core_flow_pay.domain.models.Subscription;

public class ActivateSubscriptionUseCase {
    private final SubscriptionRepository subscriptionRepository;

    public ActivateSubscriptionUseCase(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public void execute(String suscriptionId) {
        Subscription subscription = subscriptionRepository.findById(suscriptionId)
            .orElseThrow(() -> new SubscriptionNotFoundException("No existe"));

        subscription.activate();

        subscriptionRepository.save(subscription);
    }
}
