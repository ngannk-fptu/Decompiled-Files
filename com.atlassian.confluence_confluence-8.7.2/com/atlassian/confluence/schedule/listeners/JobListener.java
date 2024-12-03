/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 */
package com.atlassian.confluence.schedule.listeners;

import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;

public interface JobListener {
    public void jobToBeExecuted(JobRunnerRequest var1);

    public void jobWasExecuted(JobRunnerRequest var1, JobRunnerResponse var2);
}

