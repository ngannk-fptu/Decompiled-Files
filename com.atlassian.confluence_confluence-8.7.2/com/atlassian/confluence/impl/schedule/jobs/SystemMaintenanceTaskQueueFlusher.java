/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 */
package com.atlassian.confluence.impl.schedule.jobs;

import com.atlassian.confluence.impl.system.SystemMaintenanceTaskQueue;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;

public class SystemMaintenanceTaskQueueFlusher
implements JobRunner {
    private final SystemMaintenanceTaskQueue queue;

    public SystemMaintenanceTaskQueueFlusher(SystemMaintenanceTaskQueue queue) {
        this.queue = queue;
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        this.queue.processEntries();
        return JobRunnerResponse.success();
    }
}

