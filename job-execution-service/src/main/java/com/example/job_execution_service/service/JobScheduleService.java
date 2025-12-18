package com.example.job_execution_service.service;

import com.example.job_execution_service.entity.JobScheduleEntity;
import com.example.job_execution_service.repository.JobScheduleRepository;
import com.example.job_execution_service.utils.CronUtils;
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

        Instant nextRun =
                CronUtils.nextExecution(
                        schedule.getCronExpression(),
                        Instant.now()
                );
        schedule.setNextExecutionAt(nextRun);

        return repository.save(schedule);
    }

    public void onSuccess(
            JobScheduleEntity job,
            Instant nextExecutionAt
    ) {
        job.setRetryCount(0);
        job.setLastExecutedAt(Instant.now());
        job.setNextExecutionAt(nextExecutionAt);
        job.setUpdatedAt(Instant.now());
    }

    public void onFailure(
            JobScheduleEntity job,
            Instant now
    ) {
        job.setRetryCount(job.getRetryCount() + 1);
        job.setLastExecutedAt(now);

        Instant nextRun =
                CronUtils.nextExecution(
                        job.getCronExpression(),
                        now
                );
        job.setNextExecutionAt(nextRun);

        job.setUpdatedAt(Instant.now());
    }

    /**
     * 🔑 Used when business logic decides the next run time (e.g. payment retry windows: 3pm → 5pm → 8pm)
     */
    public void overrideNextExecution(
            JobScheduleEntity job,
            Instant nextExecutionAt
    ) {
        job.setNextExecutionAt(nextExecutionAt);
        job.setLastExecutedAt(Instant.now());
        job.setUpdatedAt(Instant.now());
    }

    public void disable(UUID id) {
        repository.findById(id).ifPresent(job -> {
            job.setEnabled(false);
            job.setUpdatedAt(Instant.now());
        });
    }
}
