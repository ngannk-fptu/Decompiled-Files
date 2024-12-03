/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerRuntimeException
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.CronScheduleInfo
 *  com.atlassian.scheduler.config.IntervalScheduleInfo
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.core.AbstractSchedulerService
 *  com.atlassian.scheduler.core.JobLauncher
 *  com.atlassian.scheduler.core.spi.RunDetailsDao
 *  com.atlassian.scheduler.core.status.LazyJobDetails
 *  com.atlassian.scheduler.core.status.SimpleJobDetails
 *  com.atlassian.scheduler.core.util.CronExpressionQuantizer
 *  com.atlassian.scheduler.core.util.ParameterMapSerializer
 *  com.atlassian.scheduler.core.util.TimeIntervalQuantizer
 *  com.atlassian.scheduler.cron.CronSyntaxException
 *  com.atlassian.scheduler.status.JobDetails
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Stopwatch
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.scheduler.caesium.impl;

import com.atlassian.annotations.Internal;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerRuntimeException;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.caesium.impl.ImmutableClusteredJob;
import com.atlassian.scheduler.caesium.impl.QueuedJob;
import com.atlassian.scheduler.caesium.impl.RunTimeCalculator;
import com.atlassian.scheduler.caesium.impl.SchedulerQueue;
import com.atlassian.scheduler.caesium.impl.SchedulerQueueImpl;
import com.atlassian.scheduler.caesium.impl.SchedulerQueueWorker;
import com.atlassian.scheduler.caesium.impl.WorkerThreadFactory;
import com.atlassian.scheduler.caesium.impl.stats.CaesiumSchedulerStats;
import com.atlassian.scheduler.caesium.impl.stats.SafeCaesiumSchedulerStatsFactory;
import com.atlassian.scheduler.caesium.migration.LazyMigratingParameterMapSerializer;
import com.atlassian.scheduler.caesium.spi.CaesiumSchedulerConfiguration;
import com.atlassian.scheduler.caesium.spi.ClusteredJob;
import com.atlassian.scheduler.caesium.spi.ClusteredJobDao;
import com.atlassian.scheduler.config.CronScheduleInfo;
import com.atlassian.scheduler.config.IntervalScheduleInfo;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.core.AbstractSchedulerService;
import com.atlassian.scheduler.core.JobLauncher;
import com.atlassian.scheduler.core.spi.RunDetailsDao;
import com.atlassian.scheduler.core.status.LazyJobDetails;
import com.atlassian.scheduler.core.status.SimpleJobDetails;
import com.atlassian.scheduler.core.util.CronExpressionQuantizer;
import com.atlassian.scheduler.core.util.ParameterMapSerializer;
import com.atlassian.scheduler.core.util.TimeIntervalQuantizer;
import com.atlassian.scheduler.cron.CronSyntaxException;
import com.atlassian.scheduler.status.JobDetails;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaesiumSchedulerService
extends AbstractSchedulerService {
    private static final Logger LOG = LoggerFactory.getLogger(CaesiumSchedulerService.class);
    private static final int DEFAULT_JOB_MAP_SIZE = 256;
    static final JobId RECOVERY_JOB_ID = JobId.of((String)"CaesiumSchedulerService.RecoveryJob");
    static final JobId REFRESH_JOB_ID = JobId.of((String)"CaesiumSchedulerService.RefreshJob");
    static final JobRunnerKey RECOVERY_JOB_RUNNER_KEY = JobRunnerKey.of((String)"CaesiumSchedulerService.RecoveryJob");
    static final JobRunnerKey REFRESH_JOB_RUNNER_KEY = JobRunnerKey.of((String)"CaesiumSchedulerService.RefreshJob");
    private static final int MAX_TRIES = 50;
    private static final int RECOVERY_INTERVAL_SECONDS = 60;
    private static final int DEFAULT_WORKER_COUNT = 4;
    private final ConcurrentMap<JobId, JobDetails> localJobs = new ConcurrentHashMap<JobId, JobDetails>(256);
    private final RecoveryJob recoveryJob = new RecoveryJob();
    private final RefreshJob refreshJob = new RefreshJob();
    private final AtomicBoolean started = new AtomicBoolean();
    private final ClusteredJobDao clusteredJobDao;
    private final CaesiumSchedulerConfiguration config;
    private final RunDetailsDao runDetailsDao;
    private final SchedulerQueue queue;
    private final RunTimeCalculator runTimeCalculator;
    private CaesiumSchedulerStats stats;

    public CaesiumSchedulerService(CaesiumSchedulerConfiguration config, RunDetailsDao runDetailsDao, ClusteredJobDao clusteredJobDao) {
        this(config, runDetailsDao, clusteredJobDao, CaesiumSchedulerService.createParameterMapSerializer(config));
    }

    public CaesiumSchedulerService(CaesiumSchedulerConfiguration config, RunDetailsDao runDetailsDao, ClusteredJobDao clusteredJobDao, ParameterMapSerializer serializer) {
        this(config, runDetailsDao, clusteredJobDao, serializer, () -> false, null);
    }

    public CaesiumSchedulerService(CaesiumSchedulerConfiguration config, RunDetailsDao runDetailsDao, ClusteredJobDao clusteredJobDao, ParameterMapSerializer serializer, Supplier<Boolean> pausedCondition, @Nullable Long pauseCheckTimeMs) {
        this(config, runDetailsDao, clusteredJobDao, new SchedulerQueueImpl(clusteredJobDao, pausedCondition, pauseCheckTimeMs), new RunTimeCalculator(config), serializer);
    }

    @VisibleForTesting
    CaesiumSchedulerService(CaesiumSchedulerConfiguration config, RunDetailsDao runDetailsDao, ClusteredJobDao clusteredJobDao, SchedulerQueue queue, RunTimeCalculator runTimeCalculator) {
        this(config, runDetailsDao, clusteredJobDao, queue, runTimeCalculator, CaesiumSchedulerService.createParameterMapSerializer(config));
    }

    CaesiumSchedulerService(CaesiumSchedulerConfiguration config, RunDetailsDao runDetailsDao, ClusteredJobDao clusteredJobDao, SchedulerQueue queue, RunTimeCalculator runTimeCalculator, ParameterMapSerializer serializer) {
        super(runDetailsDao, serializer);
        this.config = Objects.requireNonNull(config);
        this.runDetailsDao = Objects.requireNonNull(runDetailsDao);
        this.clusteredJobDao = Objects.requireNonNull(clusteredJobDao);
        this.queue = Objects.requireNonNull(queue);
        this.runTimeCalculator = Objects.requireNonNull(runTimeCalculator);
        this.stats = SafeCaesiumSchedulerStatsFactory.create();
    }

    public void registerJobRunner(JobRunnerKey jobRunnerKey, JobRunner jobRunner) {
        super.registerJobRunner(jobRunnerKey, (JobRunner)new JobRunnerWithStats(jobRunner));
    }

    public void scheduleJob(JobId jobId, JobConfig jobConfig) throws SchedulerServiceException {
        Objects.requireNonNull(jobId, "jobId");
        Objects.requireNonNull(jobConfig, "jobConfig");
        try {
            LOG.debug("scheduleJob: {}: {}", (Object)jobId, (Object)jobConfig);
            switch (jobConfig.getRunMode()) {
                case RUN_LOCALLY: {
                    this.scheduleLocalJob(jobId, jobConfig);
                    break;
                }
                case RUN_ONCE_PER_CLUSTER: {
                    this.scheduleClusteredJob(jobId, jobConfig);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unsupported run mode: " + jobConfig.getRunMode());
                }
            }
        }
        catch (SchedulerRuntimeException sre) {
            throw CaesiumSchedulerService.checked((SchedulerRuntimeException)sre);
        }
    }

    private void scheduleLocalJob(JobId jobId, JobConfig jobConfig) throws SchedulerServiceException {
        Date nextRunTime = this.runTimeCalculator.firstRunTime(jobId, jobConfig);
        Map parameters = jobConfig.getParameters();
        SimpleJobDetails jobDetails = new SimpleJobDetails(jobId, jobConfig.getJobRunnerKey(), RunMode.RUN_LOCALLY, this.quantize(jobConfig.getSchedule()), nextRunTime, this.getParameterMapSerializer().serializeParameters(parameters), parameters);
        this.localJobs.put(jobId, (JobDetails)jobDetails);
        this.enqueueJob(jobId, nextRunTime);
        try {
            this.clusteredJobDao.delete(jobId);
        }
        catch (RuntimeException re) {
            LOG.warn("Unable to verify that there is no clustered job conflicting with local job '{}'", (Object)jobId, (Object)re);
        }
    }

    private void scheduleClusteredJob(JobId jobId, JobConfig jobConfig) throws SchedulerServiceException {
        Date nextRunTime = this.runTimeCalculator.firstRunTime(jobId, jobConfig);
        ImmutableClusteredJob clusteredJob = ImmutableClusteredJob.builder().jobId(jobId).jobRunnerKey(jobConfig.getJobRunnerKey()).schedule(this.quantize(jobConfig.getSchedule())).nextRunTime(nextRunTime).parameters(this.getParameterMapSerializer().serializeParameters(jobConfig.getParameters())).build();
        this.localJobs.remove(jobId);
        this.createOrReplaceWithRetry(clusteredJob);
        this.enqueueJob(jobId, nextRunTime);
    }

    private void createOrReplaceWithRetry(ClusteredJob clusteredJob) throws SchedulerServiceException {
        for (int attempt = 1; attempt <= 50; ++attempt) {
            this.clusteredJobDao.delete(clusteredJob.getJobId());
            if (!this.clusteredJobDao.create(clusteredJob)) continue;
            return;
        }
        throw new SchedulerServiceException("Unable to either create or replace clustered job: " + clusteredJob);
    }

    public void unscheduleJob(JobId jobId) {
        boolean found = this.localJobs.remove(jobId) != null;
        this.queue.remove(jobId);
        if (found |= this.clusteredJobDao.delete(jobId)) {
            LOG.debug("unscheduleJob: {}", (Object)jobId);
        } else {
            LOG.debug("unscheduleJob for non-existent jobId: {}", (Object)jobId);
        }
    }

    @Nullable
    public JobDetails getJobDetails(JobId jobId) {
        JobDetails localJob = (JobDetails)this.localJobs.get(jobId);
        if (localJob != null) {
            return localJob;
        }
        ClusteredJob clusteredJob = this.clusteredJobDao.find(jobId);
        return clusteredJob != null ? this.toJobDetails(clusteredJob) : null;
    }

    @Nonnull
    public Set<JobRunnerKey> getJobRunnerKeysForAllScheduledJobs() {
        ImmutableSet.Builder keys = ImmutableSet.builder();
        for (JobDetails localJob : this.localJobs.values()) {
            keys.add((Object)localJob.getJobRunnerKey());
        }
        keys.addAll(this.clusteredJobDao.findAllJobRunnerKeys());
        return keys.build();
    }

    @Nonnull
    public List<JobDetails> getJobsByJobRunnerKey(JobRunnerKey jobRunnerKey) {
        TreeMap<JobId, JobDetails> jobs = new TreeMap<JobId, JobDetails>();
        for (JobDetails jobDetails : this.localJobs.values()) {
            if (!jobDetails.getJobRunnerKey().equals((Object)jobRunnerKey)) continue;
            jobs.put(jobDetails.getJobId(), jobDetails);
        }
        Collection<ClusteredJob> clusteredJobs = this.clusteredJobDao.findByJobRunnerKey(jobRunnerKey);
        for (ClusteredJob clusteredJob : clusteredJobs) {
            jobs.put(clusteredJob.getJobId(), this.toJobDetails(clusteredJob));
        }
        return ImmutableList.copyOf(jobs.values());
    }

    @Nonnull
    public List<JobDetails> getJobsByJobRunnerKeys(List<JobRunnerKey> jobRunnerKeys) {
        TreeMap<JobId, JobDetails> jobs = new TreeMap<JobId, JobDetails>();
        for (JobDetails jobDetails : this.localJobs.values()) {
            if (!jobRunnerKeys.contains(jobDetails.getJobRunnerKey())) continue;
            jobs.put(jobDetails.getJobId(), jobDetails);
        }
        Collection<ClusteredJob> clusteredJobs = this.clusteredJobDao.findByJobRunnerKeys(jobRunnerKeys);
        for (ClusteredJob clusteredJob : clusteredJobs) {
            jobs.put(clusteredJob.getJobId(), this.toJobDetails(clusteredJob));
        }
        return ImmutableList.copyOf(jobs.values());
    }

    @Nullable
    public Date calculateNextRunTime(Schedule schedule) throws SchedulerServiceException {
        return this.runTimeCalculator.nextRunTime(schedule, null);
    }

    protected void startImpl() throws SchedulerServiceException {
        this.queue.resume();
        if (this.started.compareAndSet(false, true)) {
            this.startWorkers();
            this.refreshClusteredJobs();
            this.scheduleRefreshJob();
        }
    }

    private void startWorkers() {
        SchedulerQueueWorker worker = new SchedulerQueueWorker(this.queue, this::executeQueuedJob);
        int workerCount = this.config.workerThreadCount();
        if (workerCount <= 0) {
            workerCount = 4;
        }
        WorkerThreadFactory threadFactory = new WorkerThreadFactory();
        for (int i = 1; i <= workerCount; ++i) {
            threadFactory.newThread(worker).start();
        }
    }

    protected void standbyImpl() throws SchedulerServiceException {
        this.queue.pause();
    }

    protected void shutdownImpl() {
        this.queue.close();
        try {
            this.stats.close();
        }
        catch (IOException e) {
            LOG.error("Failed to close stats", (Throwable)e);
        }
    }

    public void refreshClusteredJob(JobId jobId) {
        Date nextRunTime;
        CaesiumSchedulerService.rejectInvalidJobId(jobId);
        try {
            nextRunTime = this.clusteredJobDao.getNextRunTime(jobId);
        }
        catch (RuntimeException re) {
            LOG.warn("Unable to refresh clustered job '{}'; scheduling a recovery job...", (Object)jobId, (Object)re);
            this.recoveryJob.schedule(re);
            return;
        }
        if (nextRunTime == null) {
            if (this.localJobs.containsKey(jobId)) {
                LOG.debug("Asked to refresh job '{}', but it is a local job so that was a bit silly.", (Object)jobId);
            } else {
                this.queue.remove(jobId);
            }
            return;
        }
        this.localJobs.remove(jobId);
        try {
            this.queue.add(new QueuedJob(jobId, nextRunTime.getTime()));
        }
        catch (SchedulerQueue.SchedulerShutdownException sse) {
            LOG.debug("Refresh failed for job '{}' due to scheduler shutdown", (Object)jobId, (Object)sse);
        }
    }

    public void refreshClusteredJobs() {
        int pendingJobCountBefore = this.queue.getPendingJobsCount();
        Map<JobId, Date> clusteredJobs = this.queue.refreshClusteredJobs();
        this.localJobs.keySet().removeAll(clusteredJobs.keySet());
        int pendingJobCountAfter = this.queue.getPendingJobsCount();
        this.stats.refreshClusteredJobs(pendingJobCountAfter - pendingJobCountBefore);
    }

    void scheduleRefreshJob() throws SchedulerServiceException {
        int refreshInterval = this.config.refreshClusteredJobsIntervalInMinutes();
        if (refreshInterval > 0) {
            this.registerJobRunner(REFRESH_JOB_RUNNER_KEY, this.refreshJob);
            long millis = TimeUnit.MINUTES.toMillis(refreshInterval);
            Schedule schedule = Schedule.forInterval((long)millis, (Date)new Date(this.now() + millis));
            this.scheduleLocalJob(REFRESH_JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)REFRESH_JOB_RUNNER_KEY).withRunMode(RunMode.RUN_LOCALLY).withSchedule(schedule));
        } else {
            this.unscheduleJob(REFRESH_JOB_ID);
            this.unregisterJobRunner(REFRESH_JOB_RUNNER_KEY);
        }
    }

    protected void executeQueuedJob(QueuedJob job) {
        this.stats.jobFlowTakenFromQueue();
        JobDetails jobDetails = (JobDetails)this.localJobs.get(job.getJobId());
        if (jobDetails != null) {
            this.executeLocalJobWithRetryOnFailure(jobDetails);
        } else {
            this.executeClusteredJobWithRecoveryGuard(job);
        }
    }

    void executeLocalJobWithRetryOnFailure(JobDetails jobDetails) {
        this.stats.jobFlowLocalBegin();
        Date firedAt = new Date(this.now());
        JobId jobId = jobDetails.getJobId();
        try {
            Date scheduledRunTime = jobDetails.getNextRunTime();
            if (scheduledRunTime == null || firedAt.getTime() < scheduledRunTime.getTime()) {
                LOG.debug("Launch for job '{}' either too early or after it's been deleted; scheduledRunTime={}", (Object)jobDetails, (Object)scheduledRunTime);
                this.enqueueJob(jobId, scheduledRunTime);
                this.stats.jobFlowLocalStartedTooEarly();
                return;
            }
            this.stats.jobFlowLocalPreEnqueue();
            this.enqueueJob(jobId, this.calculateNextRunTime(jobDetails, firedAt));
        }
        catch (Throwable throwableOnSchedulingNextRun) {
            this.stats.jobFlowLocalFailedSchedulingNextRun();
            try {
                LOG.error("Unhandled exception during the attempt to enqueue job '{}'; will attempt retry in {} seconds", new Object[]{jobId, 60, throwableOnSchedulingNextRun});
                Date recoveryJobRunTime = new Date(this.now() + TimeUnit.SECONDS.toMillis(60L));
                SimpleJobDetails refreshedJobDetails = new SimpleJobDetails(jobDetails.getJobId(), jobDetails.getJobRunnerKey(), jobDetails.getRunMode(), jobDetails.getSchedule(), recoveryJobRunTime, this.serialize(jobDetails.getParameters()), jobDetails.getParameters());
                this.stats.retryJobScheduled(throwableOnSchedulingNextRun);
                this.localJobs.put(jobId, (JobDetails)refreshedJobDetails);
                this.enqueueJob(jobId, recoveryJobRunTime);
            }
            catch (SchedulerServiceException exceptionOnSerialization) {
                this.stats.retryJobSerializationError(exceptionOnSerialization);
                LOG.error("Failed scheduling the retry for job '{}' due to failed serialization of job parameters. This job will not run again on this node until the node is restarted.", (Object)jobId, (Object)exceptionOnSerialization);
            }
            catch (Throwable throwableOnSchedulingRetry) {
                this.stats.retryJobScheduleError(throwableOnSchedulingRetry);
                LOG.error("Failed scheduling the retry for job '{}'. This job will not run again on this node until the node is restarted.", (Object)jobId, (Object)throwableOnSchedulingRetry);
            }
            return;
        }
        this.stats.jobFlowLocalPreLaunch();
        this.launchJob(RunMode.RUN_LOCALLY, firedAt, jobDetails);
        this.stats.jobFlowLocalPostLaunch();
    }

    private byte[] serialize(Map<String, Serializable> parameters) throws SchedulerServiceException {
        return this.getParameterMapSerializer().serializeParameters(parameters);
    }

    void executeClusteredJob(QueuedJob queuedJob) {
        this.stats.jobFlowClusteredBegin();
        Date firedAt = new Date(this.now());
        JobId jobId = queuedJob.getJobId();
        ClusteredJob clusteredJob = this.clusteredJobDao.find(jobId);
        if (clusteredJob == null) {
            LOG.debug("Failed to claim '{}' for run at {}; the job no longer exists.", (Object)jobId, (Object)firedAt);
            this.stats.jobFlowClusteredSkipNoLongerExists();
            return;
        }
        JobDetails jobDetails = this.toJobDetails(clusteredJob);
        Date scheduledRunTime = jobDetails.getNextRunTime();
        if (scheduledRunTime == null || queuedJob.getDeadline() < scheduledRunTime.getTime()) {
            this.enqueueJob(jobId, scheduledRunTime);
            this.stats.jobFlowClusteredSkipTooEarly();
            return;
        }
        Date nextRunTime = this.calculateNextRunTime(jobDetails, firedAt);
        if (!this.clusteredJobDao.updateNextRunTime(jobId, nextRunTime, clusteredJob.getVersion())) {
            LOG.debug("Failed to claim '{}' for run at {}; guess another node got there first?", (Object)jobId, (Object)nextRunTime);
            this.refreshClusteredJob(jobId);
            this.stats.jobFlowClusteredSkipFailedToClaim();
            return;
        }
        this.stats.jobFlowClusteredPreEnqueue();
        this.enqueueJob(jobId, nextRunTime);
        this.stats.jobFlowClusteredPreLaunch();
        this.launchJob(RunMode.RUN_ONCE_PER_CLUSTER, firedAt, jobDetails);
        this.stats.jobFlowClusteredPostLaunch();
    }

    private void launchJob(RunMode runMode, Date firedAt, JobDetails jobDetails) {
        JobLauncher launcher = new JobLauncher((AbstractSchedulerService)this, runMode, firedAt, jobDetails.getJobId(), jobDetails);
        launcher.launch();
    }

    @Nullable
    private Date calculateNextRunTime(JobDetails jobDetails, Date prevRunTime) {
        try {
            return this.runTimeCalculator.nextRunTime(jobDetails.getSchedule(), prevRunTime);
        }
        catch (CronSyntaxException cse) {
            LOG.error("Clustered job '{}' has invalid cron schedule '{}' and will never run.", (Object)jobDetails.getJobId(), (Object)jobDetails.getSchedule().getCronScheduleInfo().getCronExpression());
            return null;
        }
    }

    void executeClusteredJobWithRecoveryGuard(QueuedJob queuedJob) {
        try {
            this.executeClusteredJob(queuedJob);
        }
        catch (Throwable throwableOnClusterJobExecution) {
            LOG.error("Unhandled exception during the attempt to execute job '{}'; will attempt recovery in {} seconds", new Object[]{queuedJob.getJobId(), 60, throwableOnClusterJobExecution});
            this.recoveryJob.schedule(throwableOnClusterJobExecution);
        }
    }

    protected void enqueueJob(JobId jobId, @Nullable Date expectedTime) {
        try {
            if (expectedTime == null) {
                this.queue.remove(jobId);
                LOG.debug("Job '{}' has a null nextRunTime, which means we never expect it to run again", (Object)jobId);
                return;
            }
            this.queue.add(new QueuedJob(jobId, expectedTime.getTime()));
            LOG.debug("Enqueued job '{}' for {}", (Object)jobId, (Object)expectedTime);
        }
        catch (SchedulerQueue.SchedulerShutdownException sse) {
            LOG.debug("Could not enqueue job '{}' because we're in the middle of shutting down", (Object)jobId, (Object)sse);
        }
    }

    @Nonnull
    JobDetails toJobDetails(ClusteredJob clusteredJob) {
        return new LazyJobDetails((AbstractSchedulerService)this, clusteredJob.getJobId(), clusteredJob.getJobRunnerKey(), RunMode.RUN_ONCE_PER_CLUSTER, clusteredJob.getSchedule(), clusteredJob.getNextRunTime(), clusteredJob.getRawParameters());
    }

    private Schedule quantize(Schedule schedule) {
        if (this.config.useFineGrainedSchedules()) {
            return schedule;
        }
        switch (schedule.getType()) {
            case INTERVAL: {
                IntervalScheduleInfo info = schedule.getIntervalScheduleInfo();
                return Schedule.forInterval((long)TimeIntervalQuantizer.quantizeToMinutes((long)info.getIntervalInMillis()), (Date)info.getFirstRunTime());
            }
            case CRON_EXPRESSION: {
                CronScheduleInfo info = schedule.getCronScheduleInfo();
                return Schedule.forCronExpression((String)CronExpressionQuantizer.quantizeSecondsField((String)info.getCronExpression()), (TimeZone)info.getTimeZone());
            }
        }
        throw new IllegalStateException("Unsupported schedule type: " + schedule.getType());
    }

    @VisibleForTesting
    long now() {
        return System.currentTimeMillis();
    }

    @Internal
    public Map<JobId, Date> getPendingJobs() {
        ImmutableMap.Builder jobMap = ImmutableMap.builder();
        for (QueuedJob job : this.queue.getPendingJobs()) {
            jobMap.put((Object)job.getJobId(), (Object)new Date(job.getDeadline()));
        }
        return jobMap.build();
    }

    protected static ParameterMapSerializer createParameterMapSerializer(CaesiumSchedulerConfiguration config) {
        if (config.useQuartzJobDataMapMigration()) {
            return new LazyMigratingParameterMapSerializer();
        }
        return new ParameterMapSerializer();
    }

    private static void rejectInvalidJobId(@Nullable JobId jobId) {
        if (jobId == null) {
            throw new NullPointerException("jobId cannot be null");
        }
        if (jobId.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("jobId cannot be blank");
        }
    }

    final class JobRunnerWithStats
    implements JobRunner {
        private final JobRunner delegate;

        private JobRunnerWithStats(JobRunner delegate) {
            this.delegate = delegate;
        }

        @Nullable
        public JobRunnerResponse runJob(JobRunnerRequest request) {
            JobId jobId = request.getJobId();
            Stopwatch stopwatch = Stopwatch.createStarted();
            try {
                JobRunnerResponse jobRunnerResponse = this.delegate.runJob(request);
                long jobRunTimeMillis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
                CaesiumSchedulerService.this.stats.jobRunnerCompletedSuccessfully(jobId, jobRunTimeMillis);
                return jobRunnerResponse;
            }
            catch (Throwable t) {
                long jobRunTimeMillis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
                CaesiumSchedulerService.this.stats.jobRunnerFailed(jobId, jobRunTimeMillis, t);
                throw t;
            }
        }

        @VisibleForTesting
        public JobRunner getDelegate() {
            return this.delegate;
        }
    }

    class RecoveryJob
    extends RefreshJob {
        private final AtomicInteger consecutiveRun;

        RecoveryJob() {
            this.consecutiveRun = new AtomicInteger(0);
        }

        @Override
        @Nullable
        public JobRunnerResponse runJob(JobRunnerRequest request) {
            try {
                this.consecutiveRun.incrementAndGet();
                JobRunnerResponse response = super.runJob(request);
                LOG.warn("Recovery job completed successfully; resuming normal operation");
                CaesiumSchedulerService.this.localJobs.remove(RECOVERY_JOB_ID);
                CaesiumSchedulerService.this.unregisterJobRunner(RECOVERY_JOB_RUNNER_KEY);
                CaesiumSchedulerService.this.stats.recoveryJobCompletedSuccessfully(this.consecutiveRun.getAndSet(0));
                return response;
            }
            catch (Throwable t) {
                LOG.warn("Recovery job did not complete normally; rescheduling...", t);
                this.schedule(t);
                throw t;
            }
        }

        private void schedule(Throwable reason) {
            try {
                CaesiumSchedulerService.this.registerJobRunner(RECOVERY_JOB_RUNNER_KEY, CaesiumSchedulerService.this.recoveryJob);
                long millis = TimeUnit.SECONDS.toMillis(60L);
                Date nextRunTime = new Date(CaesiumSchedulerService.this.now() + millis);
                Schedule schedule = Schedule.runOnce((Date)nextRunTime);
                SimpleJobDetails jobDetails = new SimpleJobDetails(RECOVERY_JOB_ID, RECOVERY_JOB_RUNNER_KEY, RunMode.RUN_LOCALLY, schedule, nextRunTime, null, null);
                CaesiumSchedulerService.this.localJobs.put(RECOVERY_JOB_ID, jobDetails);
                CaesiumSchedulerService.this.enqueueJob(RECOVERY_JOB_ID, nextRunTime);
                CaesiumSchedulerService.this.stats.recoveryJobScheduledSuccessfully(reason);
            }
            catch (Throwable throwableOnSchedulingRecoveryJob) {
                LOG.error("Failed scheduling a recovery job for a failed cluster job.", throwableOnSchedulingRecoveryJob);
                CaesiumSchedulerService.this.stats.recoveryJobSchedulingFailed(throwableOnSchedulingRecoveryJob);
            }
        }
    }

    class RefreshJob
    implements JobRunner {
        RefreshJob() {
        }

        @Nullable
        public JobRunnerResponse runJob(JobRunnerRequest request) {
            CaesiumSchedulerService.this.refreshClusteredJobs();
            return JobRunnerResponse.success();
        }
    }
}

