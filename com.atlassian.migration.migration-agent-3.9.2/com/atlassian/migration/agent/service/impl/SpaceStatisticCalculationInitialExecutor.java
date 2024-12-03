/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent
 *  com.atlassian.confluence.event.events.space.SpaceCreateEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
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
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  lombok.Generated
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent;
import com.atlassian.confluence.event.events.space.SpaceCreateEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.service.impl.SpaceStatisticCalculationService;
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
import com.google.common.annotations.VisibleForTesting;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceStatisticCalculationInitialExecutor
implements JobRunner {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SpaceStatisticCalculationInitialExecutor.class);
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final SchedulerService schedulerService;
    private final SpaceStatisticCalculationService spaceStatisticCalculationService;
    private final EventPublisher eventPublisher;
    private final JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.runOnce(null));
    private static final JobRunnerKey RUNNER_KEY = JobRunnerKey.of((String)"migration-plugin:initial-space-statistic-calculation");
    public static final JobId JOB_ID = JobId.of((String)"migration-plugin:initial-space-statistic-calculation");
    public static final JobId FORCE_UPDATE_JOB_ID = JobId.of((String)"migration-plugin:initial-space-statistic-calculation-force-update");

    public SpaceStatisticCalculationInitialExecutor(SpaceStatisticCalculationService spaceStatisticCalculationService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, SchedulerService schedulerService, EventPublisher eventPublisher) {
        this.spaceStatisticCalculationService = spaceStatisticCalculationService;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.schedulerService = schedulerService;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    @VisibleForTesting
    void init() throws SchedulerServiceException {
        this.unscheduleJobIfExist(JOB_ID);
        this.unscheduleJobIfExist(FORCE_UPDATE_JOB_ID);
        this.eventPublisher.register((Object)this);
        this.schedulerService.registerJobRunner(RUNNER_KEY, (JobRunner)this);
        log.info("Successfully registered job with runnerKey: {}", (Object)RUNNER_KEY);
        this.schedulerService.scheduleJob(JOB_ID, this.jobConfig);
        log.info("Successfully scheduled job. jobId: {} , runnerKey: {}", (Object)JOB_ID, (Object)RUNNER_KEY);
    }

    @PreDestroy
    @VisibleForTesting
    void cleanup() {
        this.unregisterEventPublisher(this.eventPublisher);
        this.unscheduleJobIfExist(JOB_ID);
        this.unscheduleJobIfExist(FORCE_UPDATE_JOB_ID);
        this.schedulerService.unregisterJobRunner(RUNNER_KEY);
    }

    @Nullable
    public JobRunnerResponse runJob(@NotNull JobRunnerRequest request) {
        if (this.migrationDarkFeaturesManager.isNewSpaceSelectorEnabled()) {
            JobId jobId = request.getJobId();
            log.info("Running initial space statistic calculation job. jobId: {}", (Object)jobId);
            try {
                if (jobId.equals((Object)FORCE_UPDATE_JOB_ID)) {
                    this.spaceStatisticCalculationService.runSpaceStatisticCalculation(jobId, true, true);
                } else {
                    this.spaceStatisticCalculationService.runSpaceStatisticCalculationIfEmptyOrMissingSpaces(jobId, true);
                }
                return JobRunnerResponse.success((String)"Ran initial space statistic calculation.");
            }
            catch (Exception e) {
                log.error("Failed to run initial space statistic calculation.", (Object)e.getMessage());
                return JobRunnerResponse.failed((String)("Failed to run initial space statistic calculation." + e.getMessage()));
            }
        }
        log.info("Skipped initial space statistic calculation as feature is disabled");
        return JobRunnerResponse.success((String)"Skipped initial space statistic calculation as feature is disabled.");
    }

    @EventListener
    public void handleNewSpaceSelectorTurnedOn(SiteDarkFeatureDisabledEvent event) throws SchedulerServiceException {
        if (event.getFeatureKey().equals("migration-assistant.disable.new-space-selector-feature")) {
            if (this.noInitialJobInProgress()) {
                log.info("Scheduling space statistics calculation job. Is new space selector enabled yet? {}", (Object)"migration-assistant.disable.new-space-selector-feature", (Object)this.migrationDarkFeaturesManager.isNewSpaceSelectorEnabled());
                this.schedulerService.scheduleJob(JOB_ID, this.jobConfig);
                log.info("Successfully scheduled job. jobId: {} , runnerKey: {}", (Object)JOB_ID, (Object)RUNNER_KEY);
            } else {
                log.info("The scheduling of job {} was skipped as similar job is in progress", (Object)JOB_ID);
            }
        }
    }

    @EventListener
    public void handleSpaceCreatedEvent(SpaceCreateEvent event) {
        if (this.migrationDarkFeaturesManager.isNewSpaceSelectorEnabled()) {
            this.spaceStatisticCalculationService.createSpaceStatistic(event.getSpace().getId(), event.getSpace().getLastModificationDate());
        }
    }

    public boolean scheduleWithForceUpdate() throws SchedulerServiceException {
        if (this.noInitialJobInProgress()) {
            this.schedulerService.scheduleJob(FORCE_UPDATE_JOB_ID, this.jobConfig);
            log.info("Successfully scheduled job with forceUpdate. jobId: {} , runnerKey: {}", (Object)FORCE_UPDATE_JOB_ID, (Object)RUNNER_KEY);
            return true;
        }
        log.info("The scheduling of job {} was skipped as similar job is in progress", (Object)FORCE_UPDATE_JOB_ID);
        return false;
    }

    public boolean noInitialJobInProgress() {
        return this.schedulerService.getJobDetails(JOB_ID) == null && this.schedulerService.getJobDetails(FORCE_UPDATE_JOB_ID) == null;
    }

    private void unscheduleJobIfExist(JobId jobId) {
        try {
            this.schedulerService.unscheduleJob(jobId);
        }
        catch (Exception exception) {
            log.warn("could not unschedule job {}", (Object)jobId.toString(), (Object)exception);
        }
    }

    private void unregisterEventPublisher(EventPublisher eventPublisher) {
        try {
            eventPublisher.unregister((Object)this);
        }
        catch (Exception exception) {
            log.warn("could not unregister from eventPublisher", (Throwable)exception);
        }
    }
}

