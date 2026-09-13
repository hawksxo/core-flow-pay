package com.hawksxo.core_flow_pay.infrastructure.adapters.persistence;

import java.util.Optional;
import org.springframework.stereotype.Repository;

import com.hawksxo.core_flow_pay.domain.interfaces.SubscriptionRepository;
import com.hawksxo.core_flow_pay.domain.models.Subscription;
import com.hawksxo.core_flow_pay.domain.models.SubscriptionStatus;

@Repository
public class JpaSubscriptionRepositoryAdapter implements SubscriptionRepository {

    private final SpringDataSubscriptionRepository springDataRepository;

    public JpaSubscriptionRepositoryAdapter(SpringDataSubscriptionRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionEntity entity = toEntity(subscription);
        SubscriptionEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Subscription> findById(String id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Subscription> findByIdempotencyKey(String idempotencyKey) {
        return springDataRepository.findByIdempotencyKey(idempotencyKey).map(this::toDomain);
    }

    @Override
    public boolean existsActiveSubscriptionByUserId(String userId) {
        return springDataRepository.existsByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
    }

    private SubscriptionEntity toEntity(Subscription domain) {
        return new SubscriptionEntity(
            domain.getId(), 
            domain.getUserId(), 
            domain.getPlanId(), 
            domain.getStatus(), 
            domain.getIdempotencyKey(),
            domain.getCreatedAt(),
            domain.getExpiresAt()
        );
    }

    private Subscription toDomain(SubscriptionEntity entity) {
        Subscription subscription = new Subscription(entity.getId(), entity.getUserId(), entity.getPlanId(), entity.getIdempotencyKey());

        if (entity.getStatus() == SubscriptionStatus.ACTIVE) {
            subscription.activate();
        } else if (entity.getStatus() == SubscriptionStatus.CANCELLED) {
            subscription.cancel();
        }

        return subscription;
    }
}
