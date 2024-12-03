/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.google.common.base.Stopwatch
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.schedule.caesium;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.cache.ThreadLocalCache;
import com.atlassian.confluence.impl.schedule.caesium.TimeoutPolicy;
import com.atlassian.confluence.schedule.AbstractManagedScheduledJob;
import com.atlassian.confluence.schedule.listeners.JobListener;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.google.common.base.Stopwatch;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
class JobRunnerWrapper
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(JobRunnerWrapper.class);
    private final JobRunner wrapped;
    private final Supplier<JobListener> listener;
    private final ClusterLockService lockService;

    JobRunnerWrapper(JobRunner wrapped, ClusterLockService lockService, @Nullable Supplier<JobListener> listener) {
        this.wrapped = wrapped;
        this.listener = listener;
        this.lockService = Objects.requireNonNull(lockService);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        JobRunnerKey key = request.getJobConfig().getJobRunnerKey();
        JobId id = request.getJobId();
        String jobName = key + "#" + id;
        JobListener jobListener = this.listener == null ? null : this.listener.get();
        try (Ticker ignored = Timers.start((String)("Scheduled job: " + jobName));){
            JobRunnerResponse response;
            log.info("Scheduled job: {} is starting", (Object)jobName);
            ThreadLocalCache.init();
            if (jobListener != null) {
                try {
                    jobListener.jobToBeExecuted(request);
                }
                catch (RuntimeException e) {
                    log.error("Scheduled job {} jobToBeExecuted failed to run", (Object)jobName, (Object)e);
                }
            }
            try {
                response = this.doRunJob(request);
            }
            catch (RuntimeException e) {
                log.error("Scheduled job {} failed to run", (Object)jobName, (Object)e);
                response = JobRunnerResponse.failed((Throwable)e);
            }
            if (jobListener != null) {
                try {
                    jobListener.jobWasExecuted(request, response);
                }
                catch (RuntimeException e) {
                    log.error("Scheduled job {} jobWasExecuted failed to run", (Object)jobName, (Object)e);
                }
            }
            if (response == null) return response;
            switch (response.getRunOutcome()) {
                case SUCCESS: {
                    log.info("Scheduled job {} succeeded with response {}", (Object)jobName, (Object)response);
                    return response;
                }
                case ABORTED: {
                    log.info("Scheduled job {} aborted with response {}", (Object)jobName, (Object)response);
                    return response;
                }
                default: {
                    log.warn("Scheduled job {} failed with response {}", (Object)jobName, (Object)response);
                    return response;
                }
            }
        }
        finally {
            ThreadLocalCache.dispose();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private JobRunnerResponse doRunJob(JobRunnerRequest request) {
        JobConfig jobConfig = request.getJobConfig();
        if (!JobRunnerWrapper.isLockRequired(jobConfig)) {
            return this.wrapped.runJob(request);
        }
        String jobKey = request.getJobConfig().getJobRunnerKey().toString();
        Long lockWaitTime = Optional.ofNullable((Long)jobConfig.getParameters().get("com.atlassian.confluence.schedule.AbstractManagedScheduledJob.lock_wait_time")).orElse(30000L);
        TimeoutPolicy timeoutPolicy = Optional.ofNullable((TimeoutPolicy)((Object)jobConfig.getParameters().get("com.atlassian.confluence.schedule.AbstractManagedScheduledJob.timeout_policy"))).orElse(AbstractManagedScheduledJob.DEFAULT_TIMEOUT_POLICY);
        Stopwatch stopwatch = Stopwatch.createStarted();
        ClusterLock jobLock = this.lockService.getLockForName("scheduler_" + jobKey);
        try {
            if (jobLock.tryLock(lockWaitTime.longValue(), TimeUnit.MILLISECONDS)) {
                try {
                    JobRunnerResponse jobRunnerResponse = this.wrapped.runJob(request);
                    return jobRunnerResponse;
                }
                finally {
                    jobLock.unlock();
                }
            }
            log.debug("Lock timed out for job {}", (Object)jobKey);
        }
        catch (InterruptedException e) {
            JobRunnerResponse jobRunnerResponse = JobRunnerResponse.failed((Throwable)e);
            return jobRunnerResponse;
        }
        if (TimeoutPolicy.RUN_ON_TIMEOUT.equals((Object)timeoutPolicy)) {
            log.warn("Running job {} after lock time out because TimeoutPolicy is RUN_ON_TIMEOUT. LockWaitTime: {}, real wait time: {}", new Object[]{jobKey, lockWaitTime, stopwatch.elapsed(TimeUnit.MILLISECONDS)});
            return this.wrapped.runJob(request);
        }
        log.warn("Skipping job {} after lock time out because TimeoutPolicy is CANCEL_ON_TIMEOUT. LockWaitTime: {}, real wait time: {}", new Object[]{jobKey, lockWaitTime, stopwatch.elapsed(TimeUnit.MILLISECONDS)});
        return JobRunnerResponse.aborted((String)"Job aborted because lock is timed out, and timeout policy prevents job from running");
        finally {
            stopwatch.stop();
        }
    }

    private static boolean isLockRequired(JobConfig jobConfig) {
        return !RunMode.RUN_LOCALLY.equals((Object)jobConfig.getRunMode()) || jobConfig.getParameters().get("com.atlassian.confluence.schedule.AbstractManagedScheduledJob.lock_wait_time") != null || jobConfig.getParameters().get("com.atlassian.confluence.schedule.AbstractManagedScheduledJob.timeout_policy") != null;
    }
}

