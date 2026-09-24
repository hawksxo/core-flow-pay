package com.hawksxo.core_flow_pay.subscription.infrastructure.persistence;

import com.hawksxo.core_flow_pay.subscription.domain.DuplicateIdempotencyKeyException;
import com.hawksxo.core_flow_pay.subscription.domain.Subscription;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionRepository;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Optional;

public class JpaSubscriptionRepositoryAdapter implements SubscriptionRepository {
    private final SpringDataSubscriptionRepository jpaRepository;

    public JpaSubscriptionRepositoryAdapter(SpringDataSubscriptionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionEntity entity = toEntity(subscription);
        try {
            SubscriptionEntity saved = jpaRepository.saveAndFlush(entity);
            return toDomain(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateIdempotencyKeyException(subscription.getIdempotencyKey());
        }
    }

    @Override
    public Optional<Subscription> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Subscription> findByIdempotencyKey(String idempotencyKey) {
        return jpaRepository.findByIdempotencyKey(idempotencyKey).map(this::toDomain);
    }

    @Override
    public boolean existsByUserIdAndStatus(String userId, SubscriptionStatus status) {
        return jpaRepository.existsByUserIdAndStatus(userId, status);
    }

    private SubscriptionEntity toEntity(Subscription domain) {
        return new SubscriptionEntity(
            domain.getId(),
            domain.getUserId(),
            domain.getPlanId(),
            domain.getStatus(),
            domain.getIdempotencyKey(),
            domain.getCreatedAt(),
            domain.getExpiredAt()
        );
    }

    private Subscription toDomain(SubscriptionEntity entity) {
        return Subscription.reconstruct(
            entity.getId(),
            entity.getUserId(),
            entity.getPlanId(),
            entity.getStatus(),
            entity.getIdempotencyKey(),
            entity.getCreatedAt(),
            entity.getExpiredAt()
        );
    }
}