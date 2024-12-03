/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.CronScheduleInfo
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.config.Schedule$Type
 *  com.atlassian.scheduler.core.LifecycleAwareSchedulerService
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.schedule;

import com.atlassian.confluence.schedule.ManagedScheduledJob;
import com.atlassian.confluence.schedule.ScheduledJobConfiguration;
import com.atlassian.confluence.schedule.ScheduledJobStatus;
import com.atlassian.confluence.schedule.managers.ScheduledJobManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.CronScheduleInfo;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.core.LifecycleAwareSchedulerService;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ScheduleUtil {
    public static final JobId BACKUP_JOB_ID = JobId.of((String)"BackupJob");
    public static final String SCHEDULER_FLUSH_TIMEOUT_SECONDS_PROPERTY_NAME = "confluence.scheduler.flush.timeout.seconds";
    private static final long DEFAULT_WAIT_UNTIL_IDLE_TIMEOUT_SECONDS = 120L;
    private static final int MAX_GENERATE_UNIQUE_JOB_ID_ATTEMPTS = 100;
    private static final int WAIT_UNTIL_IDLE_TIMEOUT_MINUTES = 5;
    private static final String CRON_JOB_JITTER_SECS = "CONF_CRON_JOB_JITTER_SECS";
    private static final String INTERVAL_JOB_REPEAT_COUNT = "CONF_INTERVAL_JOB_REPEAT_COUNT";
    private static final String INTERVAL_JOB_UNSCHEDULED_AT = "CONF_INTERVAL_JOB_UNSCHEDULED_AT";

    public static boolean isBackupEnabled(ScheduledJobManager scheduledJobManager, SettingsManager settingsManager) {
        if (scheduledJobManager == null) {
            return settingsManager != null && settingsManager.getGlobalSettings().isBackupDaily();
        }
        ScheduledJobStatus status = scheduledJobManager.getScheduledJob(BACKUP_JOB_ID);
        return status != null && status.isEnabled() && settingsManager != null && settingsManager.getGlobalSettings().isBackupDaily();
    }

    public static @Nullable Date calculateNextRunTime(SchedulerService schedulerService, JobConfig jobConfig, @Nullable Date thisRunTime) throws SchedulerServiceException {
        long ms = ScheduleUtil.getUnscheduleJobAfterTimestampMillis(jobConfig.getParameters());
        if (ms < System.currentTimeMillis()) {
            return null;
        }
        Schedule schedule = jobConfig.getSchedule();
        if (schedule.getType() == Schedule.Type.INTERVAL) {
            long t = thisRunTime == null ? System.currentTimeMillis() : thisRunTime.getTime();
            long dt = schedule.getIntervalScheduleInfo().getIntervalInMillis();
            return new Date(t + dt);
        }
        return schedulerService.calculateNextRunTime(schedule);
    }

    public static JobId generateUniqueJobId(SchedulerService schedulerService, String idPrefix) throws SchedulerServiceException {
        for (int i = 0; i < 100; ++i) {
            JobId jobId = JobId.of((String)(idPrefix + UUID.randomUUID()));
            if (schedulerService.getJobDetails(jobId) != null) continue;
            return jobId;
        }
        throw new SchedulerServiceException("Unable to generate a unique job ID");
    }

    public static void pauseAndFlushSchedulerService(LifecycleAwareSchedulerService schedulerService) throws SchedulerServiceException {
        try {
            schedulerService.standby();
        }
        catch (SchedulerServiceException e) {
            throw new SchedulerServiceException("Unable to switch atlassian-scheduler into standby mode", (Throwable)e);
        }
        ScheduleUtil.flushSchedulerService(schedulerService);
    }

    public static void shutdownAndFlushSchedulerService(LifecycleAwareSchedulerService schedulerService) throws SchedulerServiceException {
        schedulerService.shutdown();
        ScheduleUtil.flushSchedulerService(schedulerService);
    }

    private static void flushSchedulerService(LifecycleAwareSchedulerService schedulerService) throws SchedulerServiceException {
        try {
            boolean idle = schedulerService.waitUntilIdle(ScheduleUtil.getSchedulerFlushTimeout(), TimeUnit.SECONDS);
            if (!idle) {
                throw new SchedulerServiceException("Timed out waiting for atlassian-scheduler currently executing jobs to complete: " + schedulerService.getLocallyRunningJobs());
            }
        }
        catch (InterruptedException e) {
            throw new SchedulerServiceException("Interrupted while waiting for atlassian-scheduler currently executing jobs to complete: " + schedulerService.getLocallyRunningJobs(), (Throwable)e);
        }
    }

    public static long getSchedulerFlushTimeout() {
        return Long.getLong(SCHEDULER_FLUSH_TIMEOUT_SECONDS_PROPERTY_NAME, 120L);
    }

    public static Map<String, Serializable> withJitterSecs(int jitterSecs) {
        return ScheduleUtil.withJitterSecs((Map<String, Serializable>)ImmutableMap.of(), jitterSecs);
    }

    public static Map<String, Serializable> withJitterSecs(Map<String, Serializable> parameters, int jitterSecs) {
        if (jitterSecs <= 0) {
            return parameters;
        }
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.putAll(parameters);
        builder.put((Object)CRON_JOB_JITTER_SECS, (Object)jitterSecs);
        return builder.build();
    }

    public static int getJitterSecs(Map<String, Serializable> parameters) {
        return (Integer)parameters.getOrDefault(CRON_JOB_JITTER_SECS, Integer.valueOf(-1));
    }

    public static Map<String, Serializable> withoutJitterSecs(Map<String, Serializable> parameters) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Map.Entry<String, Serializable> entry : parameters.entrySet()) {
            String key = entry.getKey();
            if (key.equals(CRON_JOB_JITTER_SECS)) continue;
            builder.put((Object)key, (Object)entry.getValue());
        }
        return builder.build();
    }

    public static Map<String, Serializable> withUnscheduleJobAfterTimestampMillis(long repeatInterval, int repeatCount, @Nullable Date firstRunTime) {
        return ScheduleUtil.withUnscheduleJobAfterTimestampMillis((Map<String, Serializable>)ImmutableMap.of(), repeatInterval, repeatCount, firstRunTime);
    }

    public static Map<String, Serializable> withUnscheduleJobAfterTimestampMillis(Map<String, Serializable> parameters, long repeatInterval, int repeatCount, @Nullable Date firstRunTime) {
        if (repeatInterval <= 0L || repeatCount < 0) {
            HashMap<String, Serializable> mutable = new HashMap<String, Serializable>(parameters);
            mutable.remove(INTERVAL_JOB_REPEAT_COUNT);
            mutable.remove(INTERVAL_JOB_UNSCHEDULED_AT);
            return ImmutableMap.copyOf(mutable);
        }
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.putAll(parameters);
        long firstRunTimeMs = firstRunTime == null ? System.currentTimeMillis() : firstRunTime.getTime();
        long timestampMs = firstRunTimeMs + repeatInterval * (long)(repeatCount + 1) - 1L;
        builder.put((Object)INTERVAL_JOB_REPEAT_COUNT, (Object)repeatCount);
        builder.put((Object)INTERVAL_JOB_UNSCHEDULED_AT, (Object)timestampMs);
        return builder.build();
    }

    public static long getUnscheduleJobAfterTimestampMillis(Map<String, Serializable> parameters) {
        return (Long)parameters.getOrDefault(INTERVAL_JOB_UNSCHEDULED_AT, Long.valueOf(Long.MAX_VALUE));
    }

    public static int getRepeatCount(Map<String, Serializable> parameters) {
        return (Integer)parameters.getOrDefault(INTERVAL_JOB_REPEAT_COUNT, Integer.valueOf(-1));
    }

    public static JobConfig withTimeZone(JobConfig jobConfig, TimeZone timeZone) {
        Schedule schedule = jobConfig.getSchedule();
        if (schedule.getType() != Schedule.Type.CRON_EXPRESSION) {
            return jobConfig;
        }
        CronScheduleInfo cronScheduleInfo = schedule.getCronScheduleInfo();
        TimeZone scheduleTimeZone = cronScheduleInfo.getTimeZone();
        if (ScheduleUtil.sameTimeZones(scheduleTimeZone, timeZone)) {
            return jobConfig;
        }
        Schedule newSchedule = Schedule.forCronExpression((String)cronScheduleInfo.getCronExpression(), (TimeZone)timeZone);
        return jobConfig.withSchedule(newSchedule);
    }

    public static JobConfig withCronSchedule(JobConfig jobConfig, @Nullable String cronExpression, TimeZone timeZone) {
        Schedule schedule = jobConfig.getSchedule();
        if (schedule.getType() != Schedule.Type.CRON_EXPRESSION) {
            return jobConfig;
        }
        if (cronExpression == null) {
            return ScheduleUtil.withTimeZone(jobConfig, timeZone);
        }
        Schedule newSchedule = Schedule.forCronExpression((String)cronExpression, (TimeZone)timeZone);
        return jobConfig.withSchedule(newSchedule);
    }

    private static boolean sameTimeZones(TimeZone t1, TimeZone t2) {
        return Objects.equals(t1, t2);
    }

    public static JobConfig getJobConfig(ScheduledJobConfiguration configuration, ManagedScheduledJob job, TimeZone timeZone) {
        return ManagedScheduledJob.isCronJob(job) ? ScheduleUtil.getCronJobConfig(configuration, job, timeZone) : ScheduleUtil.getSimpleJobConfig(configuration, job);
    }

    private static JobConfig getCronJobConfig(ScheduledJobConfiguration configuration, ManagedScheduledJob job, TimeZone timeZone) {
        return ScheduleUtil.withCronSchedule(ScheduleUtil.withLockParameters(job), configuration.getCronSchedule(), timeZone);
    }

    private static JobConfig getSimpleJobConfig(ScheduledJobConfiguration configuration, ManagedScheduledJob job) {
        JobConfig defaultConfig = ScheduleUtil.withLockParameters(job);
        Long repeatInterval = configuration.getRepeatInterval();
        if (repeatInterval == null) {
            return defaultConfig;
        }
        Schedule intervalInfo = Schedule.forInterval((long)repeatInterval, null);
        Map<String, Serializable> countInfo = ScheduleUtil.withUnscheduleJobAfterTimestampMillis(defaultConfig.getParameters(), repeatInterval, ScheduleUtil.getRepeatCount(defaultConfig.getParameters()), null);
        return defaultConfig.withSchedule(intervalInfo).withParameters(countInfo);
    }

    private static JobConfig withLockParameters(ManagedScheduledJob job) {
        if (job.getLockWaitTime() > 0L || job.getTimeoutPolicy().isPresent()) {
            JobConfig defaultConfig = job.getJobConfig();
            HashMap<String, Serializable> parameters = new HashMap<String, Serializable>(defaultConfig.getParameters());
            if (job.getLockWaitTime() > 0L) {
                parameters.put("com.atlassian.confluence.schedule.AbstractManagedScheduledJob.lock_wait_time", Long.valueOf(job.getLockWaitTime()));
            }
            if (job.getTimeoutPolicy().isPresent()) {
                parameters.put("com.atlassian.confluence.schedule.AbstractManagedScheduledJob.timeout_policy", (Serializable)((Object)job.getTimeoutPolicy().get()));
            }
            return defaultConfig.withParameters((Map)ImmutableMap.copyOf(parameters));
        }
        return job.getJobConfig();
    }
}

