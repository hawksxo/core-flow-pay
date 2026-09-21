package com.hawksxo.core_flow_pay.subscription.application;

import com.hawksxo.core_flow_pay.subscription.domain.Subscription;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionRepository;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.Instant;

public class CreateSubscriptionUseCase {
    private final SubscriptionRepository repository;
    private final Clock clock;

    public CreateSubscriptionUseCase(SubscriptionRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public SubscriptionResult execute(CreateSubscriptionCommand command) {
        Instant now = clock.instant();

        if (repository.findByIdempotencyKey(command.idempotencyKey()).isPresent()) {
            throw new com.hawksxo.core_flow_pay.subscription.domain.DuplicateIdempotencyKeyException(command.idempotencyKey());
        }

        if (repository.existsByUserIdAndStatus(command.userId(), SubscriptionStatus.ACTIVE)) {
            throw new com.hawksxo.core_flow_pay.subscription.domain.SubscriptionAlreadyActiveException(command.userId());
        }

        Subscription subscription = Subscription.create(command.userId(), command.idempotencyKey(), now);
        Subscription saved = repository.save(subscription);
        return new SubscriptionResult(saved.getId(), saved.getUserId(), saved.getStatus(), saved.getIdempotencyKey(), saved.getCreatedAt(), saved.getExpiredAt());
    }

    public record SubscriptionResult(String id, String userId, SubscriptionStatus status, String idempotencyKey, Instant createdAt, Instant expiredAt) {
    }
}