/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.scheduling.PluginJob
 *  com.atlassian.sal.api.scheduling.PluginScheduler
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerRuntimeException
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.sal.core.scheduling;

import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerRuntimeException;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class DefaultPluginScheduler
implements PluginScheduler,
InitializingBean,
DisposableBean {
    static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)DefaultPluginScheduler.class.getName());
    private final ConcurrentMap<JobId, JobDescriptor> descriptors = new ConcurrentHashMap<JobId, JobDescriptor>();
    private final SchedulerService schedulerService;

    public DefaultPluginScheduler(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void scheduleJob(String jobKey, Class<? extends PluginJob> jobClass, Map<String, Object> jobDataMap, Date startTime, long repeatInterval) {
        JobId jobId = DefaultPluginScheduler.toJobId(jobKey);
        this.descriptors.put(jobId, new JobDescriptor(jobClass, jobDataMap));
        JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withRunMode(RunMode.RUN_LOCALLY).withSchedule(Schedule.forInterval((long)repeatInterval, (Date)startTime));
        try {
            this.schedulerService.scheduleJob(jobId, jobConfig);
        }
        catch (SchedulerServiceException sse) {
            throw new SchedulerRuntimeException(sse.getMessage(), (Throwable)sse);
        }
    }

    public void unscheduleJob(String jobKey) {
        JobId jobId = DefaultPluginScheduler.toJobId(jobKey);
        this.schedulerService.unscheduleJob(jobId);
        if (this.descriptors.remove(jobId) == null) {
            throw new IllegalArgumentException("Error unscheduling job. Job '" + jobKey + "' is not scheduled.");
        }
    }

    @Nonnull
    JobRunnerResponse runJobImpl(JobRunnerRequest jobRunnerRequest) {
        JobDescriptor descriptor = (JobDescriptor)this.descriptors.get(jobRunnerRequest.getJobId());
        if (descriptor == null) {
            return JobRunnerResponse.aborted((String)"Job descriptor not found");
        }
        return descriptor.runJob();
    }

    public void afterPropertiesSet() {
        this.schedulerService.registerJobRunner(JOB_RUNNER_KEY, new JobRunner(){

            @Nullable
            public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
                return DefaultPluginScheduler.this.runJobImpl(jobRunnerRequest);
            }
        });
    }

    public void destroy() {
        this.schedulerService.unregisterJobRunner(JOB_RUNNER_KEY);
    }

    static JobId toJobId(String jobKey) {
        return JobId.of((String)(DefaultPluginScheduler.class.getSimpleName() + ':' + jobKey));
    }

    static class JobDescriptor {
        final Class<? extends PluginJob> jobClass;
        final Map<String, Object> jobDataMap;

        JobDescriptor(Class<? extends PluginJob> jobClass, Map<String, Object> jobDataMap) {
            this.jobClass = Objects.requireNonNull(jobClass, "jobClass");
            this.jobDataMap = jobDataMap;
        }

        @Nonnull
        JobRunnerResponse runJob() {
            PluginJob job;
            try {
                job = this.jobClass.newInstance();
            }
            catch (IllegalAccessException | InstantiationException e) {
                return JobRunnerResponse.aborted((String)e.toString());
            }
            job.execute(this.jobDataMap);
            return JobRunnerResponse.success();
        }
    }
}

