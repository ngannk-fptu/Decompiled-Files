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
 *  javax.inject.Inject
 *  javax.validation.constraints.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.guardrails;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.entity.InstanceAnalysisControl;
import com.atlassian.migration.agent.service.guardrails.InstanceAnalysisControlTypes;
import com.atlassian.migration.agent.store.guardrails.InstanceAnalysisControlStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
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
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceAnalysisControlService
implements JobRunner {
    private static final Long BROWSER_METRICS_COLLECTION_TIME_IN_DAYS = 1L;
    private final PluginTransactionTemplate ptx;
    private final Logger log = LoggerFactory.getLogger(InstanceAnalysisControlService.class);
    static final JobRunnerKey RUNNER_KEY = JobRunnerKey.of((String)"com.atlassian.jira.migration.guardrails.InstanceAnalysisControlScheduler");
    static final JobId JOB_ID = JobId.of((String)"instance-analysis-control-job-id");
    private final InstanceAnalysisControlStore instanceAnalysisControlStore;
    private final SchedulerService schedulerService;
    private final MigrationDarkFeaturesManager features;

    @Inject
    public InstanceAnalysisControlService(PluginTransactionTemplate ptx, @NotNull InstanceAnalysisControlStore instanceAnalysisControlStore, @NotNull SchedulerService schedulerService, MigrationDarkFeaturesManager features) {
        this.ptx = ptx;
        this.instanceAnalysisControlStore = instanceAnalysisControlStore;
        this.schedulerService = schedulerService;
        this.features = features;
    }

    @PostConstruct
    void postConstruct() {
        this.schedulerService.registerJobRunner(RUNNER_KEY, (JobRunner)this);
    }

    @PreDestroy
    void preDestroy() {
        this.schedulerService.unregisterJobRunner(RUNNER_KEY);
        this.log.info("Instance analysis control job unregistered successfully.");
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        try {
            Optional<InstanceAnalysisControl> instAnalysisCtrl = this.instanceAnalysisControlStore.findInstanceAnalysisControl(InstanceAnalysisControlTypes.BROWSER_METRICS.name());
            if (instAnalysisCtrl.isPresent() && !this.instanceAnalysisControlStore.isFinished(instAnalysisCtrl.get().getEndTimestamp())) {
                this.log.info("Updating end time on instance analysis control");
                Duration remainingDuration = this.calculateRemainingDuration(instAnalysisCtrl.get());
                if (!this.features.isBrowserMetricsEnabled() || remainingDuration.isZero() || remainingDuration.isNegative()) {
                    this.finishAssessmentCollection();
                }
            }
            return JobRunnerResponse.success((String)request.getJobId().toString());
        }
        catch (Exception e) {
            this.log.error("Failed to run instance analysis control with job id ${request.jobId}: ${e.message}", (Throwable)e);
            return JobRunnerResponse.failed((Throwable)e);
        }
    }

    void finishAssessmentCollection() {
        this.ptx.write(() -> this.instanceAnalysisControlStore.completeInstanceAnalysisControl(InstanceAnalysisControlTypes.BROWSER_METRICS.name()));
        this.schedulerService.unscheduleJob(JOB_ID);
        this.log.info("Instance analysis control is completed.");
    }

    public final void startAssessmentCollection() throws SchedulerServiceException {
        this.log.info("Initializing browser metrics collection");
        this.ptx.write(() -> this.createInstanceAnalysisControl(InstanceAnalysisControlTypes.BROWSER_METRICS.name()));
        this.scheduleAssessmentControlJob();
        this.log.info("Browser metrics collection started");
    }

    public final void scheduleAssessmentControlJob() throws SchedulerServiceException {
        this.schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.forInterval((long)TimeUnit.HOURS.toMillis(1L), (Date)Date.from(Instant.now().plus(Duration.ofHours(1L))))));
    }

    public final Optional<InstanceAnalysisControl> findInstanceAnalysisControl(@NotNull String analysisType) {
        return this.instanceAnalysisControlStore.findInstanceAnalysisControl(analysisType);
    }

    @NotNull
    public final InstanceAnalysisControl createInstanceAnalysisControl(@NotNull String analysisType) {
        return this.instanceAnalysisControlStore.createInstanceAnalysisControl(analysisType);
    }

    Duration calculateRemainingDuration(InstanceAnalysisControl instanceAnalysisControl) {
        Duration elapsed = Duration.between(Instant.ofEpochMilli(instanceAnalysisControl.getStartTimestamp()), Instant.now());
        Duration taskDuration = Duration.ofDays(BROWSER_METRICS_COLLECTION_TIME_IN_DAYS);
        Duration remainingDuration = taskDuration.minus(elapsed);
        if (remainingDuration.isNegative()) {
            return Duration.ZERO;
        }
        return remainingDuration;
    }
}

