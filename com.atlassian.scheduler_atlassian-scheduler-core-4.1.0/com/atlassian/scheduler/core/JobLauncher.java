/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerRuntimeException
 *  com.atlassian.scheduler.config.IntervalScheduleInfo
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.status.JobDetails
 *  com.atlassian.scheduler.status.RunOutcome
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.scheduler.core;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerRuntimeException;
import com.atlassian.scheduler.config.IntervalScheduleInfo;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.core.AbstractSchedulerService;
import com.atlassian.scheduler.core.JobRunnerNotRegisteredException;
import com.atlassian.scheduler.core.RunningJob;
import com.atlassian.scheduler.core.impl.RunningJobImpl;
import com.atlassian.scheduler.status.JobDetails;
import com.atlassian.scheduler.status.RunOutcome;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobLauncher {
    protected static final Logger LOG = LoggerFactory.getLogger(JobLauncher.class);
    protected final AbstractSchedulerService schedulerService;
    protected final RunMode schedulerRunMode;
    protected final Date firedAt;
    protected final JobId jobId;
    private JobDetails jobDetails;
    private JobRunner jobRunner;
    private JobConfig jobConfig;
    private JobRunnerResponse response;

    public JobLauncher(AbstractSchedulerService schedulerService, RunMode schedulerRunMode, @Nullable Date firedAt, JobId jobId) {
        this(schedulerService, schedulerRunMode, firedAt, jobId, null);
    }

    public JobLauncher(AbstractSchedulerService schedulerService, RunMode schedulerRunMode, @Nullable Date firedAt, JobId jobId, @Nullable JobDetails jobDetails) {
        this.schedulerService = Objects.requireNonNull(schedulerService, "schedulerService");
        this.schedulerRunMode = Objects.requireNonNull(schedulerRunMode, "schedulerRunMode");
        this.firedAt = firedAt != null ? firedAt : new Date();
        this.jobId = Objects.requireNonNull(jobId, "jobId");
        this.jobDetails = jobDetails;
    }

    public void launch() {
        LOG.debug("launch: {}: {}", (Object)this.schedulerRunMode, (Object)this.jobId);
        try {
            JobRunnerResponse response = this.launchAndBuildResponse();
            this.schedulerService.addRunDetails(this.jobId, this.firedAt, response.getRunOutcome(), response.getMessage());
        }
        catch (JobRunnerNotRegisteredException ex) {
            LOG.debug("Scheduled job with ID '{}' is unavailable because its job runner is not registered: {}", (Object)this.jobId, (Object)ex.getJobRunnerKey());
            this.schedulerService.addRunDetails(this.jobId, this.firedAt, RunOutcome.UNAVAILABLE, "Job runner key '" + ex.getJobRunnerKey() + "' is not registered");
        }
        this.deleteIfRunOnce();
    }

    @Nonnull
    private JobRunnerResponse launchAndBuildResponse() throws JobRunnerNotRegisteredException {
        try {
            this.response = this.validate();
            if (this.response == null) {
                this.response = this.runJob();
            }
        }
        catch (RuntimeException ex) {
            LOG.error("Scheduled job with ID '{}' failed", (Object)this.jobId, (Object)ex);
            this.response = JobRunnerResponse.failed((Throwable)ex);
        }
        catch (LinkageError err) {
            LOG.error("Scheduled job with ID '{}' failed due to binary incompatibilities", (Object)this.jobId, (Object)err);
            this.response = JobRunnerResponse.failed((Throwable)err);
        }
        return this.response;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    private JobRunnerResponse runJob() {
        RunningJobImpl job = new RunningJobImpl(this.firedAt, this.jobId, this.jobConfig);
        RunningJob existing = this.schedulerService.enterJob(this.jobId, job);
        if (existing != null) {
            LOG.debug("Unable to start job {} because it is already running as {}", (Object)job, (Object)existing);
            return JobRunnerResponse.aborted((String)"Already running");
        }
        this.schedulerService.preJob();
        Thread thd = Thread.currentThread();
        ClassLoader originalClassLoader = thd.getContextClassLoader();
        try {
            thd.setContextClassLoader(this.jobRunner.getClass().getClassLoader());
            JobRunnerResponse response = this.jobRunner.runJob((JobRunnerRequest)job);
            JobRunnerResponse jobRunnerResponse = response != null ? response : JobRunnerResponse.success();
            return jobRunnerResponse;
        }
        finally {
            thd.setContextClassLoader(originalClassLoader);
            this.schedulerService.leaveJob(this.jobId, job);
            this.schedulerService.postJob();
        }
    }

    @Nullable
    private JobRunnerResponse validate() throws JobRunnerNotRegisteredException {
        JobRunnerResponse response = this.validateJobDetails();
        if (response == null && (response = this.validateJobRunner()) == null) {
            response = this.validateJobConfig();
        }
        return response;
    }

    @Nullable
    private JobRunnerResponse validateJobDetails() {
        if (this.jobDetails == null) {
            this.jobDetails = this.schedulerService.getJobDetails(this.jobId);
            if (this.jobDetails == null) {
                return JobRunnerResponse.aborted((String)"No corresponding job details");
            }
        }
        if (this.jobDetails.getRunMode() != this.schedulerRunMode) {
            return JobRunnerResponse.aborted((String)("Inconsistent run mode: expected '" + this.jobDetails.getRunMode() + "' got: '" + this.schedulerRunMode + '\''));
        }
        return null;
    }

    @Nullable
    private JobRunnerResponse validateJobRunner() throws JobRunnerNotRegisteredException {
        this.jobRunner = this.schedulerService.getJobRunner(this.jobDetails.getJobRunnerKey());
        if (this.jobRunner == null) {
            throw new JobRunnerNotRegisteredException(this.jobDetails.getJobRunnerKey());
        }
        return null;
    }

    @Nullable
    private JobRunnerResponse validateJobConfig() {
        try {
            this.jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)this.jobDetails.getJobRunnerKey()).withRunMode(this.jobDetails.getRunMode()).withSchedule(this.jobDetails.getSchedule()).withParameters(this.jobDetails.getParameters());
            return null;
        }
        catch (SchedulerRuntimeException sre) {
            return JobRunnerResponse.aborted((String)this.jobDetails.toString());
        }
    }

    private void deleteIfRunOnce() {
        IntervalScheduleInfo info;
        if (this.jobDetails != null && (info = this.jobDetails.getSchedule().getIntervalScheduleInfo()) != null && info.getIntervalInMillis() == 0L) {
            LOG.debug("deleteIfRunOnce: deleting completed job: {}", (Object)this.jobId);
            this.schedulerService.unscheduleJob(this.jobId);
        }
    }

    public String toString() {
        return "JobLauncher[jobId=" + this.jobId + ",jobDetails=" + this.jobDetails + ",response=" + this.response + ']';
    }
}

