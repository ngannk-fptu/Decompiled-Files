/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.guardrails;

import com.atlassian.migration.agent.store.guardrails.GuardrailsBrowserMetricsStore;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class BrowserMetricsCleanupScheduler
implements JobRunner {
    private final JobRunnerKey runnerKey = JobRunnerKey.of((String)"com.atlassian.jira.migration.guardrails.BrowserMetricsCleanupScheduler");
    private final JobId jobId = JobId.of((String)"browser-metrics-job-id");
    private final Logger log = LoggerFactory.getLogger(BrowserMetricsCleanupScheduler.class);
    private final GuardrailsBrowserMetricsStore browserMetricsRepository;
    private final SchedulerService schedulerService;

    public BrowserMetricsCleanupScheduler(GuardrailsBrowserMetricsStore browserMetricsRepository, SchedulerService schedulerService) {
        this.browserMetricsRepository = browserMetricsRepository;
        this.schedulerService = schedulerService;
    }

    @PostConstruct
    void postConstruct() throws SchedulerServiceException {
        this.schedulerService.registerJobRunner(this.runnerKey, (JobRunner)this);
        String everyDayMidnight = "0 0 0 * * ?";
        this.schedulerService.scheduleJob(this.jobId, JobConfig.forJobRunnerKey((JobRunnerKey)this.runnerKey).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.forCronExpression((String)everyDayMidnight)));
        this.log.info("Browser metrics cleanup job registered successfully.");
    }

    @PreDestroy
    void preDestroy() {
        this.schedulerService.unregisterJobRunner(this.runnerKey);
        this.log.info("Browser metrics cleanup job unregistered successfully.");
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        try {
            this.log.info("Running browser metrics cleanup job. Metrics older than two weeks will be deleted.");
            this.browserMetricsRepository.deleteStaleMetrics();
            return JobRunnerResponse.success((String)request.getJobId().toString());
        }
        catch (Throwable e) {
            this.log.error("Failed to run browser metrics cleanup job: ${e.message}", e);
            return JobRunnerResponse.failed((Throwable)e);
        }
    }
}

