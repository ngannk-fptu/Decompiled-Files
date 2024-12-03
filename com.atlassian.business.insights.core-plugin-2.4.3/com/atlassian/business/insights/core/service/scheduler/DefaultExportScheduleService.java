/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.status.JobDetails
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.service.scheduler;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.core.service.api.ExportScheduleService;
import com.atlassian.business.insights.core.service.scheduler.ExportScheduleException;
import com.atlassian.business.insights.core.service.scheduler.ExportScheduleJobRunner;
import com.atlassian.business.insights.core.service.scheduler.ScheduleConfig;
import com.atlassian.business.insights.core.util.CronJobConversionUtil;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.status.JobDetails;
import com.google.common.collect.ImmutableMap;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import javax.annotation.Nonnull;

public class DefaultExportScheduleService
implements ExportScheduleService {
    @VisibleForTesting
    static final JobRunnerKey SCHEDULE_JOB_KEY = JobRunnerKey.of((String)ExportScheduleJobRunner.class.getName());
    @VisibleForTesting
    static final JobId SCHEDULE_JOB_ID = JobId.of((String)ExportScheduleJobRunner.class.getName());
    static final String FROM_DATE = "fromDate";
    static final String SCHEMA_VERSION = "schemaVersion";
    private final SchedulerService schedulerService;
    private final ExportScheduleJobRunner exportScheduleJobRunner;

    public DefaultExportScheduleService(ExportScheduleJobRunner exportScheduleJobRunner, SchedulerService schedulerService) {
        this.exportScheduleJobRunner = exportScheduleJobRunner;
        this.schedulerService = schedulerService;
    }

    @Override
    public void registerJobRunner() {
        this.schedulerService.registerJobRunner(SCHEDULE_JOB_KEY, (JobRunner)this.exportScheduleJobRunner);
    }

    @Override
    public void unregisterJobRunner() {
        this.schedulerService.unregisterJobRunner(SCHEDULE_JOB_KEY);
    }

    @Override
    @Nonnull
    public JobDetails scheduleJob(@Nonnull ScheduleConfig config) {
        Objects.requireNonNull(config);
        String cronExpression = CronJobConversionUtil.validatedCronJobConfig(CronJobConversionUtil.buildCronjobConfig(config));
        String fromDate = config.getFromDate();
        ImmutableMap parameters = ImmutableMap.of((Object)FROM_DATE, (Object)fromDate, (Object)SCHEMA_VERSION, (Object)config.getSchemaVersion());
        JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)SCHEDULE_JOB_KEY).withSchedule(Schedule.forCronExpression((String)cronExpression, (TimeZone)this.getTimeZoneFromConfig(config))).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withParameters((Map)parameters);
        try {
            this.schedulerService.scheduleJob(SCHEDULE_JOB_ID, jobConfig);
            return this.schedulerService.getJobDetails(SCHEDULE_JOB_ID);
        }
        catch (SchedulerServiceException e) {
            throw new ExportScheduleException("Unable to create schedule for the data-pipeline", e);
        }
    }

    private TimeZone getTimeZoneFromConfig(ScheduleConfig config) {
        return TimeZone.getTimeZone(ZoneId.of(config.getZoneId()));
    }

    @Override
    public void unscheduleJob() {
        this.schedulerService.unscheduleJob(SCHEDULE_JOB_ID);
    }
}

