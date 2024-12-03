/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.backgroundjob;

import com.atlassian.confluence.impl.backgroundjob.BackgroundJobService;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import javax.annotation.Nullable;

public class BackgroundJobServiceScheduledJob
implements JobRunner {
    private final BackgroundJobService backgroundJobService;

    public BackgroundJobServiceScheduledJob(BackgroundJobService backgroundJobService) {
        this.backgroundJobService = backgroundJobService;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        this.backgroundJobService.processNextJobs();
        return JobRunnerResponse.success();
    }
}

