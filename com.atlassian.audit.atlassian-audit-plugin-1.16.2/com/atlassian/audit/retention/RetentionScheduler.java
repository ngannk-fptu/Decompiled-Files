/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditRetentionConfigService
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.retention;

import com.atlassian.audit.ao.dao.AuditEntityDao;
import com.atlassian.audit.api.AuditRetentionConfigService;
import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import com.atlassian.audit.retention.RetentionJobRunner;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetentionScheduler {
    private static final JobRunnerKey AUDIT_CLEANUP_JOB_RUNNER_KEY = JobRunnerKey.of((String)RetentionJobRunner.class.getName());
    private static final JobId AUDIT_CLEANUP_JOB_ID = JobId.of((String)RetentionJobRunner.class.getName());
    private static final Logger log = LoggerFactory.getLogger(RetentionJobRunner.class);
    private final SchedulerService schedulerService;
    private final AuditEntityDao auditEntityDao;
    private final AuditRetentionConfigService auditRetentionConfigService;
    private final PluginSettingsFactory settingsFactory;
    private final int cleanUpInterval;

    public RetentionScheduler(SchedulerService schedulerService, AuditRetentionConfigService auditRetentionConfigService, AuditEntityDao auditEntityDao, PluginSettingsFactory settingsFactory, PropertiesProvider propertiesProvider) {
        this.schedulerService = schedulerService;
        this.auditRetentionConfigService = auditRetentionConfigService;
        this.auditEntityDao = auditEntityDao;
        this.settingsFactory = settingsFactory;
        this.cleanUpInterval = propertiesProvider.getInteger("plugin.audit.retention.interval.hours", 23);
    }

    public void registerJob() {
        this.schedulerService.registerJobRunner(AUDIT_CLEANUP_JOB_RUNNER_KEY, (JobRunner)new RetentionJobRunner(this.auditEntityDao, this.auditRetentionConfigService, this.settingsFactory, this.cleanUpInterval));
        JobConfig config = JobConfig.forJobRunnerKey((JobRunnerKey)AUDIT_CLEANUP_JOB_RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.forCronExpression((String)"0 0 0 1/1 * ? *"));
        try {
            this.schedulerService.scheduleJob(AUDIT_CLEANUP_JOB_ID, config);
            log.info("Jobrunner {} registered", (Object)AUDIT_CLEANUP_JOB_RUNNER_KEY);
        }
        catch (SchedulerServiceException e) {
            log.error("Could not schedule auditing cleanup job", (Throwable)e);
        }
    }

    public void unregisterJob() {
        this.schedulerService.unregisterJobRunner(AUDIT_CLEANUP_JOB_RUNNER_KEY);
        log.info("Jobrunner {} unregistered", (Object)AUDIT_CLEANUP_JOB_RUNNER_KEY);
    }
}

