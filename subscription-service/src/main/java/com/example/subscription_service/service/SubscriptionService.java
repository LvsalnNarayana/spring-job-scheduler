package com.example.subscription_service.service;

import com.example.subscription_service.entity.SubscriptionEntity;
import com.example.subscription_service.entity.SubscriptionEntity.SubscriptionStatus;
import com.example.subscription_service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    /* =========================================================
       =============== Scheduler-facing methods =================
       ========================================================= */

    public List<SubscriptionEntity> processExpiringSubscriptions(LocalDate today) {

        List<SubscriptionEntity> expiring = subscriptionRepository.findByExpiryDate(today);

        for (SubscriptionEntity subscription : expiring) {

            if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
                continue;
            }

            if (Boolean.TRUE.equals(subscription.getAutoRenew())) {
                attemptRenewal(subscription);
            } else {
                markExpired(subscription);
            }
        }

        return expiring;
    }

    public List<SubscriptionEntity> retryFailedPayments() {

        List<SubscriptionEntity> retryables =
                subscriptionRepository.findRetryEligibleSubscriptions(SubscriptionStatus.PAYMENT_FAILED);

        for (SubscriptionEntity subscription : retryables) {
            attemptPaymentRetry(subscription);
        }

        return retryables;
    }

    @Transactional(readOnly = true)
    public List<SubscriptionEntity> findSubscriptionsExpiringBetween(
            LocalDate startDate,
            LocalDate endDate
    ) {
        return subscriptionRepository.findByExpiryDateBetween(startDate, endDate);
    }

    public List<SubscriptionEntity> cleanupExpiredSubscriptions(LocalDate cutoffDate) {
        return subscriptionRepository.findByStatusAndExpiryDateBefore(
                SubscriptionStatus.EXPIRED,
                cutoffDate
        );
    }

    /* =========================================================
       ===================== CRUD METHODS =======================
       ========================================================= */

    /**
     * Create new subscription. Status is domain-controlled.
     */
    public SubscriptionEntity createSubscription(SubscriptionEntity subscription) {

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setRetryCount(0);

        return subscriptionRepository.save(subscription);
    }

    /**
     * Fetch subscription by ID.
     */
    @Transactional(readOnly = true)
    public SubscriptionEntity getSubscriptionById(UUID id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subscription not found: " + id
                ));
    }

    /**
     * Fetch all subscriptions.
     */
    @Transactional(readOnly = true)
    public List<SubscriptionEntity> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    /**
     * Update allowed subscription fields. Status cannot be arbitrarily updated.
     */
    public SubscriptionEntity updateSubscription(
            UUID id,
            LocalDate newExpiryDate,
            Boolean autoRenew
    ) {
        SubscriptionEntity subscription = getSubscriptionById(id);

        if (newExpiryDate != null) {
            subscription.setExpiryDate(newExpiryDate);
        }

        if (autoRenew != null) {
            subscription.setAutoRenew(autoRenew);
        }

        return subscription;
    }

    /**
     * Cancel subscription (soft delete).
     */
    public SubscriptionEntity cancelSubscription(UUID id) {
        SubscriptionEntity subscription = getSubscriptionById(id);
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        return subscription;
    }

    /* =========================================================
       ===================== Domain Logic =======================
       ========================================================= */
    public SubscriptionEntity tryPayment(UUID id) {
        SubscriptionEntity subscription = getSubscriptionById(id);

        if (subscription.getStatus() != SubscriptionStatus.PAYMENT_FAILED &&
                subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Payment retry not allowed for status: " + subscription.getStatus()
            );
        }

        attemptPaymentRetry(subscription);
        return subscription;
    }

    public SubscriptionEntity forceExpire(UUID id) {
        SubscriptionEntity subscription = getSubscriptionById(id);
        markExpired(subscription);
        return subscription;
    }

    public SubscriptionEntity forceRenew(UUID id) {
        SubscriptionEntity subscription = getSubscriptionById(id);

        if (!Boolean.TRUE.equals(subscription.getAutoRenew())) {
            throw new IllegalStateException("Auto-renew is disabled for this subscription");
        }

        attemptRenewal(subscription);
        return subscription;
    }

    private void attemptRenewal(SubscriptionEntity subscription) {

        boolean paymentSuccess = mockPaymentGateway();

        subscription.setLastPaymentAttemptAt(Instant.now());

        if (paymentSuccess) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setRetryCount(0);
            subscription.setExpiryDate(subscription.getExpiryDate().plusMonths(1));
        } else {
            subscription.setStatus(SubscriptionStatus.PAYMENT_FAILED);
            subscription.setRetryCount(subscription.getRetryCount() + 1);
            subscription.setLastRetryAt(Instant.now());
        }
    }

    private void attemptPaymentRetry(SubscriptionEntity subscription) {

        boolean paymentSuccess = mockPaymentGateway();

        subscription.setLastPaymentAttemptAt(Instant.now());

        if (paymentSuccess) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setRetryCount(0);
            subscription.setExpiryDate(subscription.getExpiryDate().plusMonths(1));
        } else {
            subscription.setRetryCount(subscription.getRetryCount() + 1);
            subscription.setLastRetryAt(Instant.now());

            if (subscription.getRetryCount() >= subscription.getMaxRetryAllowed()) {
                markExpired(subscription);
            }
        }
    }

    private void markExpired(SubscriptionEntity subscription) {
        subscription.setStatus(SubscriptionStatus.EXPIRED);
    }

    /**
     * Mock payment logic.
     */
    private boolean mockPaymentGateway() {
        return Math.random() > 0.3;
    }
}
