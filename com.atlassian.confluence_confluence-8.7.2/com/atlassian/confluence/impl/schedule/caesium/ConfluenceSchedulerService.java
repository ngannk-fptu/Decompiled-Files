/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.SchedulerRuntimeException
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.caesium.impl.CaesiumSchedulerService
 *  com.atlassian.scheduler.caesium.spi.CaesiumSchedulerConfiguration
 *  com.atlassian.scheduler.caesium.spi.ClusteredJobDao
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.core.JobRunnerNotRegisteredException
 *  com.atlassian.scheduler.core.spi.RunDetailsDao
 *  com.atlassian.scheduler.core.util.ParameterMapSerializer
 *  com.atlassian.scheduler.status.JobDetails
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.schedule.caesium;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.impl.schedule.caesium.JobRunnerWrapper;
import com.atlassian.confluence.impl.schedule.caesium.SecureParameterMapSerializer;
import com.atlassian.confluence.impl.schedule.caesium.ThreadLocalSchedulerControl;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.confluence.schedule.listeners.JobListener;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerRuntimeException;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.caesium.impl.CaesiumSchedulerService;
import com.atlassian.scheduler.caesium.spi.CaesiumSchedulerConfiguration;
import com.atlassian.scheduler.caesium.spi.ClusteredJobDao;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.core.JobRunnerNotRegisteredException;
import com.atlassian.scheduler.core.spi.RunDetailsDao;
import com.atlassian.scheduler.core.util.ParameterMapSerializer;
import com.atlassian.scheduler.status.JobDetails;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class ConfluenceSchedulerService
extends CaesiumSchedulerService {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceSchedulerService.class);
    private final Supplier<JobListener> listener;
    private final ClusterLockService lockService;
    private final ClusterManager clusterManager;

    public ConfluenceSchedulerService(CaesiumSchedulerConfiguration config, RunDetailsDao runDetailsDao, ClusteredJobDao clusteredJobDao, Set<String> parameterClassWhiteList, ClusterLockService lockService, ClusterManager clusterManager, @Nullable Supplier<JobListener> listener) {
        super(config, runDetailsDao, clusteredJobDao, (ParameterMapSerializer)new SecureParameterMapSerializer(parameterClassWhiteList));
        this.lockService = Objects.requireNonNull(lockService);
        this.listener = listener;
        this.clusterManager = Objects.requireNonNull(clusterManager);
    }

    public ConfluenceSchedulerService(CaesiumSchedulerConfiguration config, RunDetailsDao runDetailsDao, ClusteredJobDao clusteredJobDao, Set<String> parameterClassWhiteList, ClusterLockService lockService, ClusterManager clusterManager) {
        this(config, runDetailsDao, clusteredJobDao, parameterClassWhiteList, lockService, clusterManager, null);
    }

    public void registerJobRunner(JobRunnerKey jobRunnerKey, JobRunner jobRunner) {
        JobRunnerWrapper wrapper = new JobRunnerWrapper(jobRunner, this.lockService, this.listener);
        super.registerJobRunner(jobRunnerKey, (JobRunner)wrapper);
    }

    protected void enqueueJob(JobId jobId, @Nullable Date nextRunTime) {
        Map parameters;
        if (nextRunTime == null) {
            super.enqueueJob(jobId, null);
            return;
        }
        JobDetails jobDetails = this.getJobDetails(jobId);
        if (jobDetails == null) {
            return;
        }
        try {
            parameters = jobDetails.getParameters();
        }
        catch (SchedulerRuntimeException e) {
            if (e.getCause() instanceof JobRunnerNotRegisteredException) {
                log.info("Not recognised job runner entry in the database, ignoring this run. {}", (Object)e.getMessage());
                return;
            }
            throw e;
        }
        if (!this.handleRepeatCount(jobId, nextRunTime, parameters) && !this.handleJitter(jobId, nextRunTime, parameters)) {
            super.enqueueJob(jobId, nextRunTime);
        }
    }

    private boolean handleRepeatCount(JobId jobId, Date nextRunTime, Map<String, Serializable> parameters) {
        long ms = ScheduleUtil.getUnscheduleJobAfterTimestampMillis(parameters);
        if (System.currentTimeMillis() <= ms) {
            if (nextRunTime.getTime() > ms) {
                super.enqueueJob(jobId, null);
                return true;
            }
            return false;
        }
        this.unscheduleJob(jobId);
        return true;
    }

    private boolean handleJitter(JobId jobId, Date nextRunTime, Map<String, Serializable> parameters) {
        int jitterSecs = ScheduleUtil.getJitterSecs(parameters);
        if (jitterSecs <= 0) {
            return false;
        }
        long jitterMillis = (long)(Math.random() * (double)jitterSecs * 1000.0);
        Date jitterDate = new Date(nextRunTime.getTime() + jitterMillis);
        super.enqueueJob(jobId, jitterDate);
        return true;
    }

    public void scheduleJob(JobId jobId, JobConfig jobConfig) throws SchedulerServiceException {
        if (ThreadLocalSchedulerControl.getInstance().schedulerDisabled()) {
            log.warn("Scheduler is disabled, {} won't be run", (Object)jobConfig.getJobRunnerKey());
            return;
        }
        if (!this.clusterManager.isClustered() && RunMode.RUN_ONCE_PER_CLUSTER.equals((Object)jobConfig.getRunMode())) {
            super.scheduleJob(jobId, jobConfig.withRunMode(RunMode.RUN_LOCALLY));
        } else {
            super.scheduleJob(jobId, jobConfig);
        }
    }

    public @NonNull JobId scheduleJobWithGeneratedId(JobConfig jobConfig) throws SchedulerServiceException {
        if (ThreadLocalSchedulerControl.getInstance().schedulerDisabled()) {
            log.warn("Scheduler is disabled, {} won't be run", (Object)jobConfig.getJobRunnerKey());
            return JobId.of((String)UUID.randomUUID().toString());
        }
        if (!this.clusterManager.isClustered() && RunMode.RUN_ONCE_PER_CLUSTER.equals((Object)jobConfig.getRunMode())) {
            return super.scheduleJobWithGeneratedId(jobConfig.withRunMode(RunMode.RUN_LOCALLY));
        }
        return super.scheduleJobWithGeneratedId(jobConfig);
    }
}

