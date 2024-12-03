/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.eviction.SynchronyDataService
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.schedule.jobs.synchrony.eviction;

import com.atlassian.confluence.api.service.eviction.SynchronyDataService;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import javax.annotation.Nullable;

public class SynchronyEventsSoftRemovalScheduledJob
implements JobRunner {
    private static final int SOFT_REMOVAL_THRESHOLD_HOURS = Integer.getInteger("synchrony.eviction.soft.job.threshold.hours", 72);
    private static final int CONTENT_COUNT = Integer.getInteger("synchrony.eviction.soft.job.content.batch.count", 1000);
    private final SynchronyDataService synchronyDataService;

    public SynchronyEventsSoftRemovalScheduledJob(SynchronyDataService synchronyDataService) {
        this.synchronyDataService = synchronyDataService;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        this.synchronyDataService.softRemoveHistoryOlderThan(SOFT_REMOVAL_THRESHOLD_HOURS, CONTENT_COUNT);
        return JobRunnerResponse.success();
    }
}

