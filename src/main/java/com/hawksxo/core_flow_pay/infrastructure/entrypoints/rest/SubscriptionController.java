package com.hawksxo.core_flow_pay.infrastructure.entrypoints.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hawksxo.core_flow_pay.application.dtos.CreateSubscriptionCommand;
import com.hawksxo.core_flow_pay.application.usecases.CreateSubscriptionUseCase;
import com.hawksxo.core_flow_pay.domain.models.Subscription;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {
    private final CreateSubscriptionUseCase createSubscriptionUseCase;

    public SubscriptionController(CreateSubscriptionUseCase createSubscriptionUseCase) {
        this.createSubscriptionUseCase = createSubscriptionUseCase;
    }

    public record CreateSubscriptionRequest(String userId, String planId) {}

    @PostMapping
    public ResponseEntity<Subscription> createSubscription(
            @RequestBody CreateSubscriptionRequest request,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey) {

                CreateSubscriptionCommand command = new CreateSubscriptionCommand(
                request.userId(),
                request.planId(),
                idempotencyKey
        );

        Subscription subscription = createSubscriptionUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }
    
}
