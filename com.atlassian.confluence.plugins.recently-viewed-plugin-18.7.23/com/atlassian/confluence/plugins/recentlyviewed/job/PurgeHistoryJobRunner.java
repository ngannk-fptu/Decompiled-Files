/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.recentlyviewed.job;

import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ParametersAreNonnullByDefault
public class PurgeHistoryJobRunner
implements JobRunner {
    private final RecentlyViewedManager recentlyViewedManager;

    @Autowired
    public PurgeHistoryJobRunner(RecentlyViewedManager recentlyViewedManager) {
        this.recentlyViewedManager = recentlyViewedManager;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        this.recentlyViewedManager.deleteOldEntries();
        return null;
    }
}

