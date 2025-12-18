package com.example.job_execution_service.entity;

public enum JobType {

    SUBSCRIPTION_EXPIRY,
    SUBSCRIPTION_RENEWAL,
    SUBSCRIPTION_PAYMENT_RETRY,
    SUBSCRIPTION_REMINDER,

    CLEANUP_EXPIRED_SUBSCRIPTIONS
}
