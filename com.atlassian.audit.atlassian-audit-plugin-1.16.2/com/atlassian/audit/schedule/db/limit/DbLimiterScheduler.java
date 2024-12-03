/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
package com.atlassian.audit.schedule.db.limit;

import com.atlassian.audit.ao.dao.AuditEntityDao;
import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import com.atlassian.audit.schedule.db.limit.DbLimiterJobRunner;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbLimiterScheduler {
    public static final String AUDIT_DB_LIMIT_ROWS_KEY = "plugin.audit.db.limit.rows";
    public static final int AUDIT_DB_LIMIT_ROWS_DEFAULT = 10000000;
    public static final String AUDIT_DB_LIMIT_BUFFER_ROWS_KEY = "plugin.audit.db.limit.buffer.rows";
    public static final int AUDIT_DB_LIMIT_BUFFER_ROWS_DEFAULT = 1000;
    private static final JobRunnerKey AUDIT_DB_LIMITER_JOB_RUNNER_KEY = JobRunnerKey.of((String)DbLimiterJobRunner.class.getName());
    private static final JobId AUDIT_DB_LIMITER_JOB_ID = JobId.of((String)DbLimiterJobRunner.class.getName());
    private static final Logger log = LoggerFactory.getLogger(DbLimiterJobRunner.class);
    private static final int DB_LIMITER_SCHEDULE_INTERVAL_DEFAULT_MINS = 60;
    private static final String DB_LIMITER_SCHEDULE_INTERVAL_KEY = "plugin.audit.schedule.db.limiter.interval.mins";
    private final SchedulerService schedulerService;
    private final AuditEntityDao auditEntityDao;
    private final Supplier<Integer> jobIntervalInMinuteSupplier;
    private final int rowsLimit;
    private final int rowsLimitBuffer;

    public DbLimiterScheduler(SchedulerService schedulerService, AuditEntityDao auditEntityDao, PropertiesProvider propertiesProvider) {
        this.schedulerService = schedulerService;
        this.auditEntityDao = auditEntityDao;
        this.jobIntervalInMinuteSupplier = () -> propertiesProvider.getInteger(DB_LIMITER_SCHEDULE_INTERVAL_KEY, 60);
        this.rowsLimit = propertiesProvider.getInteger(AUDIT_DB_LIMIT_ROWS_KEY, 10000000);
        this.rowsLimitBuffer = propertiesProvider.getInteger(AUDIT_DB_LIMIT_BUFFER_ROWS_KEY, 1000);
    }

    public void registerJob() {
        this.schedulerService.registerJobRunner(AUDIT_DB_LIMITER_JOB_RUNNER_KEY, (JobRunner)new DbLimiterJobRunner(this.auditEntityDao, this.rowsLimit, this.rowsLimitBuffer));
        JobConfig config = JobConfig.forJobRunnerKey((JobRunnerKey)AUDIT_DB_LIMITER_JOB_RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.forInterval((long)TimeUnit.MINUTES.toMillis(this.jobIntervalInMinuteSupplier.get().intValue()), (Date)Date.from(Instant.now().plus((long)this.jobIntervalInMinuteSupplier.get().intValue(), ChronoUnit.MINUTES))));
        try {
            this.schedulerService.scheduleJob(AUDIT_DB_LIMITER_JOB_ID, config);
            log.info("Jobrunner {} registered", (Object)AUDIT_DB_LIMITER_JOB_RUNNER_KEY);
        }
        catch (SchedulerServiceException e) {
            log.error("Could not schedule auditing DB limiter job", (Throwable)e);
        }
    }

    public void unregisterJob() {
        this.schedulerService.unregisterJobRunner(AUDIT_DB_LIMITER_JOB_RUNNER_KEY);
        log.info("Jobrunner {} unregistered", (Object)AUDIT_DB_LIMITER_JOB_RUNNER_KEY);
    }
}

