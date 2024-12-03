/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cmpt.analytics.events.def.DetectedUserEmailOperationalEvent
 *  com.atlassian.cmpt.analytics.events.def.DetectedUserEmailOperationalEvent$Builder
 *  com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator
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
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.UnmodifiableIterator
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  lombok.Generated
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cmpt.analytics.events.def.DetectedUserEmailOperationalEvent;
import com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.logging.LoggingContextBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.migration.agent.service.impl.UserService;
import com.atlassian.migration.agent.store.impl.DetectedEmailEventLogStore;
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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Generated;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DetectedUserEmailAnalyticsService
implements JobRunner {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DetectedUserEmailAnalyticsService.class);
    private static final String JOB_RUNNER_ID = "migration-plugin:detected-emails-runner";
    private static final String CLOUD_ID = "cloudId";
    private final UserService userService;
    private final SchedulerService schedulerService;
    private final AnalyticsEventService analyticsEventService;
    private final SENSupplier senSupplier;
    private final CloudSiteService cloudSiteService;
    private final DetectedEmailEventLogStore eventLogStore;
    private final PluginTransactionTemplate ptx;
    private final MigrationAgentConfiguration migrationAgentConfiguration;
    private final Supplier<Instant> nowSupplier;

    public DetectedUserEmailAnalyticsService(UserService userService, SchedulerService schedulerService, AnalyticsEventService analyticsEventService, SENSupplier senSupplier, CloudSiteService cloudSiteService, DetectedEmailEventLogStore eventLogStore, PluginTransactionTemplate ptx, MigrationAgentConfiguration migrationAgentConfiguration) {
        this(userService, schedulerService, analyticsEventService, senSupplier, cloudSiteService, eventLogStore, ptx, migrationAgentConfiguration, Instant::now);
    }

    @VisibleForTesting
    DetectedUserEmailAnalyticsService(UserService userService, SchedulerService schedulerService, AnalyticsEventService analyticsEventService, SENSupplier senSupplier, CloudSiteService cloudSiteService, DetectedEmailEventLogStore eventLogStore, PluginTransactionTemplate ptx, MigrationAgentConfiguration migrationAgentConfiguration, Supplier<Instant> nowSupplier) {
        this.userService = userService;
        this.schedulerService = schedulerService;
        this.analyticsEventService = analyticsEventService;
        this.senSupplier = senSupplier;
        this.cloudSiteService = cloudSiteService;
        this.eventLogStore = eventLogStore;
        this.ptx = ptx;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
        this.nowSupplier = nowSupplier;
    }

    @PostConstruct
    public void postConstruct() {
        this.schedulerService.registerJobRunner(JobRunnerKey.of((String)JOB_RUNNER_ID), (JobRunner)this);
    }

    @PreDestroy
    public void cleanup() {
        this.schedulerService.unregisterJobRunner(JobRunnerKey.of((String)JOB_RUNNER_ID));
    }

    public void triggerForCloudId(String cloudId) {
        this.trigger(Optional.of(cloudId));
    }

    public void triggerForAllCloudIds() {
        this.trigger(Optional.empty());
    }

    private void trigger(Optional<String> targetCloudId) {
        ImmutableMap params = targetCloudId.isPresent() ? ImmutableMap.of((Object)CLOUD_ID, (Object)targetCloudId.get()) : Collections.emptyMap();
        JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)JobRunnerKey.of((String)JOB_RUNNER_ID)).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.runOnce(null)).withParameters((Map)params);
        try {
            JobId jobId = this.schedulerService.scheduleJobWithGeneratedId(jobConfig);
            this.logCtxForJob(jobId).execute(() -> log.info("Detected user emails events collector job is scheduled for cloudIds = {}.", (Object)targetCloudId));
        }
        catch (SchedulerServiceException e) {
            throw new RuntimeException(String.format("Failed to schedule job for runner %s and cloudIds = %s.", JOB_RUNNER_ID, targetCloudId), e);
        }
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        JobId jobId = request.getJobId();
        return this.logCtxForJob(jobId).execute(() -> {
            long start = System.currentTimeMillis();
            Set<String> targetCloudIds = this.resolveTargetCloudIds(request);
            log.info("Start job to collect detected emails analytics events for cloud ids = {}.", targetCloudIds);
            Ctx ctx = new Ctx(this.nowSupplier.get().toEpochMilli(), targetCloudIds);
            this.collectAndSendEvents(ctx);
            long elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start);
            log.info("Finished job to collect detected emails analytics events in {} seconds.", (Object)elapsedSeconds);
            return JobRunnerResponse.success();
        });
    }

    private void collectAndSendEvents(Ctx ctx) {
        if (CollectionUtils.isEmpty(ctx.cloudIds)) {
            log.warn("There are no cloud sites linked. Skip job.");
            return;
        }
        UnmodifiableIterator it = Iterators.partition(this.userService.getAllUsers().iterator(), (int)this.migrationAgentConfiguration.getDetectedUserEmailAnalyticsEventUsersBatchSize());
        while (it.hasNext()) {
            this.processBatchOfUsers((List)it.next(), ctx);
        }
    }

    private void processBatchOfUsers(List<MigrationUser> users, Ctx ctx) {
        List<MigrationUser> migUsers = users.stream().filter(u -> StringUtils.isNotEmpty((CharSequence)u.getEmail())).filter(u -> IdentityAcceptedEmailValidator.isValid((String)u.getEmail())).collect(Collectors.toList());
        HashMap uniqUsers = new HashMap();
        migUsers.forEach(u -> uniqUsers.compute(u.getEmail(), (key, oldValue) -> oldValue == null || u.isActive() ? u : oldValue));
        log.info("About to create detected email events for {} cloud sites. Original users batch size = {}. Users count after filtering by invalid emails = {}. Users count with unique email address = {}.", new Object[]{ctx.cloudIds.size(), users.size(), migUsers.size(), uniqUsers.size()});
        if (!uniqUsers.isEmpty()) {
            ctx.cloudIds.forEach(cloudId -> this.createAndSaveEvents(uniqUsers, (String)cloudId, ctx));
        }
    }

    private void createAndSaveEvents(Map<String, MigrationUser> uniqUsersByEmail, String cloudId, Ctx ctx) {
        LoggingContextBuilder.logCtx().withAttribute(CLOUD_ID, cloudId).execute(() -> {
            log.info("Creating email detected events for {} users.", (Object)uniqUsersByEmail.size());
            Set notTrackedEmails = this.ptx.read(() -> this.eventLogStore.findEmailsWhichAreNotTracked(cloudId, uniqUsersByEmail.keySet()));
            List events = uniqUsersByEmail.values().stream().filter(u -> notTrackedEmails.contains(u.getEmail())).map(u -> this.createEvent((MigrationUser)u, cloudId, ctx)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
            this.analyticsEventService.saveAnalyticsEvents(() -> events);
            int countOfEmailsAlreadyTracked = uniqUsersByEmail.size() - notTrackedEmails.size();
            log.info("{} email detected events created. Count of email already tracked = {}.", (Object)events.size(), (Object)countOfEmailsAlreadyTracked);
            this.ptx.write(() -> notTrackedEmails.forEach(email -> this.eventLogStore.track((String)email, cloudId)));
        });
    }

    private Optional<DetectedUserEmailOperationalEvent> createEvent(MigrationUser user, String cloudId, Ctx ctx) {
        DetectedUserEmailOperationalEvent event = ((DetectedUserEmailOperationalEvent.Builder)((DetectedUserEmailOperationalEvent.Builder)new DetectedUserEmailOperationalEvent.Builder(ctx.time, user.getEmail()).sen(this.senSupplier.get())).userStatus(user.isActive()).cloudId(cloudId)).build();
        if (StringUtils.isEmpty((CharSequence)event.actionSubjectId)) {
            log.debug("Hashed email for user {} is empty. Skip.", (Object)user);
            return Optional.empty();
        }
        return Optional.of(event);
    }

    private Set<String> resolveTargetCloudIds(JobRunnerRequest request) {
        Optional<String> maybeCloudIdFromJob = this.getCloudIdFromJobParams(request.getJobConfig().getParameters());
        if (maybeCloudIdFromJob.isPresent()) {
            return ImmutableSet.of((Object)maybeCloudIdFromJob.get());
        }
        return this.cloudSiteService.getAllSites().stream().map(CloudSite::getCloudId).collect(Collectors.toSet());
    }

    private Optional<String> getCloudIdFromJobParams(Map<String, Serializable> jobParams) {
        return jobParams.containsKey(CLOUD_ID) ? Optional.of((String)((Object)jobParams.get(CLOUD_ID))) : Optional.empty();
    }

    private LoggingContextBuilder logCtxForJob(JobId jobId) {
        return LoggingContextBuilder.logCtx().withAttribute("jobRunnerId", JOB_RUNNER_ID).withAttribute("jobId", jobId.toString());
    }

    private static class Ctx {
        final long time;
        final Set<String> cloudIds;

        private Ctx(long time, Set<String> cloudIds) {
            this.time = time;
            this.cloudIds = cloudIds;
        }
    }
}

