package com.example.job_execution_service.repository;

import com.example.job_execution_service.entity.JobScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface JobScheduleRepository extends JpaRepository<JobScheduleEntity, UUID> {

    /**
     * Find enabled jobs that are due to run
     */
    @Query("""
        SELECT j FROM JobScheduleEntity j
        WHERE j.enabled = true
          AND (j.nextExecutionAt IS NULL OR j.nextExecutionAt <= :now)
    """)
    List<JobScheduleEntity> findDueJobs(Instant now);
}
