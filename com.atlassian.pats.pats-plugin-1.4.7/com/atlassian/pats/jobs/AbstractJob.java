/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.pats.jobs;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractJob
implements JobRunner,
InitializingBean,
DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(AbstractJob.class);
    protected final SchedulerService schedulerService;

    protected AbstractJob(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void afterPropertiesSet() throws Exception {
        this.schedulerService.registerJobRunner(this.getJobRunnerKey(), (JobRunner)this);
        this.schedulerService.scheduleJob(this.getJobId(), JobConfig.forJobRunnerKey((JobRunnerKey)this.getJobRunnerKey()).withSchedule(this.getSchedule()).withRunMode(this.getRunMode()));
        logger.info("Registered job with key: [{}] and with schedule: [{}]", (Object)this.getJobRunnerKey(), this.getSchedule().getCronScheduleInfo() != null ? this.getSchedule().getCronScheduleInfo() : this.getSchedule().getIntervalScheduleInfo());
    }

    protected abstract Schedule getSchedule();

    protected abstract RunMode getRunMode();

    public void destroy() {
        this.schedulerService.unregisterJobRunner(this.getJobRunnerKey());
        logger.debug("Unregistered job with key: [{}] and id [{}]", (Object)this.getJobRunnerKey(), (Object)this.getJobId());
    }

    private JobId getJobId() {
        return JobId.of((String)this.getClass().getSimpleName());
    }

    private JobRunnerKey getJobRunnerKey() {
        return JobRunnerKey.of((String)this.getClass().getSimpleName());
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest ignored) {
        this.doJob();
        return JobRunnerResponse.success();
    }

    protected abstract void doJob();
}

