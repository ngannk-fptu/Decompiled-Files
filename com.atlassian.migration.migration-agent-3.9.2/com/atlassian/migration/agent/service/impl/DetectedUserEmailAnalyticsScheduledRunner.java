/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
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
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.service.impl.DetectedUserEmailAnalyticsService;
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
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DetectedUserEmailAnalyticsScheduledRunner
implements JobRunner {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DetectedUserEmailAnalyticsScheduledRunner.class);
    private static final String JOB_RUNNER_ID = "migration-plugin:detected-emails-weekly-runner";
    private static final JobId JOB_ID = JobId.of((String)"migration-plugin:detected-emails-weekly-job");
    private final SchedulerService schedulerService;
    private final DetectedUserEmailAnalyticsService detectedUserEmailAnalyticsService;
    private final MigrationAgentConfiguration configuration;
    private final Supplier<Instant> instantSupplier;

    public DetectedUserEmailAnalyticsScheduledRunner(SchedulerService schedulerService, DetectedUserEmailAnalyticsService detectedUserEmailAnalyticsService, MigrationAgentConfiguration configuration) {
        this(schedulerService, detectedUserEmailAnalyticsService, configuration, Instant::now);
    }

    @VisibleForTesting
    DetectedUserEmailAnalyticsScheduledRunner(SchedulerService schedulerService, DetectedUserEmailAnalyticsService detectedUserEmailAnalyticsService, MigrationAgentConfiguration configuration, Supplier<Instant> instantSupplier) {
        this.schedulerService = schedulerService;
        this.detectedUserEmailAnalyticsService = detectedUserEmailAnalyticsService;
        this.configuration = configuration;
        this.instantSupplier = instantSupplier;
    }

    @PostConstruct
    public void postConstruct() {
        this.schedulerService.registerJobRunner(JobRunnerKey.of((String)JOB_RUNNER_ID), (JobRunner)this);
        Duration intervalBetweenRuns = this.configuration.getIntervalBetweenDetectedUserEmailAnalyticsRuns();
        JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)JobRunnerKey.of((String)JOB_RUNNER_ID)).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.forInterval((long)intervalBetweenRuns.toMillis(), (Date)Date.from(this.instantSupplier.get().plus(Duration.ofSeconds(15L)))));
        try {
            this.schedulerService.scheduleJob(JOB_ID, jobConfig);
            log.info("Scheduled detected email analytics event collection job to be run each {} hours.", (Object)intervalBetweenRuns.toHours());
        }
        catch (SchedulerServiceException e) {
            throw new RuntimeException("Failed to schedule job " + JOB_ID, e);
        }
    }

    @PreDestroy
    public void cleanup() {
        this.schedulerService.unregisterJobRunner(JobRunnerKey.of((String)JOB_RUNNER_ID));
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        log.info("Trigger detected emails analytics events collection for all linked cloud ids.");
        this.detectedUserEmailAnalyticsService.triggerForAllCloudIds();
        return JobRunnerResponse.success();
    }
}

