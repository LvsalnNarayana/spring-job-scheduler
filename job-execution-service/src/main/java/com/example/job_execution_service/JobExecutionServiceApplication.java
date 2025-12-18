package com.example.job_execution_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobExecutionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobExecutionServiceApplication.class, args);
	}

}
