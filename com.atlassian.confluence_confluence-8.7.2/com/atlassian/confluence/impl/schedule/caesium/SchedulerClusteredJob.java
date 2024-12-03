/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.caesium.impl.ImmutableClusteredJob
 *  com.atlassian.scheduler.caesium.spi.ClusteredJob
 *  com.atlassian.scheduler.config.CronScheduleInfo
 *  com.atlassian.scheduler.config.IntervalScheduleInfo
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.config.Schedule$Type
 *  com.google.common.io.ByteStreams
 */
package com.atlassian.confluence.impl.schedule.caesium;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.scheduler.caesium.impl.ImmutableClusteredJob;
import com.atlassian.scheduler.caesium.spi.ClusteredJob;
import com.atlassian.scheduler.config.CronScheduleInfo;
import com.atlassian.scheduler.config.IntervalScheduleInfo;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.Schedule;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.TimeZone;

public class SchedulerClusteredJob
extends ConfluenceEntityObject {
    private String jobId;
    private Date nextRunTime;
    private long version;
    private String jobRunnerKey;
    private InputStream rawParameters;
    private char schedType;
    private String cronExpression;
    private String cronTimeZone;
    private Date intervalFirstRunTime;
    private long intervalMillis;

    public ClusteredJob toClusteredJob() throws IOException {
        byte[] bytes = this.rawParameters == null || this.rawParameters.available() == 0 ? null : ByteStreams.toByteArray((InputStream)this.rawParameters);
        return ImmutableClusteredJob.builder().jobId(JobId.of((String)this.jobId)).jobRunnerKey(JobRunnerKey.of((String)this.jobRunnerKey)).schedule(SchedulerClusteredJob.toSchedule(this.schedType, this.cronExpression, this.cronTimeZone, this.intervalFirstRunTime, this.intervalMillis)).nextRunTime(this.nextRunTime).version(this.version).parameters(bytes).build();
    }

    public static Schedule toSchedule(char schedType, String cronExpression, String cronTimeZone, Date intervalFirstRunTime, long intervalMillis) {
        Schedule.Type scheduleType = SchedulerClusteredJob.charToScheduleType(schedType);
        switch (scheduleType) {
            case CRON_EXPRESSION: {
                TimeZone timeZone = cronTimeZone == null ? null : TimeZone.getTimeZone(cronTimeZone);
                return Schedule.forCronExpression((String)cronExpression, (TimeZone)timeZone);
            }
            case INTERVAL: {
                return Schedule.forInterval((long)intervalMillis, (Date)intervalFirstRunTime);
            }
        }
        throw new IllegalArgumentException("Unknown Schedule.Type: " + scheduleType);
    }

    public static SchedulerClusteredJob fromClusterJob(ClusteredJob clusteredJob) {
        SchedulerClusteredJob ret = new SchedulerClusteredJob();
        ret.jobId = clusteredJob.getJobId().toString();
        ret.nextRunTime = clusteredJob.getNextRunTime();
        ret.version = clusteredJob.getVersion();
        ret.jobRunnerKey = clusteredJob.getJobRunnerKey().toString();
        byte[] bytes = clusteredJob.getRawParameters();
        ret.rawParameters = bytes == null ? null : new ByteArrayInputStream(bytes);
        Schedule sched = clusteredJob.getSchedule();
        ret.schedType = SchedulerClusteredJob.scheduleTypeToChar(sched.getType());
        if (sched.getType() == Schedule.Type.CRON_EXPRESSION) {
            CronScheduleInfo cron = sched.getCronScheduleInfo();
            TimeZone timeZone = cron.getTimeZone();
            ret.cronExpression = cron.getCronExpression();
            ret.cronTimeZone = timeZone == null ? null : timeZone.getID();
        } else {
            IntervalScheduleInfo interval = sched.getIntervalScheduleInfo();
            ret.intervalFirstRunTime = interval.getFirstRunTime();
            ret.intervalMillis = interval.getIntervalInMillis();
        }
        return ret;
    }

    public static char scheduleTypeToChar(Schedule.Type scheduleType) {
        switch (scheduleType) {
            case CRON_EXPRESSION: {
                return 'C';
            }
            case INTERVAL: {
                return 'I';
            }
        }
        throw new IllegalArgumentException("Unknown Schedule.Type: " + scheduleType + "; it should be CRON_EXPRESSION or INTERVAL");
    }

    public static Schedule.Type charToScheduleType(char schedType) {
        switch (schedType) {
            case 'C': {
                return Schedule.Type.CRON_EXPRESSION;
            }
            case 'I': {
                return Schedule.Type.INTERVAL;
            }
        }
        throw new IllegalArgumentException("Cannot convert " + schedType + " to " + Schedule.Type.class);
    }

    public String getJobId() {
        return this.jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Date getNextRunTime() {
        return this.nextRunTime;
    }

    public void setNextRunTime(Date nextRunTime) {
        this.nextRunTime = nextRunTime;
    }

    public long getVersion() {
        return this.version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getJobRunnerKey() {
        return this.jobRunnerKey;
    }

    public void setJobRunnerKey(String jobRunnerKey) {
        this.jobRunnerKey = jobRunnerKey;
    }

    public InputStream getRawParameters() {
        return this.rawParameters;
    }

    public void setRawParameters(InputStream rawParameters) {
        this.rawParameters = rawParameters;
    }

    public char getSchedType() {
        return this.schedType;
    }

    public void setSchedType(char schedType) {
        this.schedType = schedType;
    }

    public String getCronExpression() {
        return this.cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getCronTimeZone() {
        return this.cronTimeZone;
    }

    public void setCronTimeZone(String cronTimeZone) {
        this.cronTimeZone = cronTimeZone;
    }

    public Date getIntervalFirstRunTime() {
        return this.intervalFirstRunTime;
    }

    public void setIntervalFirstRunTime(Date intervalFirstRunTime) {
        this.intervalFirstRunTime = intervalFirstRunTime;
    }

    public long getIntervalMillis() {
        return this.intervalMillis;
    }

    public void setIntervalMillis(long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }
}

