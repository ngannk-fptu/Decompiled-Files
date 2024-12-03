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
 *  javax.validation.constraints.NotNull
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.mma.service;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.mma.service.MigrationMetadataAggregatorService;
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
import javax.validation.constraints.NotNull;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceMetadataIntervalEmitter
implements JobRunner {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SpaceMetadataIntervalEmitter.class);
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final MigrationMetadataAggregatorService migrationMetadataAggregatorService;
    private final SchedulerService schedulerService;
    static final String atlassianSchedulerJobName = "migration-plugin:emit-space-metadata-to-mma";
    static final JobRunnerKey RUNNER_KEY = JobRunnerKey.of((String)"migration-plugin:emit-space-metadata-to-mma");
    static final JobId JOB_ID = JobId.of((String)"migration-plugin:emit-space-metadata-to-mma");
    static final String RESCHEDULE_CRON_INTERVAL_3AM_LOCAL_TIME = "0 0 3 * * ?";

    public SpaceMetadataIntervalEmitter(MigrationDarkFeaturesManager migrationDarkFeaturesManager, MigrationMetadataAggregatorService migrationMetadataAggregatorService, SchedulerService schedulerService) {
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.migrationMetadataAggregatorService = migrationMetadataAggregatorService;
        this.schedulerService = schedulerService;
    }

    @PostConstruct
    public void postConstruct() throws SchedulerServiceException {
        this.unscheduleJobIfExist(JOB_ID);
        this.schedulerService.registerJobRunner(RUNNER_KEY, (JobRunner)this);
        log.debug("Successfully registered EmitSpaceMetadataToMMA job {}.", (Object)RUNNER_KEY);
        this.schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.forCronExpression((String)RESCHEDULE_CRON_INTERVAL_3AM_LOCAL_TIME)));
        log.debug("Successfully started EmitSpaceMetadataToMMA.");
    }

    @PreDestroy
    public void cleanup() {
        this.unscheduleJobIfExist(JOB_ID);
        this.schedulerService.unregisterJobRunner(RUNNER_KEY);
    }

    public JobRunnerResponse runJob(@NotNull JobRunnerRequest request) {
        if (!this.migrationDarkFeaturesManager.isCloudFirstMigrationEnabled()) {
            return JobRunnerResponse.success((String)"Space metadata emission is not enabled.");
        }
        try {
            this.migrationMetadataAggregatorService.sendSpaceMetadataToMMAForAllCloudSites();
        }
        catch (Exception e) {
            return JobRunnerResponse.failed((Throwable)e);
        }
        return JobRunnerResponse.success((String)"Finished emitting space metadata to MMA.");
    }

    private void unscheduleJobIfExist(JobId jobId) {
        try {
            this.schedulerService.unscheduleJob(jobId);
        }
        catch (Exception exception) {
            log.warn("could not un-schedule job {}", (Object)jobId.toString(), (Object)exception);
        }
    }
}

