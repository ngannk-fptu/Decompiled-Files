/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.scheduling.PluginJob
 *  com.atlassian.sal.api.scheduling.PluginScheduler
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.IntervalScheduleInfo
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.config.Schedule$Type
 *  com.atlassian.scheduler.status.JobDetails
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.scheduler;

import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.IntervalScheduleInfo;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.status.JobDetails;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class FallbackSchedulerService
implements SchedulerService {
    private static final Function<JobConfig, JobRunnerKey> TO_RUNNER_KEY = JobConfig::getJobRunnerKey;
    private static final String KEY_JOB_CONFIG = "job-config";
    private static final String KEY_JOB_ID = "job-id";
    private static final String KEY_SCHEDULER_SERVICE = "scheduler-service";
    private final Map<JobRunnerKey, JobRunner> jobRunners;
    private final Map<JobId, JobConfig> scheduledJobs;
    private final PluginScheduler scheduler;

    public FallbackSchedulerService(PluginScheduler scheduler) {
        this.scheduler = scheduler;
        this.jobRunners = Maps.newConcurrentMap();
        this.scheduledJobs = Maps.newConcurrentMap();
    }

    public void registerJobRunner(@Nonnull JobRunnerKey jobRunnerKey, @Nonnull JobRunner jobRunner) {
        this.jobRunners.put(jobRunnerKey, jobRunner);
    }

    public void unregisterJobRunner(@Nonnull JobRunnerKey jobRunnerKey) {
        this.jobRunners.remove(jobRunnerKey);
    }

    @Nonnull
    public Set<JobRunnerKey> getRegisteredJobRunnerKeys() {
        return ImmutableSet.copyOf(this.jobRunners.keySet());
    }

    @Nonnull
    public Set<JobRunnerKey> getJobRunnerKeysForAllScheduledJobs() {
        return ImmutableSet.copyOf((Collection)this.scheduledJobs.values().stream().map(TO_RUNNER_KEY).collect(Collectors.toList()));
    }

    public void scheduleJob(@Nonnull JobId jobId, @Nonnull JobConfig jobConfig) throws SchedulerServiceException {
        if (jobConfig.getSchedule().getType() == Schedule.Type.CRON_EXPRESSION) {
            throw new IllegalArgumentException("The fallback scheduler does not support CRON expressions");
        }
        this.unscheduleJob(jobId);
        this.scheduledJobs.put(jobId, jobConfig);
        IntervalScheduleInfo interval = jobConfig.getSchedule().getIntervalScheduleInfo();
        ImmutableMap contextMap = ImmutableMap.of((Object)KEY_JOB_CONFIG, (Object)jobConfig, (Object)KEY_JOB_ID, (Object)jobId, (Object)KEY_SCHEDULER_SERVICE, (Object)this);
        this.scheduler.scheduleJob(jobId.toString(), JobRunnerAdapter.class, (Map)contextMap, interval.getFirstRunTime(), interval.getIntervalInMillis());
    }

    @Nonnull
    public JobId scheduleJobWithGeneratedId(@Nonnull JobConfig jobConfig) throws SchedulerServiceException {
        JobId jobId = JobId.of((String)UUID.randomUUID().toString());
        this.scheduleJob(jobId, jobConfig);
        return jobId;
    }

    public void unscheduleJob(@Nonnull JobId jobId) {
        this.scheduledJobs.remove(jobId);
        try {
            this.scheduler.unscheduleJob(jobId.toString());
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
    }

    public JobDetails getJobDetails(@Nonnull JobId jobId) {
        JobConfig config = this.scheduledJobs.get(jobId);
        return config == null ? null : new SimpleJobDetails(jobId, config);
    }

    @Nonnull
    public List<JobDetails> getJobsByJobRunnerKey(@Nonnull JobRunnerKey jobRunnerKey) {
        ArrayList<JobDetails> result = new ArrayList<JobDetails>();
        for (Map.Entry<JobId, JobConfig> entry : this.scheduledJobs.entrySet()) {
            if (!jobRunnerKey.equals((Object)entry.getValue().getJobRunnerKey())) continue;
            result.add(new SimpleJobDetails(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    JobRunner getJobRunner(JobRunnerKey jobRunnerKey) {
        return this.jobRunners.get(jobRunnerKey);
    }

    private static class SimpleRunnerJobRequest
    implements JobRunnerRequest {
        private final JobConfig jobConfig;
        private final JobId jobId;
        private final Date startTime;

        private SimpleRunnerJobRequest(JobId jobId, JobConfig jobConfig) {
            this.jobConfig = jobConfig;
            this.jobId = jobId;
            this.startTime = new Date();
        }

        @Nonnull
        public Date getStartTime() {
            return new Date(this.startTime.getTime());
        }

        @Nonnull
        public JobId getJobId() {
            return this.jobId;
        }

        @Nonnull
        public JobConfig getJobConfig() {
            return this.jobConfig;
        }
    }

    private static class SimpleJobDetails
    implements JobDetails {
        private final JobConfig config;
        private final JobId jobId;

        private SimpleJobDetails(JobId jobId, JobConfig config) {
            this.config = config;
            this.jobId = jobId;
        }

        @Nonnull
        public JobId getJobId() {
            return this.jobId;
        }

        @Nonnull
        public JobRunnerKey getJobRunnerKey() {
            return this.config.getJobRunnerKey();
        }

        @Nonnull
        public RunMode getRunMode() {
            return this.config.getRunMode();
        }

        @Nonnull
        public Schedule getSchedule() {
            return this.config.getSchedule();
        }

        public Date getNextRunTime() {
            return null;
        }

        @Nonnull
        public Map<String, Serializable> getParameters() {
            return this.config.getParameters();
        }

        public boolean isRunnable() {
            return true;
        }
    }

    public static class JobRunnerAdapter
    implements PluginJob {
        public void execute(Map<String, Object> jobDataMap) {
            JobId jobId = (JobId)jobDataMap.get(FallbackSchedulerService.KEY_JOB_ID);
            JobConfig jobConfig = (JobConfig)jobDataMap.get(FallbackSchedulerService.KEY_JOB_CONFIG);
            FallbackSchedulerService scheduler = (FallbackSchedulerService)jobDataMap.get(FallbackSchedulerService.KEY_SCHEDULER_SERVICE);
            JobRunner runner = scheduler.getJobRunner(jobConfig.getJobRunnerKey());
            if (runner != null) {
                runner.runJob((JobRunnerRequest)new SimpleRunnerJobRequest(jobId, jobConfig));
            }
        }
    }
}

