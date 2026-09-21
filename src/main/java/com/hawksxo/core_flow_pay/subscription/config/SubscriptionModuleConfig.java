package com.hawksxo.core_flow_pay.subscription.config;

import com.hawksxo.core_flow_pay.subscription.application.ActivateSubscriptionUseCase;
import com.hawksxo.core_flow_pay.subscription.application.CancelSubscriptionUseCase;
import com.hawksxo.core_flow_pay.subscription.application.CreateSubscriptionUseCase;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionRepository;
import com.hawksxo.core_flow_pay.subscription.infrastructure.persistence.JpaSubscriptionRepositoryAdapter;
import com.hawksxo.core_flow_pay.subscription.infrastructure.persistence.SpringDataSubscriptionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import java.time.Clock;

@Configuration
public class SubscriptionModuleConfig {
    @Bean
    @Primary
    public SubscriptionRepository subscriptionRepository(SpringDataSubscriptionRepository jpaRepository) {
        return new JpaSubscriptionRepositoryAdapter(jpaRepository);
    }

    @Bean
    public CreateSubscriptionUseCase createSubscriptionUseCase(SubscriptionRepository repository) {
        return new CreateSubscriptionUseCase(repository, Clock.systemUTC());
    }

    @Bean
    public ActivateSubscriptionUseCase activateSubscriptionUseCase(SubscriptionRepository repository) {
        return new ActivateSubscriptionUseCase(repository, Clock.systemUTC());
    }

    @Bean
    public CancelSubscriptionUseCase cancelSubscriptionUseCase(SubscriptionRepository repository) {
        return new CancelSubscriptionUseCase(repository);
    }
}