package com.example.job_execution_service.service;

import com.example.job_execution_service.entity.JobExecutionEntity;
import com.example.job_execution_service.entity.JobExecutionStatus;
import com.example.job_execution_service.repository.JobExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JobExecutionService {

    private final JobExecutionRepository repository;

    public JobExecutionEntity startExecution(JobExecutionEntity execution) {
        execution.setStatus(JobExecutionStatus.STARTED);
        execution.setStartedAt(Instant.now());
        return repository.save(execution);
    }

    public void markSuccess(JobExecutionEntity execution) {
        execution.setStatus(JobExecutionStatus.SUCCESS);
        execution.setFinishedAt(Instant.now());
        repository.save(execution);
    }

    public void markFailure(JobExecutionEntity execution, String error) {
        execution.setStatus(JobExecutionStatus.FAILED);
        execution.setErrorMessage(error);
        execution.setFinishedAt(Instant.now());
        repository.save(execution);
    }
}
