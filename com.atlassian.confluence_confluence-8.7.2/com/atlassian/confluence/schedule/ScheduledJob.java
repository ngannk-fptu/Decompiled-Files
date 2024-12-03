/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 */
package com.atlassian.confluence.schedule;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;

public interface ScheduledJob {
    public JobRunner getJobRunner();

    public JobConfig getJobConfig();

    public boolean isClusteredOnly();

    public static JobId sameJobId(ScheduledJob job) {
        return JobId.of((String)job.getJobConfig().getJobRunnerKey().toString());
    }
}

