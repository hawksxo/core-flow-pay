package com.hawksxo.core_flow_pay.subscription.infrastructure.persistence;

import com.hawksxo.core_flow_pay.subscription.domain.Subscription;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionRepository;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;

public class JpaSubscriptionRepositoryAdapter implements SubscriptionRepository {
    private final SpringDataSubscriptionRepository jpaRepository;

    public JpaSubscriptionRepositoryAdapter(SpringDataSubscriptionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Subscription save(Subscription subscription) {
        SubscriptionEntity entity = toEntity(subscription);
        try {
            SubscriptionEntity saved = jpaRepository.saveAndFlush(entity);
            return toDomain(saved);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException cve
                    && cve.getConstraintName() != null
                    && cve.getConstraintName().toLowerCase().contains("idempotency_key")) {
                throw new com.hawksxo.core_flow_pay.subscription.domain.DuplicateIdempotencyKeyException(subscription.getIdempotencyKey());
            }
            throw e;
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
            entity.getStatus(),
            entity.getIdempotencyKey(),
            entity.getCreatedAt(),
            entity.getExpiredAt()
        );
    }
}