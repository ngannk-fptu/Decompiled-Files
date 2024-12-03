/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
import com.atlassian.troubleshooting.jfr.config.JfrProperties;
import com.atlassian.troubleshooting.jfr.enums.RecordingTemplate;
import com.atlassian.troubleshooting.jfr.manager.JfrRecordingManager;
import com.atlassian.troubleshooting.stp.scheduler.SchedulerServiceProvider;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class JfrDumpingScheduler
implements LifecycleAware,
JobRunner {
    private static final Logger LOG = LoggerFactory.getLogger(JfrDumpingScheduler.class);
    private static final String TASK_ID_JRF_DUMPING = "JfrScheduledDumpTask";
    private static final JobId JOB_ID = JobId.of((String)"JfrScheduledDumpTask");
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)"JfrScheduledDumpTask");
    private final SchedulerServiceProvider schedulerServiceProvider;
    private final JfrRecordingManager jfrRecordingManager;
    private final JfrProperties jfrProperties;
    private SchedulerService schedulerService;

    @Autowired
    public JfrDumpingScheduler(SchedulerServiceProvider schedulerServiceProvider, JfrRecordingManager jfrRecordingManager, JfrProperties jfrProperties) {
        this.schedulerServiceProvider = Objects.requireNonNull(schedulerServiceProvider);
        this.jfrRecordingManager = Objects.requireNonNull(jfrRecordingManager);
        this.jfrProperties = Objects.requireNonNull(jfrProperties);
    }

    public void onStart() {
        this.schedulerService = this.schedulerServiceProvider.getSchedulerService();
        this.schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)this);
        this.scheduleJfrDumping();
        LOG.debug("JFR scheduler registered.");
    }

    public void onStop() {
        this.schedulerService.unregisterJobRunner(JobRunnerKey.of((String)TASK_ID_JRF_DUMPING));
    }

    private void scheduleJfrDumping() {
        Schedule schedule = Schedule.forCronExpression((String)this.jfrProperties.getDumpCronExpression());
        try {
            this.schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withRunMode(RunMode.RUN_LOCALLY).withSchedule(schedule));
        }
        catch (SchedulerServiceException e) {
            LOG.error("Failed to schedule dumping JFR", (Throwable)e);
        }
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        LOG.debug("JFR scheduler started job.");
        try {
            this.dumpRecordings();
            LOG.debug("JFR scheduler finished job successfully.");
            return JobRunnerResponse.success();
        }
        catch (Exception exc) {
            LOG.error("Error dumping JFR recording", (Throwable)exc);
            return JobRunnerResponse.failed((Throwable)exc);
        }
    }

    private void dumpRecordings() {
        if (this.jfrRecordingManager.getSettings().isEnabled()) {
            this.jfrRecordingManager.getRecordingDetails().stream().filter(recordingDetails -> RecordingTemplate.DEFAULT.getRecordingName().equals(recordingDetails.getName())).findAny().ifPresent(recordingDetails -> this.jfrRecordingManager.dumpRecording(recordingDetails.getId()));
        }
    }
}

