package com.hawksxo.core_flow_pay.subscription.api;

import com.hawksxo.core_flow_pay.subscription.application.ActivateSubscriptionUseCase;
import com.hawksxo.core_flow_pay.subscription.application.CancelSubscriptionUseCase;
import com.hawksxo.core_flow_pay.subscription.application.CreateSubscriptionCommand;
import com.hawksxo.core_flow_pay.subscription.application.CreateSubscriptionUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscriptions")
@Validated
public class SubscriptionController {
    private final CreateSubscriptionUseCase createUseCase;
    private final ActivateSubscriptionUseCase activateUseCase;
    private final CancelSubscriptionUseCase cancelUseCase;

    public SubscriptionController(CreateSubscriptionUseCase createUseCase, ActivateSubscriptionUseCase activateUseCase, CancelSubscriptionUseCase cancelUseCase) {
        this.createUseCase = createUseCase;
        this.activateUseCase = activateUseCase;
        this.cancelUseCase = cancelUseCase;
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(
            @RequestHeader("Idempotency-Key") @NotBlank String idempotencyKey,
            @Valid @RequestBody CreateSubscriptionRequest request) {
        CreateSubscriptionCommand command = new CreateSubscriptionCommand(request.userId(), request.planId(), idempotencyKey);
        var result = createUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(SubscriptionResponse.from(result));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<SubscriptionResponse> activate(@PathVariable String id) {
        var result = activateUseCase.execute(id);
        return ResponseEntity.ok(SubscriptionResponse.from(result));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<SubscriptionResponse> cancel(@PathVariable String id) {
        var result = cancelUseCase.execute(id);
        return ResponseEntity.ok(SubscriptionResponse.from(result));
    }

    public record CreateSubscriptionRequest(@NotBlank String userId, @NotBlank String planId) {}
}