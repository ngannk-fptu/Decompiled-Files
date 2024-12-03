/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.services.AnalyticsConfigService
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
 *  org.springframework.http.HttpStatus
 */
package com.atlassian.migration.agent.service.analytics;

import com.atlassian.analytics.api.services.AnalyticsConfigService;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.entity.AnalyticsEvent;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.okhttp.HttpServiceException;
import com.atlassian.migration.agent.service.analytics.AnalyticsSenderService;
import com.atlassian.migration.agent.service.analytics.MigrationAnalyticsEventRefusedException;
import com.atlassian.migration.agent.service.analytics.ProcessedAnalyticsEvents;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.cloud.LegalService;
import com.atlassian.migration.agent.store.impl.AnalyticsEventStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
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
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

@ParametersAreNonnullByDefault
public class AnalyticsEventConsumer
implements JobRunner {
    private static final JobRunnerKey RUNNER_KEY = JobRunnerKey.of((String)"migration-plugin:analytics-events-consumer-runner-key");
    private static final JobId JOB_ID = JobId.of((String)"migration-plugin:analytics-events-consumer-job-id");
    private static final Logger log = ContextLoggerFactory.getLogger(AnalyticsEventConsumer.class);
    private final PluginTransactionTemplate ptx;
    private final AnalyticsEventStore analyticsEventStore;
    private final SchedulerService schedulerService;
    private final AnalyticsSenderService analyticsSenderService;
    private final MigrationAgentConfiguration agentConfiguration;
    private final CloudSiteService cloudSiteService;
    private final LegalService legalService;
    private final Supplier<Instant> instantSupplier;
    private final AnalyticsConfigService analyticsConfigService;
    private Instant lastEventSubmission;

    public AnalyticsEventConsumer(PluginTransactionTemplate ptx, AnalyticsEventStore analyticsEventStore, SchedulerService schedulerService, AnalyticsSenderService analyticsSenderService, MigrationAgentConfiguration agentConfiguration, CloudSiteService cloudSiteService, LegalService legalService, AnalyticsConfigService analyticsConfigService) {
        this(ptx, analyticsEventStore, schedulerService, analyticsSenderService, agentConfiguration, cloudSiteService, legalService, Instant::now, analyticsConfigService);
    }

    @VisibleForTesting
    AnalyticsEventConsumer(PluginTransactionTemplate ptx, AnalyticsEventStore analyticsEventStore, SchedulerService schedulerService, AnalyticsSenderService analyticsSenderService, MigrationAgentConfiguration agentConfiguration, CloudSiteService cloudSiteService, LegalService legalService, Supplier<Instant> instantSupplier, AnalyticsConfigService analyticsConfigService) {
        this.ptx = ptx;
        this.analyticsEventStore = analyticsEventStore;
        this.schedulerService = schedulerService;
        this.analyticsSenderService = analyticsSenderService;
        this.agentConfiguration = agentConfiguration;
        this.cloudSiteService = cloudSiteService;
        this.legalService = legalService;
        this.instantSupplier = instantSupplier;
        this.analyticsConfigService = analyticsConfigService;
        this.setLastEventSubmission(instantSupplier.get());
    }

    @PostConstruct
    public void postConstruct() throws SchedulerServiceException {
        if (this.agentConfiguration.isAnalyticsSenderDisabled()) {
            this.schedulerService.unscheduleJob(JOB_ID);
            log.warn("AnalyticsEventConsumer poller is disabled. Job {} is removed.", (Object)JOB_ID);
        } else {
            this.schedulerService.registerJobRunner(RUNNER_KEY, (JobRunner)this);
            log.info("Successfully registered AnalyticsEventConsumer job {}.", (Object)RUNNER_KEY);
            this.schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.forInterval((long)this.jobInterval(), (Date)Date.from(this.instantSupplier.get().plus(Duration.ofSeconds(10L))))));
            log.info("Successfully started AnalyticsEventConsumer poller.");
        }
    }

    @PreDestroy
    public void cleanup() {
        this.schedulerService.unregisterJobRunner(RUNNER_KEY);
    }

    public JobRunnerResponse runJob(JobRunnerRequest req) {
        return this.runJob();
    }

    public JobRunnerResponse runJob() {
        if (!this.legalService.getRememberLegalOptIn() && !this.analyticsConfigService.canCollectAnalytics()) {
            return JobRunnerResponse.success((String)"User hasn't agreed with sending analytics");
        }
        if (this.noEventsToBeSent()) {
            return JobRunnerResponse.success((String)"Waiting a bit longer before sending analytics events");
        }
        return this.submitEvents();
    }

    public void triggerJobAndDeleteRemainingEvents() {
        this.runJob();
        this.ptx.write(this.analyticsEventStore::deleteAllEvents);
    }

    private boolean noEventsToBeSent() {
        return this.ptx.read(this.analyticsEventStore::countAnalyticsEvents) < (long)this.getConfiguredBatchSize() && this.getLastEventSubmission().plus(Duration.ofMinutes(this.maxWait())).isAfter(this.instantSupplier.get());
    }

    private JobRunnerResponse submitEvents() {
        Optional<String> token = this.cloudSiteService.getNonFailingToken();
        if (!token.isPresent()) {
            return JobRunnerResponse.success((String)"No container tokens found. The user has to link a cloud site before we start sending analytics events");
        }
        this.setLastEventSubmission(this.instantSupplier.get());
        try {
            boolean eventsPending;
            while (eventsPending = this.ptx.write(() -> this.sendBatchOfEvents((String)token.get())).booleanValue()) {
            }
            return JobRunnerResponse.success();
        }
        catch (HttpServiceException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                return this.tokenError(token.get());
            }
            return this.error(e);
        }
        catch (MigrationAnalyticsEventRefusedException e) {
            return JobRunnerResponse.success((String)e.getMessage());
        }
        catch (RuntimeException e) {
            return this.error(e);
        }
    }

    private JobRunnerResponse tokenError(String token) {
        log.warn("Token refused by Stargate. Marking cloud site as failing.");
        this.cloudSiteService.markTokenAsFailed(token);
        return JobRunnerResponse.failed((String)"Failed to authorise service. Marking cloud site as failing");
    }

    private boolean sendBatchOfEvents(String token) {
        int configuredBatchSize = this.getConfiguredBatchSize();
        List<AnalyticsEvent> batch = this.analyticsEventStore.pullAnalyticsEvents(configuredBatchSize);
        if (CollectionUtils.isEmpty(batch)) {
            return false;
        }
        ProcessedAnalyticsEvents sentAnalyticsEvents = this.analyticsSenderService.processAndSendAnalyticsEvents(token, batch);
        List<AnalyticsEvent> successfullySentEvents = sentAnalyticsEvents.getSuccessfullySentEvents();
        List<AnalyticsEvent> unsuccessfullySentEvents = sentAnalyticsEvents.getUnsuccessfullySentEvents();
        if (!successfullySentEvents.isEmpty()) {
            this.analyticsEventStore.deleteAnalyticsEvents(successfullySentEvents);
        }
        if (!unsuccessfullySentEvents.isEmpty()) {
            throw new MigrationAnalyticsEventRefusedException("Migration analytics actively refused events. They'll be locally preserved for a future retry.");
        }
        return batch.size() == configuredBatchSize;
    }

    private JobRunnerResponse error(RuntimeException e) {
        log.error("An unhandled exception occurred when processing a AnalyticsEventConsumer job request. Reason: {}", (Object)e.getMessage(), (Object)e);
        return JobRunnerResponse.failed((String)("AnalyticsEventConsumer job failed with reason " + e.getMessage()));
    }

    private int maxWait() {
        return this.agentConfiguration.getAnalyticsSenderMaxWaitInMinutes();
    }

    private int getConfiguredBatchSize() {
        return this.agentConfiguration.getAnalyticsSenderBatchSize();
    }

    private long jobInterval() {
        return Duration.ofSeconds(this.agentConfiguration.getAnalyticsSenderJobIntervalInSeconds()).toMillis();
    }

    private synchronized Instant getLastEventSubmission() {
        return this.lastEventSubmission;
    }

    private synchronized void setLastEventSubmission(Instant lastEventSubmission) {
        this.lastEventSubmission = lastEventSubmission;
    }
}

