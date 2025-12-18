package com.example.job_execution_service.service;

import com.example.job_execution_service.entity.JobExecutionEntity;
import com.example.job_execution_service.entity.JobScheduleEntity;
import com.example.job_execution_service.repository.JobScheduleRepository;
import com.example.job_execution_service.utils.CronUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerEngineService {

    private final JobScheduleRepository scheduleRepository;
    private final JobExecutionService executionService;
    private final JobDispatcherService dispatcherService;
    private final JobScheduleService scheduleService;
    private final JobLockService jobLockService;

    /**
     * Unique ID for this running instance
     */
    private final String instanceId = UUID.randomUUID().toString();

    /**
     * Scheduler heartbeat Runs frequently, executes DB-driven jobs
     */
    @Scheduled(fixedDelay = 60000) // 1 minute
    public void tick() {
        log.info("hello");
        Instant now = Instant.now();

        List<JobScheduleEntity> dueJobs =
                scheduleRepository.findDueJobs(now);

        for (JobScheduleEntity job : dueJobs) {

            // 1️⃣ Skip disabled jobs
            if (!Boolean.TRUE.equals(job.getEnabled())) {
                continue;
            }

            // 2️⃣ Retry limit reached → disable job
            if (job.getRetryCount() >= job.getMaxRetry()) {
                scheduleService.disable(job.getId());
                continue;
            }

            // 3️⃣ Try DB lock (distributed safety)
            boolean locked =
                    jobLockService.tryLock(job.getId(), instanceId);

            if (!locked) {
                continue; // another instance owns it
            }

            JobExecutionEntity execution = null;

            try {
                // 4️⃣ Create execution record (STARTED)
                execution = executionService.startExecution(
                        JobExecutionEntity.builder()
                                .jobScheduleId(job.getId())
                                .jobType(job.getJobType())
                                .referenceId(job.getReferenceId())
                                .attempt(job.getRetryCount() + 1)
                                .build()
                );

                // 5️⃣ Dispatch actual job
                dispatcherService.dispatch(job);

                // 6️⃣ Mark execution success
                executionService.markSuccess(execution);
                log.info("marked success");

                // 7️⃣ Calculate next run using CRON
                Instant nextRun =
                        CronUtils.nextExecution(
                                job.getCronExpression(),
                                now
                        );

                // 8️⃣ Reset retry + update schedule
                scheduleService.onSuccess(job, nextRun);

            } catch (Exception ex) {

                // 9️⃣ Mark execution failed
                if (execution != null) {
                    executionService.markFailure(
                            execution,
                            ex.getMessage()
                    );
                }

                // 🔟 Increment retry & reschedule
                scheduleService.onFailure(job, now);

            } finally {
                // 11️⃣ Always release lock
                jobLockService.releaseLock(job.getId());
            }
        }
    }
}
