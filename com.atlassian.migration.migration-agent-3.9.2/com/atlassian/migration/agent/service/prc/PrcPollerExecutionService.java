/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.prc.client.PollerExecutionService
 *  com.atlassian.migration.prc.client.PrcPollTask
 *  com.atlassian.migration.prc.client.PrcServiceClient
 *  com.atlassian.migration.prc.client.model.PollerConfig
 *  com.atlassian.migration.prc.client.model.StartPollTaskResponse
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
 *  lombok.Generated
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.prc;

import com.atlassian.migration.agent.mapi.external.model.PublicApiException;
import com.atlassian.migration.agent.service.prc.PollerConfigHandler;
import com.atlassian.migration.agent.service.prc.model.PollerTaskContext;
import com.atlassian.migration.prc.client.PollerExecutionService;
import com.atlassian.migration.prc.client.PrcPollTask;
import com.atlassian.migration.prc.client.PrcServiceClient;
import com.atlassian.migration.prc.client.model.PollerConfig;
import com.atlassian.migration.prc.client.model.StartPollTaskResponse;
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
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrcPollerExecutionService
implements PollerExecutionService,
JobRunner {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(PrcPollerExecutionService.class);
    private static final String PRC_POLLER_JOB_RUNNER_KEY = "migration-plugin:prc-poller-runner-key.";
    private static final String PRC_POLLER_JOB_ID = "migration-plugin:prc-poller-job-id.";
    private static final String POLLER_STARTED = "Poller started successfully for cloudId = %s";
    private static final String JOB_START_TIME = "jobStartTime";
    private static ConcurrentMap<String, PollerTaskContext> cloudSitePollers = new ConcurrentHashMap<String, PollerTaskContext>();
    private final SchedulerService schedulerService;
    private final PollerConfigHandler pollerConfigHandler;

    public PrcPollerExecutionService(SchedulerService schedulerService, PollerConfigHandler pollerConfigHandler) {
        this.schedulerService = schedulerService;
        this.pollerConfigHandler = pollerConfigHandler;
    }

    @PostConstruct
    public void init() {
        this.schedulerService.registerJobRunner(JobRunnerKey.of((String)PRC_POLLER_JOB_RUNNER_KEY), (JobRunner)this);
    }

    @PreDestroy
    public void cleanUp() {
        this.schedulerService.unregisterJobRunner(JobRunnerKey.of((String)PRC_POLLER_JOB_RUNNER_KEY));
    }

    @NotNull
    public StartPollTaskResponse handlePolling(PollerConfig pollerConfig) {
        try {
            Map<String, Serializable> jobParams = this.pollerConfigHandler.getJobParametersFromConfig(pollerConfig);
            jobParams.put(JOB_START_TIME, Long.valueOf(System.currentTimeMillis()));
            Long intervalTimeInMillis = (long)pollerConfig.getPollingDelayInSec() * 1000L;
            JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)JobRunnerKey.of((String)PRC_POLLER_JOB_RUNNER_KEY)).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.forInterval((long)intervalTimeInMillis, null)).withParameters(jobParams);
            this.schedulerService.scheduleJob(this.getJobId(pollerConfig.getCloudId()), jobConfig);
        }
        catch (SchedulerServiceException e) {
            throw new RuntimeException(e);
        }
        return new StartPollTaskResponse(String.format(POLLER_STARTED, pollerConfig.getCloudId()));
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        PrcPollTask prcPollTask = null;
        try {
            Map jobParams = jobRunnerRequest.getJobConfig().getParameters();
            PollerConfig pollerConfig = this.pollerConfigHandler.getPollerConfigFromJobParams(jobParams);
            Long startTime = (Long)jobParams.get(JOB_START_TIME);
            this.createPollerIfNotExist(startTime, pollerConfig);
            prcPollTask = ((PollerTaskContext)cloudSitePollers.get(pollerConfig.getCloudId())).getPrcPollTask();
            prcPollTask.poll();
            return JobRunnerResponse.success();
        }
        catch (PublicApiException.ResourceNotFound ex) {
            this.schedulerService.unscheduleJob(jobRunnerRequest.getJobId());
            return JobRunnerResponse.aborted((String)ex.getMessage());
        }
        catch (Exception ex) {
            if (prcPollTask != null && prcPollTask.shouldBeTerminated((Throwable)ex)) {
                log.info("PRC Poller scheduled job is terminated for cloudId : {} with error : {}", (Object)prcPollTask.getPollerConfig().getCloudId(), (Object)ex.getMessage());
                this.schedulerService.unscheduleJob(jobRunnerRequest.getJobId());
                return JobRunnerResponse.aborted((String)ex.getMessage());
            }
            return JobRunnerResponse.failed((Throwable)ex);
        }
    }

    private JobId getJobId(String cloudId) {
        return JobId.of((String)(PRC_POLLER_JOB_ID + cloudId));
    }

    void createPollerIfNotExist(Long requestStartTime, PollerConfig pollerConfig) {
        String cloudId = pollerConfig.getCloudId();
        PollerTaskContext pollerTaskContext = (PollerTaskContext)cloudSitePollers.get(cloudId);
        Long existingJobStartTime = pollerTaskContext != null ? pollerTaskContext.getJobStartTime() : 0L;
        if (requestStartTime > existingJobStartTime) {
            PrcPollTask prcPollTask = new PrcPollTask(pollerConfig, new PrcServiceClient());
            prcPollTask.setToRunning();
            cloudSitePollers.put(cloudId, new PollerTaskContext(requestStartTime, prcPollTask));
        }
    }

    public PrcPollTask getExistingPrcPollTask(String cloudId) {
        return cloudSitePollers.get(cloudId) != null ? ((PollerTaskContext)cloudSitePollers.get(cloudId)).getPrcPollTask() : null;
    }
}

