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

public class ScheduledCronJob
extends AbstractScheduledJob {
    public ScheduledCronJob(String id, JobRunner jobRunner, boolean runOncePerCluster, String cronExpression) {
        super(jobRunner, ScheduledCronJob.toJobConfig(id, runOncePerCluster, cronExpression, -1), false);
    }

    public ScheduledCronJob(String id, JobRunner jobRunner, boolean runOncePerCluster, String cronExpression, int jitterSecs) {
        super(jobRunner, ScheduledCronJob.toJobConfig(id, runOncePerCluster, cronExpression, jitterSecs), false);
    }

    public ScheduledCronJob(String id, JobRunner jobRunner, boolean runOncePerCluster, String cronExpression, boolean clusteredOnly) {
        super(jobRunner, ScheduledCronJob.toJobConfig(id, runOncePerCluster, cronExpression, -1), clusteredOnly);
    }

    public ScheduledCronJob(String id, JobRunner jobRunner, boolean runOncePerCluster, String cronExpression, int jitterSecs, boolean clusteredOnly) {
        super(jobRunner, ScheduledCronJob.toJobConfig(id, runOncePerCluster, cronExpression, jitterSecs), clusteredOnly);
    }

    public static JobConfig toJobConfig(String jobRunnerKey, boolean runOncePerCluster, String cronExpression) {
        return ScheduledCronJob.toJobConfig(jobRunnerKey, runOncePerCluster, cronExpression, -1);
    }

    public static JobConfig toJobConfig(String jobRunnerKey, boolean runOncePerCluster, String cronExpression, int jitterSecs) {
        return JobConfig.forJobRunnerKey((JobRunnerKey)JobRunnerKey.of((String)jobRunnerKey)).withRunMode(runOncePerCluster ? RunMode.RUN_ONCE_PER_CLUSTER : RunMode.RUN_LOCALLY).withSchedule(Schedule.forCronExpression((String)cronExpression)).withParameters(ScheduleUtil.withJitterSecs(jitterSecs));
    }
}

