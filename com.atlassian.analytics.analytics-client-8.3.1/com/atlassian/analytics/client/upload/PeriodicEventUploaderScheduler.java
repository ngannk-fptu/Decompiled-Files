/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.analytics.client.upload;

import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.s3.AnalyticsS3Client;
import com.atlassian.analytics.client.upload.RemoteFilterRead;
import com.atlassian.analytics.client.upload.S3EventUploader;
import com.atlassian.analytics.client.upload.UploadDateCalculator;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.google.common.annotations.VisibleForTesting;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class PeriodicEventUploaderScheduler
implements DisposableBean {
    private static final String JOB_SUFFIX = ":job";
    @VisibleForTesting
    static final JobId FILTER_READ_JOB_KEY = JobId.of((String)(RemoteFilterRead.class.getName() + ":job"));
    @VisibleForTesting
    static final JobId S3_UPLOAD_JOB_KEY = JobId.of((String)(S3EventUploader.class.getName() + ":job"));
    private static final Logger LOG = LoggerFactory.getLogger(PeriodicEventUploaderScheduler.class);
    private final AnalyticsConfig analyticsConfig;
    private final SchedulerService schedulerService;

    public PeriodicEventUploaderScheduler(SchedulerService schedulerService, AnalyticsConfig analyticsConfig) {
        this.schedulerService = Objects.requireNonNull(schedulerService);
        this.analyticsConfig = Objects.requireNonNull(analyticsConfig);
    }

    public void initialise() {
        LOG.debug("upload scheduler is being initialized");
        this.analyticsConfig.setDefaultAnalyticsEnabled();
        this.unscheduleJobs();
        this.rescheduleUploadJob();
        if (this.analyticsConfig.canCollectAnalytics()) {
            LOG.info("Analytics collection is on");
            this.scheduleInitialRemoteRead();
        }
        LOG.debug("upload scheduler initialization complete");
    }

    public void scheduleInitialRemoteRead() {
        Instant scheduleStartDate = PeriodicEventUploaderScheduler.getImmediateScheduleStartDate();
        LOG.debug("Scheduled job to perform initial remote filter read to start at {}", (Object)scheduleStartDate);
        this.scheduleJob(FILTER_READ_JOB_KEY, RemoteFilterRead.KEY, scheduleStartDate);
    }

    private void scheduleJob(JobId jobId, JobRunnerKey jobRunnerKey, Instant scheduleStartDate) {
        this.schedulerService.unscheduleJob(jobId);
        if (this.schedulerService.getJobDetails(jobId) == null) {
            Schedule schedule = Schedule.forInterval((long)PeriodicEventUploaderScheduler.getSchedulerInterval(), (Date)Date.from(scheduleStartDate));
            JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)jobRunnerKey).withSchedule(schedule).withRunMode(RunMode.RUN_LOCALLY);
            try {
                this.schedulerService.scheduleJob(jobId, jobConfig);
            }
            catch (SchedulerServiceException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public void runUploadJobImmediately() {
        this.rescheduleUploadJob(PeriodicEventUploaderScheduler.getImmediateScheduleStartDate());
    }

    void rescheduleRemoteReadJob() {
        Instant scheduleStartDate = PeriodicEventUploaderScheduler.getScheduleStartDate();
        LOG.debug("Scheduled job to read remote event filters to start at {} and repeat every day", (Object)scheduleStartDate);
        this.scheduleJob(FILTER_READ_JOB_KEY, RemoteFilterRead.KEY, scheduleStartDate);
    }

    void rescheduleUploadJob() {
        this.rescheduleUploadJob(PeriodicEventUploaderScheduler.getScheduleStartDate());
    }

    private void rescheduleUploadJob(Instant scheduleStartDate) {
        LOG.debug("Scheduled job to upload analytics logs to start at {} and repeat every day", (Object)scheduleStartDate);
        this.scheduleJob(S3_UPLOAD_JOB_KEY, S3EventUploader.KEY, scheduleStartDate);
    }

    private static Instant getImmediateScheduleStartDate() {
        return Instant.now().plus(10L, ChronoUnit.SECONDS);
    }

    private static Instant getScheduleStartDate() {
        return UploadDateCalculator.calculateUploadTime(Instant.now());
    }

    private static long getSchedulerInterval() {
        return 172800000L;
    }

    public void destroy() {
        AnalyticsS3Client.unregisterMetricAdminMBean();
    }

    public void unscheduleJobs() {
        this.schedulerService.unscheduleJob(FILTER_READ_JOB_KEY);
        this.schedulerService.unscheduleJob(S3_UPLOAD_JOB_KEY);
    }
}

