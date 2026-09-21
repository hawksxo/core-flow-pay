package com.hawksxo.core_flow_pay.application.usecases;

import java.util.UUID;
import com.hawksxo.core_flow_pay.application.dtos.CreateSubscriptionCommand;
import com.hawksxo.core_flow_pay.domain.exceptions.DuplicateIdempotencyKeyException;
import com.hawksxo.core_flow_pay.domain.exceptions.SubscriptionAlreadyActiveException;
import com.hawksxo.core_flow_pay.domain.interfaces.SubscriptionRepository;
import com.hawksxo.core_flow_pay.domain.models.Subscription;

public class CreateSubscriptionUseCase {

    private final SubscriptionRepository repository;

    public CreateSubscriptionUseCase(SubscriptionRepository repository) {
        this.repository = repository;
    }

    public Subscription execute(CreateSubscriptionCommand command) {

        if (repository.findByIdempotencyKey(command.idempotencyKey()).isPresent()) {
            throw new DuplicateIdempotencyKeyException("Solicitud duplicada. La clave de idempotencia ya fue procesada.");
        }

        if (repository.existsActiveSubscriptionByUserId(command.userId())) {
            throw new SubscriptionAlreadyActiveException("El usuario ya tiene una subscripción activa");
        }

        Subscription subscription = new Subscription(UUID.randomUUID().toString(), command.userId(), command.planId(),
                command.idempotencyKey());

        repository.save(subscription);

        return subscription;

    }

}
