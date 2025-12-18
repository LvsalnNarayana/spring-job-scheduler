package com.example.job_execution_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "job_schedules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobScheduleEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * Logical job identifier
     * Example: SUBSCRIPTION_EXPIRY_JOB
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobType jobType;

    /**
     * Reference to domain entity
     * Example: subscriptionId
     */
    @Column(nullable = false)
    private UUID referenceId;

    /**
     * Cron expression stored as data
     */
    @Column(nullable = false)
    private String cronExpression;

    /**
     * Whether scheduler should consider this job
     */
    @Column(nullable = false)
    private Boolean enabled;

    /**
     * Max retries allowed for this job
     */
    @Column(nullable = false)
    private Integer maxRetry;

    /**
     * Current retry count
     */
    @Column(nullable = false)
    private Integer retryCount;

    /**
     * Last execution timestamp
     */
    private Instant lastExecutedAt;

    /**
     * Next execution timestamp (optional but powerful)
     */
    private Instant nextExecutionAt;

    /**
     * Job creation time
     */
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Job update time
     */
    @Column(nullable = false)
    private Instant updatedAt;
}
