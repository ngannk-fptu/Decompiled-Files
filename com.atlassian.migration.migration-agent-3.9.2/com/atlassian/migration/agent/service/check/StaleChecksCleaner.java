/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.check;

import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.check.CheckResultsService;
import com.atlassian.scheduler.JobRunner;
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
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;

public class StaleChecksCleaner
implements JobRunner {
    private static final JobRunnerKey RUNNER_KEY = JobRunnerKey.of((String)"migration-plugin:stale-checks-cleaner-key");
    private static final JobId JOB_ID = JobId.of((String)"migration-plugin:stale-checks-cleaner-job-id");
    private static final Duration RESCHEDULE_INTERVAL = Duration.ofMinutes(31L);
    private static final Logger log = ContextLoggerFactory.getLogger(StaleChecksCleaner.class);
    private final SchedulerService schedulerService;
    private final CheckResultsService checkResultsService;

    public StaleChecksCleaner(SchedulerService schedulerService, CheckResultsService checkResultsService) {
        this.checkResultsService = checkResultsService;
        this.schedulerService = schedulerService;
    }

    @PostConstruct
    public void postConstruct() throws SchedulerServiceException {
        this.schedulerService.registerJobRunner(RUNNER_KEY, (JobRunner)this);
        log.debug("Successfully registered StaleChecksCleaner job {}.", (Object)RUNNER_KEY);
        this.schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.forInterval((long)RESCHEDULE_INTERVAL.toMillis(), (Date)new Date(System.currentTimeMillis() + 10000L))));
        log.debug("Successfully started StaleChecksCleaner.");
    }

    @PreDestroy
    public void cleanup() {
        this.schedulerService.unregisterJobRunner(RUNNER_KEY);
    }

    public JobRunnerResponse runJob(JobRunnerRequest req) {
        log.info("Cleaning stale checks");
        this.checkResultsService.cleanStaleChecks();
        return JobRunnerResponse.success();
    }
}

