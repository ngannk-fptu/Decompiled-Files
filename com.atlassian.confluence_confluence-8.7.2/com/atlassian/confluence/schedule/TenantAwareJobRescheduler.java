/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.config.Schedule$Type
 *  com.atlassian.scheduler.status.JobDetails
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.schedule;

import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.status.JobDetails;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TenantAwareJobRescheduler {
    private static final String RESCHEDULE_ACTIVE_KEY = "confluence.tenant.job.reschedule";
    public static final String TZ_SENSITIVE_JOB_KEY = TenantAwareJobRescheduler.class.getName() + ":is-tz-sensitive";
    private static final Logger log = LoggerFactory.getLogger(TenantAwareJobRescheduler.class);
    private final SchedulerService schedulerService;
    private final TimeZoneManager timeZoneManager;

    public TenantAwareJobRescheduler(SchedulerService schedulerService, TimeZoneManager timeZoneManager) {
        this.schedulerService = schedulerService;
        this.timeZoneManager = timeZoneManager;
    }

    public void rescheduleJobs() {
        if (!Boolean.getBoolean(RESCHEDULE_ACTIVE_KEY)) {
            return;
        }
        TimeZone tenantTimeZone = this.timeZoneManager.getDefaultTimeZone();
        Set jobRunnerKeys = this.schedulerService.getJobRunnerKeysForAllScheduledJobs();
        try {
            for (JobRunnerKey jobRunnerKey : jobRunnerKeys) {
                List jobDetailsList = this.schedulerService.getJobsByJobRunnerKey(jobRunnerKey);
                for (JobDetails jobDetails : jobDetailsList) {
                    Schedule schedule = jobDetails.getSchedule();
                    Map parameters = jobDetails.getParameters();
                    if (schedule.getType() != Schedule.Type.CRON_EXPRESSION || !parameters.containsKey(TZ_SENSITIVE_JOB_KEY)) continue;
                    JobId jobId = jobDetails.getJobId();
                    log.debug("Found timezone sensitive job: {}", (Object)jobId);
                    Schedule newSchedule = Schedule.forCronExpression((String)schedule.getCronScheduleInfo().getCronExpression(), (TimeZone)tenantTimeZone);
                    JobConfig newJobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)jobDetails.getJobRunnerKey()).withRunMode(jobDetails.getRunMode()).withSchedule(newSchedule).withParameters(parameters);
                    this.schedulerService.scheduleJob(jobId, newJobConfig);
                }
            }
        }
        catch (SchedulerServiceException e) {
            log.error("Unexpected error when rescheduling cron-based atlassian-scheduler jobs", (Throwable)e);
        }
    }
}

