package com.example.subscription_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@EntityListeners(SubscriptionEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEntity {

    private static final int DEFAULT_RETRY_COUNT = 0;
    private static final int DEFAULT_MAX_RETRY = 3;

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private Boolean autoRenew;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(nullable = false)
    private Integer maxRetryAllowed;

    private Instant lastRetryAt;

    private Instant lastPaymentAttemptAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    /**
     * Explicit domain-safe initialization. No scheduler or service should ever worry about nulls.
     */
    public void initializeDefaults() {
        if (this.retryCount == null) {
            this.retryCount = DEFAULT_RETRY_COUNT;
        }
        if (this.maxRetryAllowed == null) {
            this.maxRetryAllowed = DEFAULT_MAX_RETRY;
        }
    }

    public enum SubscriptionStatus {

        ACTIVE,
        EXPIRING_SOON,
        PAYMENT_FAILED,
        EXPIRED,
        CANCELLED
    }
}
