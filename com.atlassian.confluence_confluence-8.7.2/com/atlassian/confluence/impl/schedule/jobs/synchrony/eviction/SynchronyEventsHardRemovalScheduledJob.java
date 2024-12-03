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

public class SynchronyEventsHardRemovalScheduledJob
implements JobRunner {
    private static final int HARD_REMOVAL_HOURS_THRESHOLD = Integer.getInteger("synchrony.eviction.hard.job.threshold.hours", 360);
    private final SynchronyDataService synchronyDataService;

    public SynchronyEventsHardRemovalScheduledJob(SynchronyDataService synchronyDataService) {
        this.synchronyDataService = synchronyDataService;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        this.synchronyDataService.hardRemoveHistoryOlderThan(HARD_REMOVAL_HOURS_THRESHOLD);
        return JobRunnerResponse.success();
    }
}

