/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.util.stats.ManagedStats
 *  com.atlassian.jira.util.stats.MutableLongStats
 *  com.atlassian.jira.util.stats.TopNSerializableStatsWithFrequencies
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobId
 */
package com.atlassian.scheduler.caesium.impl.stats;

import com.atlassian.jira.util.stats.ManagedStats;
import com.atlassian.jira.util.stats.MutableLongStats;
import com.atlassian.jira.util.stats.TopNSerializableStatsWithFrequencies;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.caesium.impl.stats.CaesiumSchedulerStats;
import com.atlassian.scheduler.config.JobId;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

interface ManagedCaesiumSchedulerStats
extends CaesiumSchedulerStats,
ManagedStats {

    public static class Data
    implements ManagedCaesiumSchedulerStats {
        final JobFlow jobFlow = new JobFlow();
        final JobRunnerExecutions jobRunnerExecutions = new JobRunnerExecutions();
        final RetryJob localRetryJob = new RetryJob();
        final RecoveryJob clusterRecoveryJob = new RecoveryJob();
        final MutableLongStats refreshClusterJobsDifference = new MutableLongStats(new long[]{-100L, -10L, -1L, 0L, 1L, 10L, 100L});

        private static TopNSerializableStatsWithFrequencies<String> newReasonStats() {
            return new TopNSerializableStatsWithFrequencies(50, 10, 200);
        }

        public String getStatsName() {
            return "SchedulerStats";
        }

        @Override
        public void jobFlowTakenFromQueue() {
            this.jobFlow.takenFromQueue.incrementAndGet();
        }

        @Override
        public void jobFlowLocalBegin() {
            this.jobFlow.localBegin.incrementAndGet();
        }

        @Override
        public void jobFlowLocalStartedTooEarly() {
            this.jobFlow.localStartedTooEarly.incrementAndGet();
        }

        @Override
        public void jobFlowLocalPreEnqueue() {
            this.jobFlow.localPreEnqueue.incrementAndGet();
        }

        @Override
        public void jobFlowLocalFailedSchedulingNextRun() {
            this.jobFlow.localFailedSchedulingNextRun.incrementAndGet();
        }

        @Override
        public void jobFlowLocalPreLaunch() {
            this.jobFlow.localPreLaunch.incrementAndGet();
        }

        @Override
        public void jobFlowLocalPostLaunch() {
            this.jobFlow.localPostLaunch.incrementAndGet();
        }

        @Override
        public void jobFlowClusteredBegin() {
            this.jobFlow.clusteredBegin.incrementAndGet();
        }

        @Override
        public void jobFlowClusteredSkipNoLongerExists() {
            this.jobFlow.clusteredSkipNoLongerExists.incrementAndGet();
        }

        @Override
        public void jobFlowClusteredSkipTooEarly() {
            this.jobFlow.clusteredSkipTooEarly.incrementAndGet();
        }

        @Override
        public void jobFlowClusteredSkipFailedToClaim() {
            this.jobFlow.clusteredSkipFailedToClaim.incrementAndGet();
        }

        @Override
        public void jobFlowClusteredPreEnqueue() {
            this.jobFlow.clusteredPreEnqueue.incrementAndGet();
        }

        @Override
        public void jobFlowClusteredPreLaunch() {
            this.jobFlow.clusteredPreLaunch.incrementAndGet();
        }

        @Override
        public void jobFlowClusteredPostLaunch() {
            this.jobFlow.clusteredPostLaunch.incrementAndGet();
        }

        @Override
        public void jobRunnerCompletedSuccessfully(JobId jobId, long jobRunTimeMillis) {
            this.jobRunnerExecutions.successful.jobRunTimeMillis.accept(jobRunTimeMillis);
            this.jobRunnerExecutions.successful.jobIds.store((Object)jobId);
        }

        @Override
        public void jobRunnerFailed(JobId jobId, long jobRunTimeMillis, Throwable t) {
            this.jobRunnerExecutions.failed.jobRunTimeMillis.accept(jobRunTimeMillis);
            this.jobRunnerExecutions.failed.jobIds.store((Object)jobId);
            this.jobRunnerExecutions.failed.reasons.store((Object)Data.getThrowableAsString(t));
        }

        @Override
        public void retryJobScheduled(Throwable throwableOnSchedulingNextRun) {
            this.localRetryJob.scheduleCount.incrementAndGet();
            this.localRetryJob.scheduleReasons.store((Object)Data.getThrowableAsString(throwableOnSchedulingNextRun));
        }

        @Override
        public void retryJobSerializationError(SchedulerServiceException exceptionOnSerialization) {
            this.localRetryJob.fatalSerializationErrorCount.incrementAndGet();
            this.localRetryJob.fatalSerializationErrors.store((Object)Data.getThrowableAsString((Throwable)exceptionOnSerialization));
        }

        @Override
        public void retryJobScheduleError(Throwable throwableOnSchedulingRetry) {
            this.localRetryJob.fatalSchedulingErrorCount.incrementAndGet();
            this.localRetryJob.fatalSchedulingErrors.store((Object)Data.getThrowableAsString(throwableOnSchedulingRetry));
        }

        @Override
        public void recoveryJobScheduledSuccessfully(Throwable reason) {
            this.clusterRecoveryJob.scheduledSuccessfully.count.incrementAndGet();
            this.clusterRecoveryJob.scheduledSuccessfully.reasons.store((Object)Data.getThrowableAsString(reason));
        }

        @Override
        public void recoveryJobSchedulingFailed(Throwable throwableOnSchedulingRecoveryJob) {
            this.clusterRecoveryJob.schedulingFailed.count.incrementAndGet();
            this.clusterRecoveryJob.schedulingFailed.reasons.store((Object)Data.getThrowableAsString(throwableOnSchedulingRecoveryJob));
        }

        @Override
        public void recoveryJobCompletedSuccessfully(int runNumber) {
            this.clusterRecoveryJob.completedSuccessfully.runNumber.accept((long)runNumber);
        }

        @Override
        public void refreshClusteredJobs(int pendingJobCountDifference) {
            this.refreshClusterJobsDifference.accept((long)pendingJobCountDifference);
        }

        private static String getThrowableAsString(Throwable reason) {
            String exceptionClass = reason.getClass().getName();
            String exceptionMessage = reason.getMessage();
            StackTraceElement[] stackTrace = reason.getStackTrace();
            String topOfStack = stackTrace.length > 0 ? stackTrace[0].toString() : "(unknown)";
            return exceptionClass + ": " + exceptionMessage + " at " + topOfStack;
        }

        static /* synthetic */ TopNSerializableStatsWithFrequencies access$200() {
            return Data.newReasonStats();
        }

        static class RecoveryJob {
            final Scheduled scheduledSuccessfully = new Scheduled();
            final Scheduled schedulingFailed = new Scheduled();
            final CompletedSuccessfully completedSuccessfully = new CompletedSuccessfully();

            RecoveryJob() {
            }

            static class CompletedSuccessfully {
                final MutableLongStats runNumber = new MutableLongStats(new long[]{1L, 2L, 5L});

                CompletedSuccessfully() {
                }
            }

            static class Scheduled {
                final AtomicLong count = new AtomicLong();
                final TopNSerializableStatsWithFrequencies<String> reasons = Data.access$200();

                Scheduled() {
                }
            }
        }

        static class RetryJob {
            final AtomicLong scheduleCount = new AtomicLong();
            final TopNSerializableStatsWithFrequencies<String> scheduleReasons = Data.access$200();
            final AtomicLong fatalSerializationErrorCount = new AtomicLong();
            final TopNSerializableStatsWithFrequencies<String> fatalSerializationErrors = Data.access$200();
            final AtomicLong fatalSchedulingErrorCount = new AtomicLong();
            final TopNSerializableStatsWithFrequencies<String> fatalSchedulingErrors = Data.access$200();

            RetryJob() {
            }
        }

        static class JobRunnerExecutions {
            final Successful successful = new Successful();
            final Failed failed = new Failed();

            JobRunnerExecutions() {
            }

            private static MutableLongStats newJobRunTimeMillisStats() {
                return new MutableLongStats(new long[]{100L, TimeUnit.SECONDS.toMillis(1L), TimeUnit.SECONDS.toMillis(10L), TimeUnit.MINUTES.toMillis(1L), TimeUnit.MINUTES.toMillis(10L), TimeUnit.HOURS.toMillis(1L), TimeUnit.HOURS.toMillis(10L)});
            }

            private static TopNSerializableStatsWithFrequencies<JobId> newJobIdsStats() {
                return new TopNSerializableStatsWithFrequencies(500, 10, 100);
            }

            static /* synthetic */ MutableLongStats access$000() {
                return JobRunnerExecutions.newJobRunTimeMillisStats();
            }

            static /* synthetic */ TopNSerializableStatsWithFrequencies access$100() {
                return JobRunnerExecutions.newJobIdsStats();
            }

            static class Failed {
                final MutableLongStats jobRunTimeMillis = JobRunnerExecutions.access$000();
                final TopNSerializableStatsWithFrequencies<JobId> jobIds = JobRunnerExecutions.access$100();
                final TopNSerializableStatsWithFrequencies<String> reasons = Data.access$200();

                Failed() {
                }
            }

            static class Successful {
                final MutableLongStats jobRunTimeMillis = JobRunnerExecutions.access$000();
                final TopNSerializableStatsWithFrequencies<JobId> jobIds = JobRunnerExecutions.access$100();

                Successful() {
                }
            }
        }

        static class JobFlow {
            AtomicLong takenFromQueue = new AtomicLong();
            AtomicLong localBegin = new AtomicLong();
            AtomicLong localStartedTooEarly = new AtomicLong();
            AtomicLong localPreEnqueue = new AtomicLong();
            AtomicLong localFailedSchedulingNextRun = new AtomicLong();
            AtomicLong localPreLaunch = new AtomicLong();
            AtomicLong localPostLaunch = new AtomicLong();
            AtomicLong clusteredBegin = new AtomicLong();
            AtomicLong clusteredSkipNoLongerExists = new AtomicLong();
            AtomicLong clusteredSkipTooEarly = new AtomicLong();
            AtomicLong clusteredSkipFailedToClaim = new AtomicLong();
            AtomicLong clusteredPreEnqueue = new AtomicLong();
            AtomicLong clusteredPreLaunch = new AtomicLong();
            AtomicLong clusteredPostLaunch = new AtomicLong();

            JobFlow() {
            }
        }
    }
}

