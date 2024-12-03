/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
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
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.scheduler;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
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
import com.atlassian.troubleshooting.jfr.domain.ConfigurationDetails;
import com.atlassian.troubleshooting.jfr.event.JfrSettingsStateAnalyticsEvent;
import com.atlassian.troubleshooting.jfr.event.JfrStateAnalyticsEvent;
import com.atlassian.troubleshooting.jfr.manager.JfrRecordingManager;
import com.atlassian.troubleshooting.jfr.util.JfrConditionUtils;
import com.atlassian.troubleshooting.stp.scheduler.SchedulerServiceProvider;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class JfrSettingsAnalyticsSendingScheduler
implements LifecycleAware,
JobRunner {
    private static final Logger LOG = LoggerFactory.getLogger(JfrSettingsAnalyticsSendingScheduler.class);
    private static final String EVERY_DAY_AT_MIDNIGHT_CRON = "0 0 0 ? * * *";
    private static final String TASK_ID = "JfrSettingsStateAnalyticsTask";
    private static final JobId JOB_ID = JobId.of((String)"JfrSettingsStateAnalyticsTask");
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)"JfrSettingsStateAnalyticsTask");
    private final SchedulerServiceProvider schedulerServiceProvider;
    private final JfrRecordingManager jfrRecordingManager;
    private final EventPublisher eventPublisher;
    private SchedulerService schedulerService;

    @Autowired
    public JfrSettingsAnalyticsSendingScheduler(SchedulerServiceProvider schedulerServiceProvider, JfrRecordingManager jfrRecordingManager, EventPublisher eventPublisher) {
        this.schedulerServiceProvider = Objects.requireNonNull(schedulerServiceProvider);
        this.jfrRecordingManager = Objects.requireNonNull(jfrRecordingManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public void onStart() {
        this.registerAnalyticsSendingJob();
        this.scheduleAnalyticsSendingJob();
        LOG.debug("JFR settings analytics sending scheduler registered");
    }

    private void registerAnalyticsSendingJob() {
        this.schedulerService = this.schedulerServiceProvider.getSchedulerService();
        this.schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)this);
    }

    private void scheduleAnalyticsSendingJob() {
        try {
            JobConfig jobConfig = this.buildJobConfig();
            this.schedulerService.scheduleJob(JOB_ID, jobConfig);
        }
        catch (SchedulerServiceException e) {
            LOG.error("Failed to schedule sending JFR settings analytics", (Throwable)e);
        }
    }

    private JobConfig buildJobConfig() {
        Schedule schedule = Schedule.forCronExpression((String)EVERY_DAY_AT_MIDNIGHT_CRON);
        return JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withRunMode(RunMode.RUN_LOCALLY).withSchedule(schedule);
    }

    public void onStop() {
        this.schedulerService.unregisterJobRunner(JobRunnerKey.of((String)TASK_ID));
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        LOG.debug("JFR settings analytics sending scheduler started job.");
        try {
            this.sendJfrSettingsAnalytics();
            this.sendJfrStateAnalytics();
            LOG.debug("JFR settings analytics sending scheduler finished job successfully.");
            return JobRunnerResponse.success();
        }
        catch (Exception exc) {
            LOG.error("Error occurred while sending JFR settings analytics", (Throwable)exc);
            return JobRunnerResponse.failed((Throwable)exc);
        }
    }

    private void sendJfrSettingsAnalytics() {
        ConfigurationDetails configurationDetails = this.jfrRecordingManager.getActiveConfiguration();
        JfrSettingsStateAnalyticsEvent event = JfrSettingsStateAnalyticsEvent.from(configurationDetails);
        this.eventPublisher.publish((Object)event);
    }

    private void sendJfrStateAnalytics() {
        JfrStateAnalyticsEvent event = new JfrStateAnalyticsEvent(JfrConditionUtils.isJavaVersionSupported(), !this.jfrRecordingManager.getRecordingDetails().isEmpty(), this.jfrRecordingManager.isJfrFeatureFlagEnabled());
        this.eventPublisher.publish((Object)event);
    }
}

