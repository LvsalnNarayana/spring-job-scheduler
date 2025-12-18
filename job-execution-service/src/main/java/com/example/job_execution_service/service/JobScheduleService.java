package com.example.job_execution_service.service;

import com.example.job_execution_service.entity.JobScheduleEntity;
import com.example.job_execution_service.repository.JobScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class JobScheduleService {

    private final JobScheduleRepository repository;

    public JobScheduleEntity create(JobScheduleEntity schedule) {
        schedule.setEnabled(true);
        schedule.setRetryCount(0);
        schedule.setCreatedAt(Instant.now());
        schedule.setUpdatedAt(Instant.now());
        return repository.save(schedule);
    }

    public void updateNextExecution(
            JobScheduleEntity schedule,
            Instant nextExecutionAt
    ) {
        schedule.setLastExecutedAt(Instant.now());
        schedule.setNextExecutionAt(nextExecutionAt);
        schedule.setUpdatedAt(Instant.now());
    }

    public void incrementRetry(JobScheduleEntity schedule) {
        schedule.setRetryCount(schedule.getRetryCount() + 1);
        schedule.setUpdatedAt(Instant.now());
    }

    public void disable(UUID id) {
        repository.findById(id).ifPresent(j -> j.setEnabled(false));
    }
}
