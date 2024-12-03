/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.core.util.Clock
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.config.Schedule$Type
 *  com.atlassian.scheduler.status.JobDetails
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.schedule.managers;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.event.events.cluster.ClusterDisableJobEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEnableJobEvent;
import com.atlassian.confluence.event.events.cluster.ClusterUpdateCronJobScheduleEvent;
import com.atlassian.confluence.event.events.cluster.ClusterUpdateSimpleJobScheduleEvent;
import com.atlassian.confluence.impl.schedule.managers.ScheduledJobNodeManager;
import com.atlassian.confluence.schedule.ExecutionStatus;
import com.atlassian.confluence.schedule.ManagedScheduledCronJob;
import com.atlassian.confluence.schedule.ManagedScheduledJob;
import com.atlassian.confluence.schedule.ManagedScheduledSimpleJob;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.confluence.schedule.ScheduledJobConfiguration;
import com.atlassian.confluence.schedule.ScheduledJobKey;
import com.atlassian.confluence.schedule.ScheduledJobStatus;
import com.atlassian.confluence.schedule.audit.AuditService;
import com.atlassian.confluence.schedule.audit.AuditingAction;
import com.atlassian.confluence.schedule.managers.ManagedScheduledJobException;
import com.atlassian.confluence.schedule.managers.ManagedScheduledJobRegistry;
import com.atlassian.confluence.schedule.managers.ScheduledJobManager;
import com.atlassian.confluence.schedule.managers.ScheduledJobStatusManager;
import com.atlassian.confluence.schedule.persistence.dao.ScheduledJobDao;
import com.atlassian.confluence.util.DefaultClock;
import com.atlassian.core.util.Clock;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.status.JobDetails;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultScheduledJobManager
implements ScheduledJobManager,
ScheduledJobNodeManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultScheduledJobManager.class);
    private final ScheduledJobDao jobDAO;
    private final SchedulerService schedulerService;
    private final ManagedScheduledJobRegistry jobRegistry;
    private final AuditService auditService;
    private final ScheduledJobStatusManager statusManager;
    private final TimeZoneManager timeZoneManager;
    private final EventPublisher eventPublisher;
    private final Clock clock;

    public DefaultScheduledJobManager(ScheduledJobDao jobDAO, SchedulerService schedulerService, ManagedScheduledJobRegistry jobRegistry, AuditService auditService, ScheduledJobStatusManager statusManager, TimeZoneManager timeZoneManager, EventPublisher eventPublisher) {
        this(jobDAO, schedulerService, jobRegistry, auditService, statusManager, timeZoneManager, eventPublisher, new DefaultClock());
    }

    @VisibleForTesting
    DefaultScheduledJobManager(ScheduledJobDao jobDAO, SchedulerService schedulerService, ManagedScheduledJobRegistry jobRegistry, AuditService auditService, ScheduledJobStatusManager statusManager, TimeZoneManager timeZoneManager, EventPublisher eventPublisher, Clock clock) {
        this.jobDAO = jobDAO;
        this.schedulerService = schedulerService;
        this.jobRegistry = jobRegistry;
        this.auditService = auditService;
        this.statusManager = statusManager;
        this.timeZoneManager = timeZoneManager;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    public List<ScheduledJobStatus> getScheduledJobs() {
        log.debug("Getting scheduled jobs");
        Collection<ManagedScheduledJob> managedJobs = this.jobRegistry.getManagedScheduledJobs();
        ArrayList<ScheduledJobStatus> jobs = new ArrayList<ScheduledJobStatus>();
        for (ManagedScheduledJob job : managedJobs) {
            ScheduledJobStatus status = this.statusManager.getScheduledJobStatus(job);
            if (status != null) {
                jobs.add(status);
                continue;
            }
            log.error("Scheduled job status is missing for {}", (Object)job);
        }
        return jobs;
    }

    @Override
    public ScheduledJobStatus getScheduledJob(JobId jobId) {
        ManagedScheduledJob job = this.jobRegistry.getManagedScheduledJob(jobId);
        return this.statusManager.getScheduledJobStatus(job);
    }

    @Override
    public Date updateCronJobSchedule(JobId jobId, String newCronSchedule) {
        Date nextRun = this.updateCronSchedule(jobId, newCronSchedule);
        ManagedScheduledJob managedJob = this.jobRegistry.getManagedScheduledJob(jobId);
        if (managedJob != null && managedJob.isLocalJob()) {
            this.eventPublisher.publish((Object)new ClusterUpdateCronJobScheduleEvent(this, new ScheduledJobKey(jobId.toString()), newCronSchedule));
        }
        return nextRun;
    }

    @Override
    public Date updateCronSchedule(JobId jobId, String newCronSchedule) {
        ManagedScheduledCronJob managedJob = this.getManagedScheduledJob(jobId, ManagedScheduledCronJob.class);
        if (managedJob == null || !managedJob.isEditable()) {
            log.error("Unable to complete changes. The '{}' job is not a cron job or not editable.", (Object)jobId);
            throw new ManagedScheduledJobException("Unable to complete changes. The '" + jobId + "' job is not a cron job or not editable.");
        }
        ScheduledJobConfiguration conf = this.jobDAO.getScheduledJobConfiguration(jobId);
        if (conf == null) {
            log.error("Unable to complete changes. The '{}' job config was not found.", (Object)jobId);
            throw new ManagedScheduledJobException("Unable to complete changes. The '" + jobId + "' job config was not found.");
        }
        String oldCronSchedule = conf.getCronSchedule();
        String defaultCronSchedule = managedJob.getDefaultCronExpression();
        if (oldCronSchedule == null) {
            oldCronSchedule = defaultCronSchedule;
        }
        this.auditService.auditCronJobScheduleChange(jobId, oldCronSchedule, newCronSchedule);
        if (StringUtils.equals((CharSequence)defaultCronSchedule, (CharSequence)newCronSchedule)) {
            conf.setCronSchedule(null);
        } else {
            conf.setCronSchedule(newCronSchedule);
        }
        this.jobDAO.saveScheduledJobConfiguration(jobId, conf);
        if (conf.isEnabled()) {
            Schedule newSchedule = Schedule.forCronExpression((String)newCronSchedule, (TimeZone)this.timeZoneManager.getDefaultTimeZone());
            Date nextRunDate = this.updateAtlassianSchedulerSchedule(jobId, newSchedule, null);
            if (nextRunDate != null) {
                this.jobDAO.updateNextOccurrence(jobId, nextRunDate);
            }
            return nextRunDate;
        }
        return null;
    }

    @Override
    public Date updateSimpleJobSchedule(JobId jobId, long repeatInterval) {
        Date nextRun = this.updateSimpleSchedule(jobId, repeatInterval);
        ManagedScheduledJob managedJob = this.jobRegistry.getManagedScheduledJob(jobId);
        if (managedJob != null && managedJob.isLocalJob()) {
            this.eventPublisher.publish((Object)new ClusterUpdateSimpleJobScheduleEvent(this, new ScheduledJobKey(jobId.toString()), repeatInterval));
        }
        return nextRun;
    }

    @Override
    public Date updateSimpleSchedule(JobId jobId, long repeatInterval) {
        ManagedScheduledSimpleJob managedJob = this.getManagedScheduledJob(jobId, ManagedScheduledSimpleJob.class);
        if (managedJob == null || !managedJob.isEditable()) {
            log.error("Unable to complete changes. The '{}' job is not a simple job or not editable.", (Object)jobId);
            throw new ManagedScheduledJobException("Unable to complete changes. The '" + jobId + "' job is not a simple job or not editable.");
        }
        ScheduledJobConfiguration conf = this.jobDAO.getScheduledJobConfiguration(jobId);
        if (conf == null) {
            log.error("Unable to complete changes. The '{}' job configuration not found.", (Object)jobId);
            throw new ManagedScheduledJobException("Unable to complete changes. The '" + jobId + "' job configuration could not be found.");
        }
        ScheduledJobStatus jobStatus = this.statusManager.getScheduledJobStatus(managedJob);
        if (jobStatus == null || !managedJob.canDisable()) {
            log.error("The '{}' job cannot be manually controlled.", (Object)jobId);
            throw new ManagedScheduledJobException("The '" + jobId + "' job cannot be manually controlled.");
        }
        Long oldRepeatInterval = conf.getRepeatInterval();
        Long defaultRepeatInterval = managedJob.getDefaultRepeatInterval();
        if (oldRepeatInterval == null) {
            oldRepeatInterval = defaultRepeatInterval;
        }
        this.auditService.auditSimpleJobScheduleChange(jobId, oldRepeatInterval, repeatInterval);
        if (defaultRepeatInterval == repeatInterval) {
            conf.setRepeatInterval(null);
        } else {
            conf.setRepeatInterval(repeatInterval);
        }
        this.jobDAO.saveScheduledJobConfiguration(jobId, conf);
        if (conf.isEnabled()) {
            Schedule newSchedule = this.rescheduleSimpleJob(repeatInterval);
            Date nextRunDate = newSchedule.getIntervalScheduleInfo().getFirstRunTime();
            JobConfig newJobConfig = managedJob.getJobConfig().withSchedule(newSchedule);
            try {
                this.schedulerService.scheduleJob(managedJob.getJobId(), newJobConfig);
            }
            catch (SchedulerServiceException e) {
                log.error("Unable to reschedule job '{}' with exception ", (Object)jobId, (Object)e);
                throw new ManagedScheduledJobException("Unable to reschedule job '" + jobId + "'", e);
            }
            if (nextRunDate != null) {
                this.jobDAO.updateNextOccurrence(jobId, nextRunDate);
            }
            return nextRunDate;
        }
        return null;
    }

    @Override
    public void runNow(JobId jobId) {
        this.auditService.auditAction(jobId, AuditingAction.RUN);
        ManagedScheduledJob managedJob = this.jobRegistry.getManagedScheduledJob(jobId);
        if (managedJob == null || !managedJob.canRunAdhoc()) {
            log.error("The '{}' job cannot be manually controlled.", (Object)jobId);
            throw new ManagedScheduledJobException("The '" + jobId + "' job cannot be manually controlled.");
        }
        ScheduledJobStatus status = this.statusManager.getScheduledJobStatus(managedJob);
        if (status == null) {
            log.error("Cannot determine status of job '{}', will not run.", (Object)jobId);
            throw new ManagedScheduledJobException("Cannot determine status of job '" + jobId + "', will not run.");
        }
        if (!status.isManuallyRunnable()) {
            log.error("Job '{}' is already running.", (Object)jobId);
            throw new ManagedScheduledJobException("Job '" + jobId + "' is already running.");
        }
        try {
            JobConfig jobConfig = managedJob.getJobConfig();
            Map parameters = jobConfig.getParameters();
            JobConfig runNowJobConfig = jobConfig.withRunMode(RunMode.RUN_LOCALLY).withSchedule(Schedule.runOnce(null)).withParameters(ScheduleUtil.withoutJitterSecs(parameters));
            JobId newJobId = DefaultScheduledJobManager.scheduledJobKeyToJobIdWithRunNowSuffix(managedJob.getJobId());
            this.schedulerService.scheduleJob(newJobId, runNowJobConfig);
        }
        catch (SchedulerServiceException e) {
            log.error("Unable to schedule the job '{}' with exception ", (Object)jobId, (Object)e);
            throw new ManagedScheduledJobException("Unable to schedule the job '" + jobId + "'", e);
        }
    }

    @Override
    public void disable(JobId jobId) {
        this.disableJob(jobId);
        ManagedScheduledJob managedJob = this.jobRegistry.getManagedScheduledJob(jobId);
        if (managedJob != null && managedJob.isLocalJob()) {
            this.eventPublisher.publish((Object)new ClusterDisableJobEvent(this, new ScheduledJobKey(jobId.toString())));
        }
    }

    @Override
    public void disableJob(JobId jobId) {
        this.auditService.auditAction(jobId, AuditingAction.DISABLE);
        ManagedScheduledJob managedJob = this.jobRegistry.getManagedScheduledJob(jobId);
        if (managedJob == null) {
            log.error("The '{}' job is not managed and cannot be manually controlled.", (Object)jobId);
            throw new ManagedScheduledJobException("The '" + jobId + "' job is not managed and cannot be manually controlled.");
        }
        ScheduledJobStatus jobStatus = this.statusManager.getScheduledJobStatus(managedJob);
        if (jobStatus == null || !managedJob.canDisable()) {
            log.error("The '{}' job cannot be manually disabled.", (Object)jobId);
            throw new ManagedScheduledJobException("The '" + jobId + "' job cannot be manually controlled.");
        }
        this.schedulerService.unscheduleJob(jobId);
        ScheduledJobConfiguration jobConfiguration = this.jobDAO.getScheduledJobConfiguration(jobId);
        if (jobConfiguration == null) {
            log.error("The '{}' job config was not found.", (Object)jobId);
            throw new ManagedScheduledJobException("The '" + jobId + "' job config was not found.");
        }
        jobConfiguration.setEnabled(false);
        this.jobDAO.saveScheduledJobConfiguration(jobId, jobConfiguration);
        this.updateDisabledJobExecutionStatus(jobId, jobStatus);
    }

    @Override
    public void enable(JobId jobId) {
        this.enableJob(jobId);
        ManagedScheduledJob managedJob = this.jobRegistry.getManagedScheduledJob(jobId);
        if (managedJob != null && managedJob.isLocalJob()) {
            this.eventPublisher.publish((Object)new ClusterEnableJobEvent(this, new ScheduledJobKey(jobId.toString())));
        }
    }

    @Override
    public void enableJob(JobId jobId) {
        this.auditService.auditAction(jobId, AuditingAction.ENABLE);
        ManagedScheduledJob managedJob = this.jobRegistry.getManagedScheduledJob(jobId);
        if (managedJob == null) {
            log.error("The '{}' job is not managed and cannot be manually controlled.", (Object)jobId);
            throw new ManagedScheduledJobException("The '" + jobId + "' job is not managed and cannot be manually controlled.");
        }
        ScheduledJobStatus jobStatus = this.statusManager.getScheduledJobStatus(managedJob);
        if (jobStatus == null || !managedJob.canDisable()) {
            log.error("The '{}' job cannot be manually controlled.", (Object)jobId);
            throw new ManagedScheduledJobException("The '" + jobId + "' job cannot be manually controlled.");
        }
        ScheduledJobConfiguration jobConfiguration = this.jobDAO.getScheduledJobConfiguration(jobId);
        if (jobConfiguration == null) {
            log.error("The '{}' job config was not found.", (Object)jobId);
            throw new ManagedScheduledJobException("The '" + jobId + "' job config was not found.");
        }
        jobConfiguration.setEnabled(true);
        this.jobDAO.saveScheduledJobConfiguration(jobId, jobConfiguration);
        this.updateEnabledJobExecutionStatus(jobId, jobStatus);
        try {
            Schedule schedule = managedJob.getJobConfig().getSchedule();
            if (schedule.getType().equals((Object)Schedule.Type.INTERVAL)) {
                Long repeatInterval = schedule.getIntervalScheduleInfo().getIntervalInMillis();
                Long oldRepeatInterval = jobConfiguration.getRepeatInterval();
                if (oldRepeatInterval != null) {
                    repeatInterval = oldRepeatInterval;
                }
                schedule = this.rescheduleSimpleJob(repeatInterval);
                this.jobDAO.updateNextOccurrence(jobId, schedule.getIntervalScheduleInfo().getFirstRunTime());
            }
            JobConfig jobConfig = ScheduleUtil.getJobConfig(jobConfiguration, managedJob, this.timeZoneManager.getDefaultTimeZone());
            if (schedule.getType().equals((Object)Schedule.Type.INTERVAL)) {
                jobConfig = jobConfig.withSchedule(schedule);
            }
            this.schedulerService.scheduleJob(jobId, jobConfig);
        }
        catch (SchedulerServiceException e) {
            log.error("Unable to enable and schedule the job '{}' with exception ", (Object)jobId, (Object)e);
            throw new ManagedScheduledJobException("Unable to enable and schedule the job '" + jobId + "'", e);
        }
    }

    public void updateEnabledJobExecutionStatus(JobId jobId, ScheduledJobStatus jobStatus) {
        ExecutionStatus currentStatus = jobStatus.getStatus();
        ExecutionStatus newStatus = currentStatus == ExecutionStatus.DISABLED || currentStatus == ExecutionStatus.SCHEDULED ? ExecutionStatus.SCHEDULED : ExecutionStatus.RUNNING;
        this.jobDAO.updateStatus(jobId, newStatus);
    }

    public void updateDisabledJobExecutionStatus(JobId jobId, ScheduledJobStatus jobStatus) {
        ExecutionStatus currentStatus = jobStatus.getStatus();
        ExecutionStatus newStatus = currentStatus == ExecutionStatus.DISABLED || currentStatus == ExecutionStatus.SCHEDULED ? ExecutionStatus.DISABLED : ExecutionStatus.DISABLED_MANUALLY_RUNNING;
        this.jobDAO.updateStatus(jobId, newStatus);
    }

    @Override
    public String getCronExpression(JobId jobId) {
        ManagedScheduledJob managedJob = this.jobRegistry.getManagedScheduledJob(jobId);
        if (!(managedJob instanceof ManagedScheduledCronJob)) {
            return null;
        }
        ManagedScheduledCronJob managedCronJob = (ManagedScheduledCronJob)managedJob;
        ScheduledJobConfiguration job = this.jobDAO.getScheduledJobConfiguration(jobId);
        if (job == null) {
            return managedCronJob.getDefaultCronExpression();
        }
        String cronExpression = job.getCronSchedule();
        return cronExpression == null ? managedCronJob.getDefaultCronExpression() : cronExpression;
    }

    @Override
    public Long getRepeatInterval(JobId jobId) {
        ManagedScheduledSimpleJob simpleJob = this.getManagedScheduledJob(jobId, ManagedScheduledSimpleJob.class);
        if (simpleJob == null) {
            return null;
        }
        ScheduledJobConfiguration configuration = this.jobDAO.getScheduledJobConfiguration(jobId);
        if (configuration == null) {
            return simpleJob.getDefaultRepeatInterval();
        }
        Long repeatInterval = configuration.getRepeatInterval();
        return repeatInterval == null ? simpleJob.getDefaultRepeatInterval() : repeatInterval;
    }

    public static JobId scheduledJobKeyToJobIdWithRunNowSuffix(JobId jobId) {
        return JobId.of((String)(jobId + "-runNow-" + System.currentTimeMillis()));
    }

    public static JobId jobIdToScheduledJobKeyWithoutRunNowSuffix(JobId jobId) {
        String id = jobId.toString();
        int idx = id.indexOf("-runNow-");
        String withoutRunNowSuffix = idx < 0 ? id : id.substring(0, idx);
        return JobId.of((String)withoutRunNowSuffix);
    }

    private Date updateAtlassianSchedulerSchedule(JobId jobId, Schedule newSchedule, @Nullable Map<String, Serializable> parameters) {
        JobDetails jobDetails = this.schedulerService.getJobDetails(jobId);
        if (jobDetails == null || !jobDetails.isRunnable()) {
            return null;
        }
        Map params = parameters == null ? jobDetails.getParameters() : ImmutableMap.builder().putAll(jobDetails.getParameters()).putAll(parameters).build();
        JobConfig newJobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)jobDetails.getJobRunnerKey()).withRunMode(jobDetails.getRunMode()).withSchedule(newSchedule).withParameters(params);
        try {
            this.schedulerService.scheduleJob(jobId, newJobConfig);
            return ScheduleUtil.calculateNextRunTime(this.schedulerService, newJobConfig, null);
        }
        catch (SchedulerServiceException e) {
            log.error("Unable to reschedule job '{}' with exception ", (Object)jobId, (Object)e);
            throw new ManagedScheduledJobException("Unable to reschedule job '" + jobId + "'", e);
        }
    }

    private <T> T getManagedScheduledJob(JobId jobId, Class<T> type) {
        ManagedScheduledJob managedJob = this.jobRegistry.getManagedScheduledJob(jobId);
        return (T)(type.isInstance(managedJob) ? managedJob : null);
    }

    private Schedule rescheduleSimpleJob(long repeatInterval) {
        return Schedule.forInterval((long)repeatInterval, (Date)new Date(this.clock.getCurrentDate().getTime() + repeatInterval));
    }
}

