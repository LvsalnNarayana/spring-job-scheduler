package com.example.job_execution_service.utils;

import org.springframework.scheduling.support.CronExpression;

import java.time.Instant;
import java.time.ZoneId;

public class CronUtils {

    private CronUtils() {
    }

    public static Instant nextExecution(String cron, Instant from) {
        CronExpression expression = CronExpression.parse(cron);
        return expression.next(from.atZone(ZoneId.systemDefault())).toInstant();
    }
}
