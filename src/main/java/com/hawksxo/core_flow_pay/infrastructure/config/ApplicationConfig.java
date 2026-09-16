package com.hawksxo.core_flow_pay.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hawksxo.core_flow_pay.application.usecases.ActivateSubscriptionUseCase;
import com.hawksxo.core_flow_pay.application.usecases.CancelSubscriptionUseCase;
import com.hawksxo.core_flow_pay.application.usecases.CreateSubscriptionUseCase;
import com.hawksxo.core_flow_pay.domain.interfaces.SubscriptionRepository;

@Configuration
public class ApplicationConfig {

    @Bean
    public CreateSubscriptionUseCase createSubscriptionUseCase(SubscriptionRepository subscriptionRepository) {
        return new CreateSubscriptionUseCase(subscriptionRepository);
    }

    @Bean
    public ActivateSubscriptionUseCase activateSubscriptionUseCase(SubscriptionRepository subscriptionRepository) {
        return new ActivateSubscriptionUseCase(subscriptionRepository);
    }

    @Bean
    public CancelSubscriptionUseCase cancelSubscriptionUseCase(SubscriptionRepository subscriptionRepository) {
        return new CancelSubscriptionUseCase(subscriptionRepository);
    }
}
