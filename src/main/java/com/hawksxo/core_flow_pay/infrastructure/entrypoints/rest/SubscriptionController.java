package com.hawksxo.core_flow_pay.infrastructure.entrypoints.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hawksxo.core_flow_pay.application.dtos.CreateSubscriptionCommand;
import com.hawksxo.core_flow_pay.application.usecases.ActivateSubscriptionUseCase;
import com.hawksxo.core_flow_pay.application.usecases.CancelSubscriptionUseCase;
import com.hawksxo.core_flow_pay.application.usecases.CreateSubscriptionUseCase;
import com.hawksxo.core_flow_pay.domain.models.Subscription;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {
    private final CreateSubscriptionUseCase createSubscriptionUseCase;
    private final ActivateSubscriptionUseCase activateSubscriptionUseCase;
    private final CancelSubscriptionUseCase cancelSubscriptionUseCase;

    public SubscriptionController(
            CreateSubscriptionUseCase createSubscriptionUseCase,
            ActivateSubscriptionUseCase activateSubscriptionUseCase,
            CancelSubscriptionUseCase cancelSubscriptionUseCase) {
        this.createSubscriptionUseCase = createSubscriptionUseCase;
        this.activateSubscriptionUseCase = activateSubscriptionUseCase;
        this.cancelSubscriptionUseCase = cancelSubscriptionUseCase;
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

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateSubscription(@PathVariable("id") String id) {
        activateSubscriptionUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelSubscription(@PathVariable("id") String id) {
        cancelSubscriptionUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
