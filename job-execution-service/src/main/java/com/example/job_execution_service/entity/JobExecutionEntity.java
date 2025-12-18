package com.example.job_execution_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "job_executions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobExecutionEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * Link to job schedule
     */
    @Column(nullable = false)
    private UUID jobScheduleId;

    /**
     * Logical job type snapshot
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    /**
     * Domain reference snapshot
     */
    @Column(nullable = false)
    private UUID referenceId;

    /**
     * Execution lifecycle
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobExecutionStatus status;

    /**
     * Execution timestamps
     */
    @Column(nullable = false)
    private Instant startedAt;

    private Instant finishedAt;

    /**
     * Error details (if failed)
     */
    @Column(length = 4000)
    private String errorMessage;

    /**
     * Retry attempt number
     */
    @Column(nullable = false)
    private Integer attempt;
}
