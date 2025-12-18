package com.example.job_execution_service.controller;

import com.example.job_execution_service.entity.JobExecutionEntity;
import com.example.job_execution_service.repository.JobExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job-executions")
@RequiredArgsConstructor
public class JobExecutionController {

    private final JobExecutionRepository repository;

    @GetMapping
    public List<JobExecutionEntity> findAll() {
        return repository.findAll();
    }
}
