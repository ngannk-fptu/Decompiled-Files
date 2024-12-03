/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.status.JobDetails
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.schedule.managers;

import com.atlassian.confluence.impl.schedule.caesium.SchedulerRunDetails;
import com.atlassian.confluence.internal.schedule.persistence.dao.InternalRunDetailsDao;
import com.atlassian.confluence.schedule.ExecutionStatus;
import com.atlassian.confluence.schedule.ManagedScheduledJob;
import com.atlassian.confluence.schedule.ScheduledJobConfiguration;
import com.atlassian.confluence.schedule.ScheduledJobHistory;
import com.atlassian.confluence.schedule.ScheduledJobStatus;
import com.atlassian.confluence.schedule.managers.ScheduledJobStatusManager;
import com.atlassian.confluence.schedule.persistence.dao.ScheduledJobDao;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.status.JobDetails;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultScheduledJobStatusManager
implements ScheduledJobStatusManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultScheduledJobStatusManager.class);
    private final ScheduledJobDao scheduledJobDAO;
    private final SchedulerService schedulerService;
    private final InternalRunDetailsDao internalRunDetailsDao;

    public DefaultScheduledJobStatusManager(ScheduledJobDao scheduledJobDAO, SchedulerService schedulerService, InternalRunDetailsDao internalRunDetailsDao) {
        this.scheduledJobDAO = scheduledJobDAO;
        this.schedulerService = schedulerService;
        this.internalRunDetailsDao = internalRunDetailsDao;
    }

    @Override
    public ScheduledJobStatus getScheduledJobStatus(ManagedScheduledJob job) {
        ScheduledJobConfiguration configuration;
        if (job == null) {
            return null;
        }
        JobId jobId = job.getJobId();
        ScheduledJobStatus status = this.scheduledJobDAO.getScheduledJobStatus(jobId);
        if (status == null && (configuration = this.scheduledJobDAO.getScheduledJobConfiguration(jobId)) != null) {
            List<SchedulerRunDetails> runDetailsList = this.internalRunDetailsDao.getRecentRunDetails(jobId);
            List<ScheduledJobHistory> existingHistory = runDetailsList.stream().map(runDetails -> {
                Date endDate = new Date(runDetails.getStartTime().getTime() + runDetails.getDuration());
                return new ScheduledJobHistory(runDetails.getStartTime(), endDate);
            }).collect(Collectors.toList());
            status = new ScheduledJobStatus(job.getJobId(), existingHistory);
            status.setStatus(this.getStatus(configuration));
            status.setNextExecution(this.getNextExecutionDate(job));
            this.scheduledJobDAO.saveScheduledJobStatus(jobId, status);
        }
        return status;
    }

    private ExecutionStatus getStatus(ScheduledJobConfiguration configuration) {
        return configuration.isEnabled() ? ExecutionStatus.SCHEDULED : ExecutionStatus.DISABLED;
    }

    private Date getNextExecutionDate(ManagedScheduledJob job) {
        JobDetails jobDetails = this.schedulerService.getJobDetails(job.getJobId());
        if (jobDetails == null || !jobDetails.isRunnable()) {
            try {
                return this.schedulerService.calculateNextRunTime(job.getJobConfig().getSchedule());
            }
            catch (SchedulerServiceException e) {
                log.warn("Unable to determine next execution time for job " + job.getJobId(), (Throwable)e);
                return null;
            }
        }
        return jobDetails.getNextRunTime();
    }
}

