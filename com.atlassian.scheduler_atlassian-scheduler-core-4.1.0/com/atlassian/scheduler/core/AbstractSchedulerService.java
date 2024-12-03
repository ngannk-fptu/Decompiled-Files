/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.SchedulerRuntimeException
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.status.JobDetails
 *  com.atlassian.scheduler.status.RunDetails
 *  com.atlassian.scheduler.status.RunOutcome
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.GuardedBy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.scheduler.core;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerRuntimeException;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.core.LifecycleAwareSchedulerService;
import com.atlassian.scheduler.core.RunningJob;
import com.atlassian.scheduler.core.spi.RunDetailsDao;
import com.atlassian.scheduler.core.status.RunDetailsImpl;
import com.atlassian.scheduler.core.util.JobRunnerRegistry;
import com.atlassian.scheduler.core.util.ParameterMapSerializer;
import com.atlassian.scheduler.status.JobDetails;
import com.atlassian.scheduler.status.RunDetails;
import com.atlassian.scheduler.status.RunOutcome;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSchedulerService
implements LifecycleAwareSchedulerService {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSchedulerService.class);
    protected static final Comparator<JobDetails> BY_JOB_ID = new ByJobId();
    private static final int MAX_ATTEMPTS = 100;
    private final Lock idleLock = new ReentrantLock();
    private final Condition idleCondition = this.idleLock.newCondition();
    private final JobRunnerRegistry jobRunnerRegistry = new JobRunnerRegistry();
    private final ConcurrentMap<JobId, RunningJob> runningJobs = new ConcurrentHashMap<JobId, RunningJob>(16);
    private final RunDetailsDao runDetailsDao;
    private final ParameterMapSerializer parameterMapSerializer;
    private volatile LifecycleAwareSchedulerService.State state = LifecycleAwareSchedulerService.State.STANDBY;

    protected AbstractSchedulerService(RunDetailsDao runDetailsDao) {
        this(runDetailsDao, new ParameterMapSerializer());
    }

    protected AbstractSchedulerService(RunDetailsDao runDetailsDao, ParameterMapSerializer parameterMapSerializer) {
        this.runDetailsDao = runDetailsDao;
        this.parameterMapSerializer = parameterMapSerializer;
    }

    public void registerJobRunner(JobRunnerKey jobRunnerKey, JobRunner jobRunner) {
        LOG.debug("registerJobRunner: {}", (Object)jobRunnerKey);
        this.jobRunnerRegistry.registerJobRunner(jobRunnerKey, jobRunner);
    }

    public void unregisterJobRunner(JobRunnerKey jobRunnerKey) {
        LOG.debug("unregisterJobRunner: {}", (Object)jobRunnerKey);
        this.jobRunnerRegistry.unregisterJobRunner(jobRunnerKey);
    }

    public JobRunner getJobRunner(JobRunnerKey jobRunnerKey) {
        return this.jobRunnerRegistry.getJobRunner(jobRunnerKey);
    }

    @Nonnull
    public Set<JobRunnerKey> getRegisteredJobRunnerKeys() {
        return this.jobRunnerRegistry.getRegisteredJobRunnerKeys();
    }

    @Nonnull
    public JobId scheduleJobWithGeneratedId(JobConfig jobConfig) throws SchedulerServiceException {
        JobId jobId = this.generateUniqueJobId();
        LOG.debug("scheduleJobWithGeneratedId: {} -> {}", (Object)jobConfig, (Object)jobId);
        this.scheduleJob(jobId, jobConfig);
        return jobId;
    }

    private JobId generateUniqueJobId() throws SchedulerServiceException {
        for (int i = 0; i < 100; ++i) {
            JobId jobId = JobId.of((String)UUID.randomUUID().toString());
            if (this.getJobDetails(jobId) != null) continue;
            return jobId;
        }
        throw new SchedulerServiceException("Unable to generate a unique job ID");
    }

    public RunDetails addRunDetails(JobId jobId, Date startedAt, RunOutcome runOutcome, @Nullable String message) {
        LOG.debug("addRunDetails: jobId={} startedAt={} runOutcome={} message={}", new Object[]{jobId, startedAt, runOutcome, message});
        Objects.requireNonNull(jobId, "jobId");
        Objects.requireNonNull(startedAt, "startedAt");
        Objects.requireNonNull(runOutcome, "runOutcome");
        long duration = System.currentTimeMillis() - startedAt.getTime();
        RunDetailsImpl runDetails = new RunDetailsImpl(startedAt, runOutcome, duration, message);
        this.runDetailsDao.addRunDetails(jobId, runDetails);
        return runDetails;
    }

    public void preJob() {
    }

    public void postJob() {
    }

    @Override
    public final synchronized void start() throws SchedulerServiceException {
        LOG.debug("{} -> STARTED", (Object)this.state);
        switch (this.state) {
            case STARTED: {
                return;
            }
            case SHUTDOWN: {
                throw new SchedulerServiceException("The scheduler service has been shut down; it cannot be restarted.");
            }
        }
        this.startImpl();
        this.state = LifecycleAwareSchedulerService.State.STARTED;
    }

    @Override
    public final synchronized void standby() throws SchedulerServiceException {
        LOG.debug("{} -> STANDBY", (Object)this.state);
        switch (this.state) {
            case STANDBY: {
                return;
            }
            case SHUTDOWN: {
                throw new SchedulerServiceException("The scheduler service has been shut down; it cannot be restarted.");
            }
        }
        this.cancelJobs();
        this.standbyImpl();
        this.state = LifecycleAwareSchedulerService.State.STANDBY;
    }

    @Override
    public final synchronized void shutdown() {
        LOG.debug("{} -> SHUTDOWN", (Object)this.state);
        if (this.state == LifecycleAwareSchedulerService.State.SHUTDOWN) {
            return;
        }
        this.state = LifecycleAwareSchedulerService.State.SHUTDOWN;
        this.cancelJobs();
        this.shutdownImpl();
    }

    private void cancelJobs() {
        for (RunningJob job : this.runningJobs.values()) {
            job.cancel();
        }
    }

    RunningJob enterJob(JobId jobId, RunningJob job) {
        return this.runningJobs.putIfAbsent(jobId, job);
    }

    void leaveJob(JobId jobId, RunningJob job) {
        if (!this.runningJobs.remove(jobId, job)) {
            throw new IllegalStateException("Invalid call to leaveJob(" + jobId + ", " + job + "; actual running job for that ID is: " + this.runningJobs.get(jobId));
        }
        if (this.runningJobs.isEmpty()) {
            this.signalIdle();
        }
    }

    @GuardedBy(value="idleLock")
    private void signalIdle() {
        this.idleLock.lock();
        try {
            this.idleCondition.signalAll();
        }
        finally {
            this.idleLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean waitUntilIdle(long timeout, TimeUnit units) throws InterruptedException {
        if (this.runningJobs.isEmpty()) {
            return true;
        }
        if (timeout <= 0L) {
            return false;
        }
        this.idleLock.lock();
        try {
            boolean bl = this.waitUntilIdle(units.toNanos(timeout));
            return bl;
        }
        finally {
            this.idleLock.unlock();
        }
    }

    @GuardedBy(value="idleLock")
    boolean waitUntilIdle(long timeoutInNanos) throws InterruptedException {
        long nanosLeft = timeoutInNanos;
        while (nanosLeft > 0L) {
            nanosLeft = this.awaitNanos(nanosLeft);
            if (!this.runningJobs.isEmpty()) continue;
            return true;
        }
        return false;
    }

    @VisibleForTesting
    long awaitNanos(long nanosLeft) throws InterruptedException {
        return this.idleCondition.awaitNanos(nanosLeft);
    }

    @Override
    @Nonnull
    public Collection<RunningJob> getLocallyRunningJobs() {
        return ImmutableList.copyOf(this.runningJobs.values());
    }

    @Override
    @Nonnull
    public final LifecycleAwareSchedulerService.State getState() {
        return this.state;
    }

    protected abstract void startImpl() throws SchedulerServiceException;

    protected abstract void standbyImpl() throws SchedulerServiceException;

    protected abstract void shutdownImpl();

    public ParameterMapSerializer getParameterMapSerializer() {
        return this.parameterMapSerializer;
    }

    protected static SchedulerServiceException checked(SchedulerRuntimeException sre) {
        Throwable cause = sre.getCause();
        if (cause == null) {
            cause = sre;
        }
        return new SchedulerServiceException(cause.toString(), cause);
    }

    static class ByJobId
    implements Comparator<JobDetails>,
    Serializable {
        private static final long serialVersionUID = 1L;

        ByJobId() {
        }

        @Override
        public int compare(JobDetails jd1, JobDetails jd2) {
            return jd1.getJobId().compareTo(jd2.getJobId());
        }
    }
}

