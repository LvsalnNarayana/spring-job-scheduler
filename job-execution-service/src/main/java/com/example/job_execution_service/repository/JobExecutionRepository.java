package com.example.job_execution_service.repository;

import com.example.job_execution_service.entity.JobExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobExecutionRepository
        extends JpaRepository<JobExecutionEntity, UUID> {

}
