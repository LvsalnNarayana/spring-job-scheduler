package com.example.job_execution_service.service;

import com.example.job_execution_service.entity.JobScheduleEntity;
import com.example.job_execution_service.entity.JobType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobDispatcherService {

    public void dispatch(JobScheduleEntity job) {

        JobType type = job.getJobType();

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

    private void callSubscriptionExpiry(JobScheduleEntity job) {
        // REST call to Subscription Service
    }

    private void callPaymentRetry(JobScheduleEntity job) {
        // REST call to Subscription Service
    }

    private void callReminder(JobScheduleEntity job) {
        // REST call to Notification Service
    }
}
