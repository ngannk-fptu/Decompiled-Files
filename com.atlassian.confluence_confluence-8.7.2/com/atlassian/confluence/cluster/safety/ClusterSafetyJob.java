/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.cluster.safety;

import com.atlassian.confluence.cluster.safety.ClusterSafetyManager;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ClusterSafetyJob
implements JobRunner {
    private final ClusterSafetyManager clusterSafetyManager;

    public ClusterSafetyJob(ClusterSafetyManager clusterSafetyManager) {
        this.clusterSafetyManager = clusterSafetyManager;
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        this.clusterSafetyManager.verify(request.getJobConfig().getSchedule().getIntervalScheduleInfo().getIntervalInMillis());
        return null;
    }
}

