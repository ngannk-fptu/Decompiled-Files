/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.audit.cache.schedule;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.audit.ao.service.CachedActionsService;
import com.atlassian.audit.ao.service.CachedCategoriesService;
import com.atlassian.audit.cache.schedule.BuildCacheJobRunner;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
@ThreadSafe
public class BuildCacheJobScheduler {
    @VisibleForTesting
    public static final JobId JOB_ID = JobId.of((String)"atlassian.audit.cache.build");
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)"atlassian.audit.cache.build");
    private static final String SCHEDULE_LOCK_OBJECT = "";
    private final ActiveObjects ao;
    private final CachedActionsService cachedActionsService;
    private final CachedCategoriesService cachedCategoriesService;
    private final SchedulerService schedulerService;

    public BuildCacheJobScheduler(ActiveObjects ao, CachedActionsService cachedActionsService, CachedCategoriesService cachedCategoriesService, SchedulerService schedulerService) {
        this.ao = Objects.requireNonNull(ao, "ao");
        this.cachedActionsService = Objects.requireNonNull(cachedActionsService, "cachedActionsService");
        this.cachedCategoriesService = Objects.requireNonNull(cachedCategoriesService, "cachedCategoriesService");
        this.schedulerService = Objects.requireNonNull(schedulerService, "schedulerService");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void scheduleIfNeeded() throws SchedulerServiceException {
        JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.runOnce((Date)Date.from(Instant.now().plusSeconds(1L))));
        String string = SCHEDULE_LOCK_OBJECT;
        synchronized (SCHEDULE_LOCK_OBJECT) {
            this.registerJobRunnerIfNeeded();
            this.schedulerService.scheduleJob(JOB_ID, jobConfig);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    private void registerJobRunnerIfNeeded() {
        if (this.schedulerService.getRegisteredJobRunnerKeys().contains(JOB_RUNNER_KEY)) {
            return;
        }
        this.schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)new BuildCacheJobRunner(this.ao, this.cachedActionsService, this.cachedCategoriesService));
    }
}

