/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.schedule.jobs;

import com.atlassian.confluence.schedule.managers.SchedulerRunDetailsManager;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerRunDetailsPurgeJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(SchedulerRunDetailsPurgeJob.class);
    private final SchedulerRunDetailsManager schedulerRunDetailsManager;

    public SchedulerRunDetailsPurgeJob(SchedulerRunDetailsManager schedulerRunDetailsManager) {
        this.schedulerRunDetailsManager = schedulerRunDetailsManager;
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        log.info("Purging outdated run details...");
        this.schedulerRunDetailsManager.purgeOldRunDetails();
        return JobRunnerResponse.success((String)"The old run details have been purged successfully.");
    }
}

