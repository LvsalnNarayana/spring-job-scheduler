package com.example.subscription_service.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.Instant;

public class SubscriptionEntityListener {

    @PrePersist
    public void prePersist(SubscriptionEntity entity) {
        Instant now = Instant.now();

        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        entity.initializeDefaults();
    }

    @PreUpdate
    public void preUpdate(SubscriptionEntity entity) {
        entity.setUpdatedAt(Instant.now());
    }
}
