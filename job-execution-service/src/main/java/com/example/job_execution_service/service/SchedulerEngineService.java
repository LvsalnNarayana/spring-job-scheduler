package com.example.job_execution_service.service;

import com.example.job_execution_service.entity.*;
import com.example.job_execution_service.repository.JobScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerEngineService {

    private final JobScheduleRepository scheduleRepository;
    private final JobExecutionService executionService;
    private final JobDispatcherService dispatcherService;
    private final JobScheduleService scheduleService;

    @Scheduled(fixedDelay = 60000) // 1 minute
    public void tick() {

        List<JobScheduleEntity> dueJobs =
                scheduleRepository.findDueJobs(Instant.now());

        for (JobScheduleEntity job : dueJobs) {

            JobExecutionEntity execution =
                    JobExecutionEntity.builder()
                            .jobScheduleId(job.getId())
                            .jobType(job.getJobType())
                            .referenceId(job.getReferenceId())
                            .attempt(job.getRetryCount() + 1)
                            .build();

            executionService.startExecution(execution);

            try {
                dispatcherService.dispatch(job);
                executionService.markSuccess(execution);

                scheduleService.updateNextExecution(
                        job,
                        Instant.now().plusSeconds(60) // placeholder
                );

            } catch (Exception ex) {
                executionService.markFailure(execution, ex.getMessage());
                scheduleService.incrementRetry(job);
            }
        }
    }
}
