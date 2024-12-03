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
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.collections.CollectionUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.mapi.executor;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.entity.MapiTaskMapping;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.mapi.executor.MapiStatusSenderService;
import com.atlassian.migration.agent.service.impl.MapiTaskMappingService;
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
import com.google.common.annotations.VisibleForTesting;
import java.time.Duration;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class MapiStatusConsumer
implements JobRunner {
    private static final JobRunnerKey RUNNER_KEY = JobRunnerKey.of((String)"migration-plugin:mapi-status-consumer-runner-key");
    private static final JobId JOB_ID = JobId.of((String)"migration-plugin:mapi-status-consumer-job-id");
    private static final Logger log = ContextLoggerFactory.getLogger(MapiStatusConsumer.class);
    private final MapiTaskMappingService mapiTaskMappingService;
    private final SchedulerService schedulerService;
    private final MapiStatusSenderService mapiStatusSenderService;
    private final MigrationAgentConfiguration agentConfiguration;

    @VisibleForTesting
    public MapiStatusConsumer(MapiTaskMappingService mapiTaskMappingService, SchedulerService schedulerService, MapiStatusSenderService mapiStatusSenderService, MigrationAgentConfiguration agentConfiguration) {
        this.mapiTaskMappingService = mapiTaskMappingService;
        this.schedulerService = schedulerService;
        this.mapiStatusSenderService = mapiStatusSenderService;
        this.agentConfiguration = agentConfiguration;
    }

    @PostConstruct
    public void postConstruct() throws SchedulerServiceException {
        if (this.agentConfiguration.isMapiTaskStatusSenderDisabled()) {
            this.schedulerService.unscheduleJob(JOB_ID);
            log.warn("MapiStatusConsumer poller is disabled. Job {} is removed.", (Object)JOB_ID);
        } else {
            this.schedulerService.registerJobRunner(RUNNER_KEY, (JobRunner)this);
            log.info("Successfully registered MapiStatusConsumer job {}.", (Object)RUNNER_KEY);
            this.schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.forInterval((long)this.jobInterval(), null)));
            log.info("Successfully started MapiStatusConsumer poller.");
        }
    }

    @PreDestroy
    public void cleanup() {
        this.schedulerService.unregisterJobRunner(RUNNER_KEY);
    }

    public JobRunnerResponse runJob(JobRunnerRequest req) {
        try {
            boolean eventsPending;
            while (eventsPending = this.sendBatchOfMapiTaskStatuses()) {
            }
            return JobRunnerResponse.success();
        }
        catch (Exception e) {
            return this.error(e);
        }
    }

    private boolean sendBatchOfMapiTaskStatuses() {
        List<MapiTaskMapping> batch = this.mapiTaskMappingService.getPendingTasks();
        if (CollectionUtils.isEmpty(batch)) {
            return false;
        }
        this.mapiStatusSenderService.processAndSendMapiTaskStatuses(batch);
        return true;
    }

    private JobRunnerResponse error(Exception e) {
        log.error("An unhandled exception occurred when processing a MapiStatusConsumer job request. Reason: {}", (Object)e.getMessage(), (Object)e);
        return JobRunnerResponse.failed((String)("MapiStatusConsumer job failed with reason " + e.getMessage()));
    }

    private long jobInterval() {
        return Duration.ofSeconds(this.agentConfiguration.getMapiStatusSenderJobIntervalInSeconds()).toMillis();
    }
}

