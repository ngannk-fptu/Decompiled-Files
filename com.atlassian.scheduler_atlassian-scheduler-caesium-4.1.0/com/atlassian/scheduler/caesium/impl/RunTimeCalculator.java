/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.CronScheduleInfo
 *  com.atlassian.scheduler.config.IntervalScheduleInfo
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.core.spi.SchedulerServiceConfiguration
 *  com.atlassian.scheduler.cron.CronSyntaxException
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 */
package com.atlassian.scheduler.caesium.impl;

import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.caesium.cron.parser.CronExpressionParser;
import com.atlassian.scheduler.caesium.cron.rule.CronExpression;
import com.atlassian.scheduler.caesium.cron.rule.DateTimeTemplate;
import com.atlassian.scheduler.config.CronScheduleInfo;
import com.atlassian.scheduler.config.IntervalScheduleInfo;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.core.spi.SchedulerServiceConfiguration;
import com.atlassian.scheduler.cron.CronSyntaxException;
import com.google.common.annotations.VisibleForTesting;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class RunTimeCalculator {
    final SchedulerServiceConfiguration config;

    public RunTimeCalculator(SchedulerServiceConfiguration config) {
        this.config = config;
    }

    @Nonnull
    public Date firstRunTime(JobId jobId, JobConfig jobConfig) throws SchedulerServiceException {
        Date nextRunTime = this.nextRunTime(jobConfig.getSchedule(), null);
        if (nextRunTime == null) {
            throw new SchedulerServiceException("Job '" + jobId + "' would never run: " + jobConfig.getSchedule());
        }
        return nextRunTime;
    }

    @Nullable
    public Date nextRunTime(Schedule schedule, @Nullable Date prevRunTime) throws CronSyntaxException {
        Objects.requireNonNull(schedule, "schedule");
        switch (schedule.getType()) {
            case INTERVAL: {
                return this.nextRunTime(schedule.getIntervalScheduleInfo(), prevRunTime);
            }
            case CRON_EXPRESSION: {
                return this.nextRunTime(schedule.getCronScheduleInfo(), prevRunTime);
            }
        }
        throw new IllegalArgumentException("Unsupported schedule type: " + schedule.getType());
    }

    @Nullable
    private Date nextRunTime(IntervalScheduleInfo info, @Nullable Date prevRunTime) {
        if (prevRunTime == null) {
            Date firstRun = info.getFirstRunTime();
            Date now = this.now();
            return firstRun != null && firstRun.getTime() > now.getTime() ? firstRun : now;
        }
        if (info.getIntervalInMillis() == 0L) {
            return null;
        }
        return new Date(prevRunTime.getTime() + info.getIntervalInMillis());
    }

    @Nullable
    private Date nextRunTime(CronScheduleInfo info, @Nullable Date prevRunTime) throws CronSyntaxException {
        String cronExpression = info.getCronExpression();
        TimeZone timeZone = this.getTimeZone(info);
        CronExpression cron = CronExpressionParser.parse(cronExpression);
        Date runTimeForCalc = prevRunTime == null ? this.now() : prevRunTime;
        DateTimeTemplate when = new DateTimeTemplate(runTimeForCalc, DateTimeZone.forTimeZone((TimeZone)timeZone));
        while (cron.next(when)) {
            DateTime dateTime = when.toDateTime();
            if (dateTime == null) continue;
            return new Date(dateTime.getMillis());
        }
        return null;
    }

    private TimeZone getTimeZone(CronScheduleInfo info) {
        TimeZone timeZone = info.getTimeZone();
        if (timeZone == null && (timeZone = this.config.getDefaultTimeZone()) == null) {
            timeZone = TimeZone.getDefault();
        }
        return timeZone;
    }

    @VisibleForTesting
    Date now() {
        return new Date();
    }
}

