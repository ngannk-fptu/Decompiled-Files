/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.Schedule
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.confluence.image.effects;

import com.atlassian.confluence.image.effects.CacheCleanup;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.Schedule;
import java.util.Objects;
import java.util.TimeZone;
import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService
@Named(value="cacheCleanupJobScheduler")
public class CacheCleanupJobScheduler
implements LifecycleAware {
    private static final JobId JOB_ID = JobId.of((String)"image-effects-cache-cleanup-job");
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)CacheCleanup.class.getName());
    private static final String CRON_EXPRESSION = "0 0 2 * * ?";
    private final SchedulerService schedulerService;
    private final TimeZoneManager timeZoneManager;

    @Inject
    public CacheCleanupJobScheduler(@ComponentImport SchedulerService schedulerService, @ComponentImport TimeZoneManager timeZoneManager) {
        this.schedulerService = schedulerService;
        this.timeZoneManager = Objects.requireNonNull(timeZoneManager);
    }

    public void onStart() {
        try {
            this.schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withSchedule(Schedule.forCronExpression((String)CRON_EXPRESSION, (TimeZone)this.timeZoneManager.getDefaultTimeZone())));
        }
        catch (SchedulerServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public void onStop() {
        this.schedulerService.unregisterJobRunner(JOB_RUNNER_KEY);
    }
}

