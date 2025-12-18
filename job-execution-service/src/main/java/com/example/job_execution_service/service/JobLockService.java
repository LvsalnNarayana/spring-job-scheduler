package com.example.job_execution_service.service;

import com.example.job_execution_service.entity.JobLockEntity;
import com.example.job_execution_service.repository.JobLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobLockService {

    private final JobLockRepository repository;

    public boolean tryLock(UUID jobScheduleId, String instanceId) {
        try {
            repository.save(
                JobLockEntity.builder()
                    .jobScheduleId(jobScheduleId)
                    .lockedBy(instanceId)
                    .lockedAt(Instant.now())
                    .build()
            );
            return true;
        } catch (Exception ex) {
            // unique constraint violation → already locked
            return false;
        }
    }

    public void releaseLock(UUID jobScheduleId) {
        repository.deleteByJobScheduleId(jobScheduleId);
    }
}
