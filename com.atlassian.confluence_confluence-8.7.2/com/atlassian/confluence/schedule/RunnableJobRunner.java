/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.schedule;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import org.checkerframework.checker.nullness.qual.Nullable;

public class RunnableJobRunner
implements JobRunner {
    private final Runnable task;

    public RunnableJobRunner(Runnable task) {
        this.task = task;
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        this.task.run();
        return null;
    }
}

