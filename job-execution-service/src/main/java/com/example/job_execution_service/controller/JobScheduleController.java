package com.example.job_execution_service.controller;

import com.example.job_execution_service.entity.JobScheduleEntity;
import com.example.job_execution_service.service.JobScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/job-schedules")
@RequiredArgsConstructor
public class JobScheduleController {

    private final JobScheduleService service;

    @PostMapping
    public ResponseEntity<JobScheduleEntity> create(
            @RequestBody JobScheduleEntity schedule
    ) {
        return ResponseEntity.ok(service.create(schedule));
    }

    @PostMapping("/{id}/disable")
    public ResponseEntity<Void> disable(@PathVariable String id) {
        service.disable(java.util.UUID.fromString(id));
        return ResponseEntity.ok().build();
    }
}
