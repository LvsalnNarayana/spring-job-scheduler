package com.example.subscription_service.models;

import com.example.subscription_service.entity.SubscriptionEntity.SubscriptionStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private UUID id;

    private String userId;

    private SubscriptionStatus status;

    private LocalDate startDate;

    private LocalDate expiryDate;

    private Boolean autoRenew;

    private Integer retryCount;

    private Integer maxRetryAllowed;

    private Instant lastRetryAt;

    private Instant lastPaymentAttemptAt;

    private Instant createdAt;

    private Instant updatedAt;
}
