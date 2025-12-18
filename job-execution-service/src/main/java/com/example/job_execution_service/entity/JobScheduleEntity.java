package com.example.job_execution_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
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
     * Logical job identifier Example: SUBSCRIPTION_EXPIRY_JOB
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobType jobType;

    /**
     * Reference to domain entity Example: subscriptionId
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

    /**
     * Start of retry window (e.g. 15:00)
     */
    @Column(nullable = true)
    private Integer retryWindowStartHour;

    /**
     * End of retry window (e.g. 20:00)
     */
    @Column(nullable = true)
    private Integer retryWindowEndHour;

    /**
     * Gap between retries in minutes (e.g. 120 → 3pm→5pm→8pm)
     */
    @Column(nullable = true)
    private Integer retryIntervalMinutes;

    /**
     * Date for which this retry window applies (e.g. expiryDate.minusDays(1))
     */
    @Column(nullable = true)
    private LocalDate retryWindowDate;

}
