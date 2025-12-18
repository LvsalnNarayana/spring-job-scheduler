package com.example.job_execution_service.repository;

import com.example.job_execution_service.entity.JobLockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JobLockRepository
        extends JpaRepository<JobLockEntity, UUID> {

    Optional<JobLockEntity> findByJobScheduleId(UUID jobScheduleId);

    void deleteByJobScheduleId(UUID jobScheduleId);
}
