package com.example.job_execution_service.service;

import com.example.job_execution_service.entity.JobScheduleEntity;
import com.example.job_execution_service.entity.JobType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobDispatcherService {

    private final JobScheduleService scheduleService;

    public void dispatch(JobScheduleEntity job) {

        JobType type = job.getJobType();

        log.info(
                "[DISPATCH] jobId={}, type={}, referenceId={}",
                job.getId(),
                type,
                job.getReferenceId()
        );

        switch (type) {
            case SUBSCRIPTION_EXPIRY ->
                    callSubscriptionExpiry(job);

            case SUBSCRIPTION_PAYMENT_RETRY ->
                    callPaymentRetry(job);

            case SUBSCRIPTION_REMINDER ->
                    callReminder(job);

            default ->
                    throw new IllegalStateException("Unsupported job type: " + type);
        }
    }

    /**
     * Subscription expiry is a one-shot business action
     */
    private void callSubscriptionExpiry(JobScheduleEntity job) {
        log.info(
                "[JOB] SUBSCRIPTION_EXPIRY triggered for subscriptionId={}",
                job.getReferenceId()
        );

        // Later: REST call to Subscription Service
        scheduleService.disable(job.getId());
    }

    /**
     * Payment retry with retry window logic
     */
    private void callPaymentRetry(JobScheduleEntity job) {

        Instant now = Instant.now();
        LocalTime currentTime =
                now.atZone(ZoneId.systemDefault()).toLocalTime();

        LocalTime windowStart =
                LocalTime.of(job.getRetryWindowStartHour(), 0);

        LocalTime windowEnd =
                LocalTime.of(job.getRetryWindowEndHour(), 0);

        // 1️⃣ Check retry window
        if (currentTime.isBefore(windowStart) || currentTime.isAfter(windowEnd)) {
            log.info(
                    "[PAYMENT_RETRY] Outside retry window. Disabling job. subscriptionId={}",
                    job.getReferenceId()
            );
            scheduleService.disable(job.getId());
            return;
        }

        // 2️⃣ Retry limit check
        if (job.getRetryCount() >= job.getMaxRetry()) {
            log.info(
                    "[PAYMENT_RETRY] Retry limit reached. Disabling job. subscriptionId={}",
                    job.getReferenceId()
            );
            scheduleService.disable(job.getId());
            return;
        }

        // 3️⃣ Attempt payment (mock)
        boolean paymentSuccess = mockPayment();

        if (paymentSuccess) {
            log.info(
                    "[PAYMENT_RETRY] Payment SUCCESS for subscriptionId={}",
                    job.getReferenceId()
            );
            scheduleService.disable(job.getId());
            return;
        }

        // 4️⃣ Schedule next retry
        Instant nextRetry =
                now.plusSeconds(job.getRetryIntervalMinutes() * 60L);

        log.info(
                "[PAYMENT_RETRY] Payment FAILED. Next retry at {} for subscriptionId={}",
                nextRetry,
                job.getReferenceId()
        );

        scheduleService.overrideNextExecution(job, nextRetry);
    }

    private void callReminder(JobScheduleEntity job) {
        log.info(
                "[JOB] SUBSCRIPTION_REMINDER triggered for subscriptionId={}",
                job.getReferenceId()
        );
    }

    private boolean mockPayment() {
        return Math.random() > 0.7;
    }
}
