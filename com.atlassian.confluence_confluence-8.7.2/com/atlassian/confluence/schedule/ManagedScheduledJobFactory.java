/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.schedule;

import com.atlassian.confluence.schedule.ExecutionStatus;
import com.atlassian.confluence.schedule.ManagedScheduledJob;
import com.atlassian.confluence.schedule.ManagedScheduledJobInitialiser;
import com.atlassian.confluence.schedule.ManagedScheduledJobRegistrationService;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.confluence.schedule.ScheduledJobConfiguration;
import com.atlassian.confluence.schedule.ScheduledJobHistory;
import com.atlassian.confluence.schedule.ScheduledJobStatus;
import com.atlassian.confluence.schedule.managers.DefaultManagedScheduledJobRegistry;
import com.atlassian.confluence.schedule.managers.ScheduledJobManager;
import com.atlassian.confluence.schedule.persistence.dao.ScheduledJobDao;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedScheduledJobFactory
implements ManagedScheduledJobRegistrationService,
ManagedScheduledJobInitialiser {
    private static final Logger log = LoggerFactory.getLogger(ManagedScheduledJobFactory.class);
    private final SchedulerService schedulerService;
    private final TimeZoneManager timeZoneManager;
    private final ScheduledJobDao scheduledJobDAO;
    private final DefaultManagedScheduledJobRegistry registry;
    private final ScheduledJobManager scheduledJobManager;
    private boolean factoryInitialised;

    protected ManagedScheduledJobFactory() {
        this.schedulerService = null;
        this.timeZoneManager = null;
        this.scheduledJobDAO = null;
        this.registry = null;
        this.scheduledJobManager = null;
    }

    public ManagedScheduledJobFactory(SchedulerService schedulerService, TimeZoneManager timeZoneManager, ScheduledJobDao scheduledJobDAO, DefaultManagedScheduledJobRegistry registry, ScheduledJobManager scheduledJobManager) {
        this.schedulerService = schedulerService;
        this.timeZoneManager = timeZoneManager;
        this.scheduledJobDAO = scheduledJobDAO;
        this.registry = registry;
        this.scheduledJobManager = scheduledJobManager;
    }

    @Override
    public synchronized void initialiseManagedScheduledJobs() {
        if (this.factoryInitialised) {
            return;
        }
        for (ManagedScheduledJob job : this.registry.getManagedScheduledJobs()) {
            this.initialiseManagedScheduledJob(job);
        }
        this.factoryInitialised = true;
    }

    @Override
    public synchronized void registerManagedScheduledJob(ManagedScheduledJob managedScheduledJob) {
        JobId jobId = managedScheduledJob.getJobId();
        if (this.isRegisteredConflict(managedScheduledJob)) {
            log.error("Refuse to register job with ID {} - already exists with JobRunnerKey {}", (Object)jobId, (Object)this.registry.getManagedScheduledJob(jobId).getJobConfig().getJobRunnerKey());
            return;
        }
        this.registry.addManagedScheduledJob(managedScheduledJob);
        if (this.factoryInitialised) {
            this.initialiseManagedScheduledJob(managedScheduledJob);
        }
    }

    @Override
    public synchronized void unregisterManagedScheduledJob(ManagedScheduledJob managedScheduledJob) {
        if (!this.registry.isManaged(managedScheduledJob.getJobId()) || this.isRegisteredConflict(managedScheduledJob)) {
            log.error("Refuse to deregister job with ID {} - either not registered or registered with a different JobRunnerKey", (Object)managedScheduledJob.getJobId());
            return;
        }
        this.registry.removeManagedScheduledJob(managedScheduledJob);
        if (this.factoryInitialised) {
            this.schedulerService.unscheduleJob(managedScheduledJob.getJobId());
        }
    }

    private boolean isRegisteredConflict(ManagedScheduledJob managedScheduledJob) {
        JobId jobId = managedScheduledJob.getJobId();
        JobRunnerKey runnerKey = managedScheduledJob.getJobConfig().getJobRunnerKey();
        return this.registry.isManaged(jobId) && !runnerKey.equals((Object)this.registry.getManagedScheduledJob(jobId).getJobConfig().getJobRunnerKey());
    }

    private void initialiseManagedScheduledJob(ManagedScheduledJob managedScheduledJob) {
        if (managedScheduledJob == null) {
            log.error("Null ManagedScheduledJobType, skipping.");
            return;
        }
        JobId jobId = managedScheduledJob.getJobId();
        log.info("Initialising managed scheduled job: {}", (Object)jobId);
        ScheduledJobConfiguration configuration = this.scheduledJobDAO.getScheduledJobConfiguration(jobId);
        if (configuration == null) {
            configuration = new ScheduledJobConfiguration();
            if (managedScheduledJob.disabledByDefault()) {
                configuration.setEnabled(false);
            }
            log.info("Creating and saving configuration for job: {}; ScheduledJobConfiguration[enabled: {}; cron expression: {}; repeat interval: {}]", new Object[]{jobId, configuration.isEnabled(), configuration.getCronSchedule(), configuration.getRepeatInterval()});
            this.scheduledJobDAO.saveScheduledJobConfiguration(jobId, configuration);
        }
        List<ScheduledJobHistory> existingHistory = this.scheduledJobManager.getScheduledJob(jobId).getHistory();
        ScheduledJobStatus jobStatus = new ScheduledJobStatus(managedScheduledJob.getJobId(), existingHistory);
        jobStatus.setStatus(configuration.isEnabled() ? ExecutionStatus.SCHEDULED : ExecutionStatus.DISABLED);
        if (configuration.isEnabled()) {
            JobConfig jobConfig = ScheduleUtil.getJobConfig(configuration, managedScheduledJob, this.timeZoneManager.getDefaultTimeZone());
            try {
                log.info("Scheduling job: {}; {}", (Object)jobId, (Object)jobConfig);
                this.schedulerService.scheduleJob(jobId, jobConfig);
                jobStatus.setNextExecution(this.schedulerService.calculateNextRunTime(jobConfig.getSchedule()));
            }
            catch (SchedulerServiceException e) {
                log.error("Unable to schedule job (" + jobId + ").", (Throwable)e);
            }
        } else {
            log.info("Disabling job: {}", (Object)jobId);
            this.schedulerService.unscheduleJob(jobId);
        }
        log.info("Saving job status. Job: {}; Status: {}", (Object)jobId, (Object)jobStatus.getStatus());
        this.scheduledJobDAO.saveScheduledJobStatus(jobId, jobStatus);
    }
}

