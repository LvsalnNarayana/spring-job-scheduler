package com.example.subscription_service.controller;

import com.example.subscription_service.entity.SubscriptionEntity;
import com.example.subscription_service.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /* =========================================================
       =============== INTERNAL (SCHEDULER) APIs =================
       ========================================================= */

    @PostMapping("/internal/subscriptions/process-expiry")
    public ResponseEntity<List<SubscriptionEntity>> processExpiry(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return ResponseEntity.ok(
                subscriptionService.processExpiringSubscriptions(date)
        );
    }

    @PostMapping("/internal/subscriptions/retry-payments")
    public ResponseEntity<List<SubscriptionEntity>> retryFailedPayments() {
        return ResponseEntity.ok(
                subscriptionService.retryFailedPayments()
        );
    }

    @GetMapping("/internal/subscriptions/expiring-between")
    public ResponseEntity<List<SubscriptionEntity>> findExpiringBetween(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return ResponseEntity.ok(
                subscriptionService.findSubscriptionsExpiringBetween(startDate, endDate)
        );
    }

    @PostMapping("/internal/subscriptions/cleanup-expired")
    public ResponseEntity<List<SubscriptionEntity>> cleanupExpired(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate cutoffDate
    ) {
        return ResponseEntity.ok(
                subscriptionService.cleanupExpiredSubscriptions(cutoffDate)
        );
    }

    /* =========================================================
       ===================== PUBLIC CRUD APIs ====================
       ========================================================= */

    /**
     * Create a new subscription
     */
    @PostMapping("/subscriptions")
    public ResponseEntity<SubscriptionEntity> createSubscription(
            @RequestBody SubscriptionEntity subscription
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionService.createSubscription(subscription));
    }

    /**
     * Get subscription by ID
     */
    @GetMapping("/subscriptions/{id}")
    public ResponseEntity<SubscriptionEntity> getSubscriptionById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                subscriptionService.getSubscriptionById(id)
        );
    }

    /**
     * Get all subscriptions
     */
    @GetMapping("/subscriptions")
    public ResponseEntity<List<SubscriptionEntity>> getAllSubscriptions() {
        return ResponseEntity.ok(
                subscriptionService.getAllSubscriptions()
        );
    }

    /**
     * Update allowed subscription fields
     */
    @PutMapping("/subscriptions/{id}")
    public ResponseEntity<SubscriptionEntity> updateSubscription(
            @PathVariable UUID id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate expiryDate,
            @RequestParam(required = false)
            Boolean autoRenew
    ) {
        return ResponseEntity.ok(
                subscriptionService.updateSubscription(id, expiryDate, autoRenew)
        );
    }

    /**
     * Cancel subscription (soft delete)
     */
    @PostMapping("/subscriptions/{id}/cancel")
    public ResponseEntity<SubscriptionEntity> cancelSubscription(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                subscriptionService.cancelSubscription(id)
        );
    }
    /* =========================================================
   ===================== ACTION (TAPI) APIs ==================
   ========================================================= */

    /**
     * Manually trigger payment attempt (admin / debug / scheduler dry-run)
     */
    @PostMapping("/subscriptions/{id}/actions/try-payment")
    public ResponseEntity<SubscriptionEntity> tryPayment(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                subscriptionService.tryPayment(id)
        );
    }

    /**
     * Manually cancel subscription (explicit action, not CRUD delete)
     */
    @PostMapping("/subscriptions/{id}/actions/cancel")
    public ResponseEntity<SubscriptionEntity> cancelSubscriptionAction(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                subscriptionService.cancelSubscription(id)
        );
    }

    /**
     * Force-expire a subscription (used for ops, testing, or backfill jobs)
     */
    @PostMapping("/subscriptions/{id}/actions/expire")
    public ResponseEntity<SubscriptionEntity> expireSubscription(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                subscriptionService.forceExpire(id)
        );
    }

    /**
     * Manually trigger renewal (admin or scheduler replay)
     */
    @PostMapping("/subscriptions/{id}/actions/renew")
    public ResponseEntity<SubscriptionEntity> renewSubscription(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                subscriptionService.forceRenew(id)
        );
    }

}
