package com.example.job_execution_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "job_locks",
    uniqueConstraints = @UniqueConstraint(columnNames = "jobScheduleId")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobLockEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private UUID jobScheduleId;

    @Column(nullable = false)
    private String lockedBy;

    @Column(nullable = false)
    private Instant lockedAt;
}
