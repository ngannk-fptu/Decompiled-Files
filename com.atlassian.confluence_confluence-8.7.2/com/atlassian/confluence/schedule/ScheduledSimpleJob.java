/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 */
package com.atlassian.confluence.schedule;

import com.atlassian.confluence.schedule.AbstractScheduledJob;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;

public class ScheduledSimpleJob
extends AbstractScheduledJob {
    public ScheduledSimpleJob(String id, JobRunner jobRunner, boolean runOncePerCluster, long repeatInterval) {
        super(jobRunner, ScheduledSimpleJob.toJobConfig(id, runOncePerCluster, repeatInterval, -1, -1), false);
    }

    public ScheduledSimpleJob(String id, JobRunner jobRunner, boolean runOncePerCluster, long repeatInterval, boolean clusteredOnly) {
        super(jobRunner, ScheduledSimpleJob.toJobConfig(id, runOncePerCluster, repeatInterval, -1, -1), clusteredOnly);
    }

    public ScheduledSimpleJob(String id, JobRunner jobRunner, boolean runOncePerCluster, long repeatInterval, int repeatCount, int jitterSecs) {
        super(jobRunner, ScheduledSimpleJob.toJobConfig(id, runOncePerCluster, repeatInterval, repeatCount, jitterSecs), false);
    }

    public ScheduledSimpleJob(String id, JobRunner jobRunner, boolean runOncePerCluster, long repeatInterval, int repeatCount, int jitterSecs, boolean clusteredOnly) {
        super(jobRunner, ScheduledSimpleJob.toJobConfig(id, runOncePerCluster, repeatInterval, repeatCount, jitterSecs), clusteredOnly);
    }

    public static JobConfig toJobConfig(String jobRunnerKey, boolean runOncePerCluster, long repeatInterval, int repeatCount) {
        return ScheduledSimpleJob.toJobConfig(jobRunnerKey, runOncePerCluster, repeatInterval, repeatCount, -1);
    }

    public static JobConfig toJobConfig(String jobRunnerKey, boolean runOncePerCluster, long repeatInterval, int repeatCount, int jitterSecs) {
        return JobConfig.forJobRunnerKey((JobRunnerKey)JobRunnerKey.of((String)jobRunnerKey)).withRunMode(runOncePerCluster ? RunMode.RUN_ONCE_PER_CLUSTER : RunMode.RUN_LOCALLY).withSchedule(Schedule.forInterval((long)repeatInterval, null)).withParameters(ScheduleUtil.withJitterSecs(ScheduleUtil.withUnscheduleJobAfterTimestampMillis(repeatInterval, repeatCount, null), jitterSecs));
    }
}

