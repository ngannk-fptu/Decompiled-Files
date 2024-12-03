/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.basicauth.job;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.basicauth.BasicAuthConfig;
import com.atlassian.plugins.authentication.impl.basicauth.service.BasicAuthDao;
import com.atlassian.plugins.authentication.impl.basicauth.service.CachingBasicAuthService;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import java.time.Duration;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@ExportAsService(value={LifecycleAware.class})
public class UpdateBasicAuthConfigJob
implements LifecycleAware {
    private static final Logger log = LoggerFactory.getLogger(UpdateBasicAuthConfigJob.class);
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)UpdateBasicAuthConfigJob.class.getName());
    private static final JobId JOB_ID = JobId.of((String)(UpdateBasicAuthConfigJob.class.getName() + ":job"));
    @VisibleForTesting
    static final String BASIC_AUTH_UPDATE_JOB_INTERVAL_MILLISECONDS_PROPERTY = "com.atlassian.plugins.authentication.basicauth.update.job.interval.milliseconds";
    @VisibleForTesting
    static final Duration DEFAULT_INTERVAL = Duration.ofMinutes(1L);
    private final BasicAuthDao basicAuthDao;
    private final CachingBasicAuthService cachingBasicAuthService;
    private final SchedulerService schedulerService;

    @Inject
    public UpdateBasicAuthConfigJob(BasicAuthDao basicAuthDao, CachingBasicAuthService cachingBasicAuthService, @ComponentImport SchedulerService schedulerService) {
        this.basicAuthDao = basicAuthDao;
        this.cachingBasicAuthService = cachingBasicAuthService;
        this.schedulerService = schedulerService;
    }

    public void onStart() {
        try {
            this.schedulerService.registerJobRunner(JOB_RUNNER_KEY, this::runJob);
            this.schedulerService.scheduleJob(JOB_ID, this.getJobConfig());
            log.debug(this.getClass().getName() + " job handler registered");
        }
        catch (SchedulerServiceException e) {
            String message = "Couldn't initialise scheduler for " + this.getClass().getName();
            log.error(message, (Throwable)e);
            throw new RuntimeException(message, e);
        }
    }

    private JobConfig getJobConfig() {
        Schedule schedule = Schedule.forInterval((long)Long.getLong(BASIC_AUTH_UPDATE_JOB_INTERVAL_MILLISECONDS_PROPERTY, DEFAULT_INTERVAL.toMillis()), (Date)new Date());
        log.info("Basic authentication update job has an interval {}ms", (Object)schedule.getIntervalScheduleInfo().getIntervalInMillis());
        return JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withRunMode(RunMode.RUN_LOCALLY).withSchedule(schedule);
    }

    public void onStop() {
        this.schedulerService.unregisterJobRunner(JOB_RUNNER_KEY);
        log.debug(this.getClass().getName() + " job handler unregistered");
    }

    @VisibleForTesting
    JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        BasicAuthConfig basicAuthConfig = this.basicAuthDao.get();
        if (!basicAuthConfig.equals(this.cachingBasicAuthService.getConfig())) {
            this.cachingBasicAuthService.update();
        }
        return JobRunnerResponse.success();
    }
}

