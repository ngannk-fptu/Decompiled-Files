/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobId
 */
package com.atlassian.scheduler.caesium.impl.stats;

import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobId;
import java.io.Closeable;

public interface CaesiumSchedulerStats
extends Closeable {
    public static final String STATS_NAME = "SchedulerStats";

    public void jobFlowTakenFromQueue();

    public void jobFlowLocalBegin();

    public void jobFlowLocalStartedTooEarly();

    public void jobFlowLocalPreEnqueue();

    public void jobFlowLocalFailedSchedulingNextRun();

    public void jobFlowLocalPreLaunch();

    public void jobFlowLocalPostLaunch();

    public void jobFlowClusteredBegin();

    public void jobFlowClusteredSkipNoLongerExists();

    public void jobFlowClusteredSkipTooEarly();

    public void jobFlowClusteredSkipFailedToClaim();

    public void jobFlowClusteredPreEnqueue();

    public void jobFlowClusteredPreLaunch();

    public void jobFlowClusteredPostLaunch();

    public void jobRunnerCompletedSuccessfully(JobId var1, long var2);

    public void jobRunnerFailed(JobId var1, long var2, Throwable var4);

    public void retryJobScheduled(Throwable var1);

    public void retryJobSerializationError(SchedulerServiceException var1);

    public void retryJobScheduleError(Throwable var1);

    public void recoveryJobScheduledSuccessfully(Throwable var1);

    public void recoveryJobSchedulingFailed(Throwable var1);

    public void recoveryJobCompletedSuccessfully(int var1);

    public void refreshClusteredJobs(int var1);
}

