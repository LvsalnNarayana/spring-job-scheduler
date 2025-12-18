package com.example.subscription_service.repository;

import com.example.subscription_service.entity.SubscriptionEntity;
import com.example.subscription_service.entity.SubscriptionEntity.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, UUID> {

    /**
     * Used by: Daily Expiry Processor Job Finds subscriptions expiring today.
     */
    List<SubscriptionEntity> findByExpiryDate(LocalDate expiryDate);

    /**
     * Used by: Reminder Notification Job Finds subscriptions expiring within a date range.
     */
    List<SubscriptionEntity> findByExpiryDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Used by: Payment Retry Job Finds subscriptions eligible for retry.
     */
    @Query("""
                SELECT s
                FROM SubscriptionEntity s
                WHERE s.status = :status
                  AND s.retryCount < s.maxRetryAllowed
            """)
    List<SubscriptionEntity> findRetryEligibleSubscriptions(
            @Param("status") SubscriptionStatus status
    );

    /**
     * Used by: Backoff-aware Retry Job Prevents hammering payment gateways.
     */
    @Query("""
                SELECT s
                FROM SubscriptionEntity s
                WHERE s.status = :status
                  AND s.retryCount < s.maxRetryAllowed
                  AND (s.lastRetryAt IS NULL OR s.lastRetryAt < :cutoffTime)
            """)
    List<SubscriptionEntity> findRetryEligibleWithBackoff(
            @Param("status") SubscriptionStatus status,
            @Param("cutoffTime") Instant cutoffTime
    );

    /**
     * Used by: Cleanup / Archival Job Finds subscriptions expired before a cutoff date.
     */
    List<SubscriptionEntity> findByStatusAndExpiryDateBefore(
            SubscriptionStatus status,
            LocalDate cutoffDate
    );
}
