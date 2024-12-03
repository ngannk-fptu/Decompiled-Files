/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.status.JobDetails
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.lang3.time.DateUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionCallbackWithoutResult
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.schedule.listeners;

import com.atlassian.confluence.schedule.ExecutionStatus;
import com.atlassian.confluence.schedule.ManagedScheduledJob;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.confluence.schedule.ScheduledJobConfiguration;
import com.atlassian.confluence.schedule.ScheduledJobHistory;
import com.atlassian.confluence.schedule.ScheduledJobStatus;
import com.atlassian.confluence.schedule.listeners.JobListener;
import com.atlassian.confluence.schedule.managers.DefaultScheduledJobManager;
import com.atlassian.confluence.schedule.managers.ManagedScheduledJobRegistry;
import com.atlassian.confluence.schedule.managers.ScheduledJobStatusManager;
import com.atlassian.confluence.schedule.persistence.dao.ScheduledJobDao;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.status.JobDetails;
import com.google.common.annotations.VisibleForTesting;
import java.util.Date;
import java.util.function.Supplier;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class ScheduledJobsListener
implements JobListener {
    private static final Logger log = LoggerFactory.getLogger(ScheduledJobsListener.class);
    private final SchedulerService schedulerService;
    private final ScheduledJobDao scheduledJobDAO;
    private final Supplier<ManagedScheduledJobRegistry> jobRegistry;
    private final ScheduledJobStatusManager statusManager;
    private final PlatformTransactionManager transactionManager;
    private final TimeZoneManager timeZoneManager;

    public ScheduledJobsListener(SchedulerService schedulerService, ScheduledJobDao scheduledJobDAO, Supplier<ManagedScheduledJobRegistry> jobRegistry, ScheduledJobStatusManager statusManager, PlatformTransactionManager transactionManager, TimeZoneManager timeZoneManager) {
        this.schedulerService = schedulerService;
        this.scheduledJobDAO = scheduledJobDAO;
        this.jobRegistry = jobRegistry;
        this.statusManager = statusManager;
        this.transactionManager = transactionManager;
        this.timeZoneManager = timeZoneManager;
    }

    @Override
    public void jobToBeExecuted(JobRunnerRequest request) {
        this.executeInTransaction(() -> {
            JobId jobId = DefaultScheduledJobManager.jobIdToScheduledJobKeyWithoutRunNowSuffix(request.getJobId());
            this.jobToBeExecuted(jobId, request);
        });
    }

    @Override
    public void jobWasExecuted(JobRunnerRequest request, JobRunnerResponse response) {
        this.executeInTransaction(() -> {
            Date nextFireTime;
            JobId jobId = request.getJobId();
            JobId originalJobId = DefaultScheduledJobManager.jobIdToScheduledJobKeyWithoutRunNowSuffix(jobId);
            try {
                JobDetails jobDetails;
                nextFireTime = jobId.equals((Object)originalJobId) ? ScheduleUtil.calculateNextRunTime(this.schedulerService, request.getJobConfig(), request.getStartTime()) : ((jobDetails = this.schedulerService.getJobDetails(originalJobId)) == null || !jobDetails.isRunnable() ? null : this.calculateNextRunTime(this.jobRegistry.get().getManagedScheduledJob(originalJobId), request));
            }
            catch (SchedulerServiceException e) {
                nextFireTime = null;
            }
            this.jobWasExecuted(originalJobId, request.getStartTime(), nextFireTime);
        });
    }

    @VisibleForTesting
    void executeInTransaction(final Runnable runnable) {
        TransactionTemplate tt = new TransactionTemplate();
        tt.setTransactionManager(this.transactionManager);
        tt.execute((TransactionCallback)new TransactionCallbackWithoutResult(){

            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                runnable.run();
            }
        });
    }

    @VisibleForTesting
    Date calculateNextRunTime(ManagedScheduledJob job, JobRunnerRequest request) {
        ScheduledJobConfiguration scheduledJobConfiguration = this.scheduledJobDAO.getScheduledJobConfiguration(job.getJobId());
        if (scheduledJobConfiguration != null && scheduledJobConfiguration.isEnabled()) {
            try {
                JobConfig jobConfig = ScheduleUtil.getJobConfig(scheduledJobConfiguration, job, this.timeZoneManager.getDefaultTimeZone());
                return ScheduleUtil.calculateNextRunTime(this.schedulerService, jobConfig, request.getStartTime());
            }
            catch (SchedulerServiceException e) {
                log.error("Error calculating next run time for " + job.getJobId(), (Throwable)e);
            }
        }
        return null;
    }

    private void jobToBeExecuted(JobId jobId, JobRunnerRequest request) {
        ScheduledJobStatus jobStatus;
        ManagedScheduledJob job = this.jobRegistry.get().getManagedScheduledJob(jobId);
        if (job != null && (jobStatus = this.statusManager.getScheduledJobStatus(job)) != null) {
            if (ExecutionStatus.DISABLED.equals((Object)jobStatus.getStatus())) {
                this.scheduledJobDAO.updateStatus(jobId, ExecutionStatus.DISABLED_MANUALLY_RUNNING);
            } else {
                this.scheduledJobDAO.updateStatus(jobId, ExecutionStatus.RUNNING);
            }
        }
    }

    private void jobWasExecuted(JobId jobId, Date startTime, Date nextFireTime) {
        long jobRunTime = System.currentTimeMillis() - startTime.getTime();
        ManagedScheduledJob job = this.jobRegistry.get().getManagedScheduledJob(jobId);
        if (job != null) {
            ScheduledJobStatus jobStatus = this.statusManager.getScheduledJobStatus(job);
            if (jobStatus != null) {
                if (ExecutionStatus.DISABLED_MANUALLY_RUNNING.equals((Object)jobStatus.getStatus())) {
                    this.scheduledJobDAO.updateStatus(jobId, ExecutionStatus.DISABLED);
                } else {
                    this.scheduledJobDAO.updateStatus(jobId, ExecutionStatus.SCHEDULED);
                }
            }
            Date endTime = DateUtils.addMilliseconds((Date)startTime, (int)((int)jobRunTime));
            ScheduledJobHistory history = new ScheduledJobHistory(startTime, endTime);
            this.scheduledJobDAO.addHistory(jobId, history, nextFireTime);
        }
    }
}

