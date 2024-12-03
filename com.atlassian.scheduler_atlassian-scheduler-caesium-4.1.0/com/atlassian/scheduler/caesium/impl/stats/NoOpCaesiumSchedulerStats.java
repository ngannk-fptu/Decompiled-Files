/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobId
 */
package com.atlassian.scheduler.caesium.impl.stats;

import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.caesium.impl.stats.CaesiumSchedulerStats;
import com.atlassian.scheduler.config.JobId;

class NoOpCaesiumSchedulerStats
implements CaesiumSchedulerStats {
    NoOpCaesiumSchedulerStats() {
    }

    @Override
    public void jobFlowTakenFromQueue() {
    }

    @Override
    public void jobFlowLocalBegin() {
    }

    @Override
    public void jobFlowLocalStartedTooEarly() {
    }

    @Override
    public void jobFlowLocalPreEnqueue() {
    }

    @Override
    public void jobFlowLocalFailedSchedulingNextRun() {
    }

    @Override
    public void jobFlowLocalPreLaunch() {
    }

    @Override
    public void jobFlowLocalPostLaunch() {
    }

    @Override
    public void jobFlowClusteredBegin() {
    }

    @Override
    public void jobFlowClusteredSkipNoLongerExists() {
    }

    @Override
    public void jobFlowClusteredSkipTooEarly() {
    }

    @Override
    public void jobFlowClusteredSkipFailedToClaim() {
    }

    @Override
    public void jobFlowClusteredPreEnqueue() {
    }

    @Override
    public void jobFlowClusteredPreLaunch() {
    }

    @Override
    public void jobFlowClusteredPostLaunch() {
    }

    @Override
    public void jobRunnerCompletedSuccessfully(JobId jobId, long jobRunTimeMillis) {
    }

    @Override
    public void jobRunnerFailed(JobId jobId, long jobRunTimeMillis, Throwable t) {
    }

    @Override
    public void retryJobScheduled(Throwable throwableOnSchedulingNextRun) {
    }

    @Override
    public void retryJobSerializationError(SchedulerServiceException exceptionOnSerialization) {
    }

    @Override
    public void retryJobScheduleError(Throwable throwableOnSchedulingRetry) {
    }

    @Override
    public void recoveryJobScheduledSuccessfully(Throwable reason) {
    }

    @Override
    public void recoveryJobSchedulingFailed(Throwable throwableOnSchedulingRecoveryJob) {
    }

    @Override
    public void recoveryJobCompletedSuccessfully(int runNumber) {
    }

    @Override
    public void refreshClusteredJobs(int pendingJobCountDifference) {
    }

    @Override
    public void close() {
    }
}

