/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 */
package com.atlassian.confluence.impl.search;

import com.atlassian.confluence.impl.search.IndexFlushScheduler;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;

public class IndexFlushScheduledJob
implements JobRunner {
    private final IndexFlushScheduler indexFlushScheduler;

    public IndexFlushScheduledJob(IndexFlushScheduler indexFlushScheduler) {
        this.indexFlushScheduler = indexFlushScheduler;
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        this.indexFlushScheduler.requestFlush();
        return JobRunnerResponse.success();
    }
}

