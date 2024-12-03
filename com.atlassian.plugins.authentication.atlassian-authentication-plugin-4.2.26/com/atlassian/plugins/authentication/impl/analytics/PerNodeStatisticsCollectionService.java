/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.analytics;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.analytics.AbstractStatisticsCollectionService;
import com.atlassian.plugins.authentication.impl.analytics.NodeIdProvider;
import com.atlassian.plugins.authentication.impl.analytics.events.ConditionlessResponseProcessedStatusEvent;
import com.atlassian.plugins.authentication.impl.analytics.events.JitProvisionedUsersCountEvent;
import com.atlassian.plugins.authentication.impl.analytics.events.ResponseWithMultipleAuthnStatementStatusEvent;
import com.atlassian.plugins.authentication.impl.analytics.events.ResponseWithoutAuthnStatementStatusEvent;
import com.atlassian.plugins.authentication.impl.web.saml.TrackingCompatibilityModeResponseHandler;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.UserProvisionedEvent;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class PerNodeStatisticsCollectionService
extends AbstractStatisticsCollectionService {
    private static final Logger log = LoggerFactory.getLogger(PerNodeStatisticsCollectionService.class);
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)PerNodeStatisticsCollectionService.class.getCanonicalName());
    private static final JobId JOB_ID = JobId.of((String)"analytics-collection-local");
    private final AtomicLong jitUserProvisionedCount = new AtomicLong(0L);
    private final NodeIdProvider nodeIdProvider;
    private final TrackingCompatibilityModeResponseHandler conditionlessResponseHandler;

    @Inject
    public PerNodeStatisticsCollectionService(@ComponentImport EventPublisher eventPublisher, @ComponentImport SchedulerService schedulerService, NodeIdProvider nodeIdProvider, TrackingCompatibilityModeResponseHandler conditionlessResponseHandler) {
        super(eventPublisher, schedulerService);
        this.nodeIdProvider = nodeIdProvider;
        this.conditionlessResponseHandler = conditionlessResponseHandler;
    }

    @Override
    @PostConstruct
    public void register() throws SchedulerServiceException {
        super.register();
        this.eventPublisher.register((Object)this);
    }

    @Override
    @PreDestroy
    public void unregister() {
        super.unregister();
        this.eventPublisher.unregister((Object)this);
    }

    @Override
    @NotNull
    protected RunMode getRunMode() {
        return RunMode.RUN_LOCALLY;
    }

    @Override
    protected JobId getJobId() {
        return JOB_ID;
    }

    @Override
    protected JobRunnerKey getJobRunnerKey() {
        return JOB_RUNNER_KEY;
    }

    @EventListener
    public void onUserProvisionedEvent(UserProvisionedEvent event) {
        this.jitUserProvisionedCount.incrementAndGet();
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        log.debug("Collecting Local Authentication statistics");
        TrackingCompatibilityModeResponseHandler.CompatibilityModeResponseData compatibilityModeResponseData = this.conditionlessResponseHandler.getCompatibilityModeResponseData();
        Stream.of(new ConditionlessResponseProcessedStatusEvent(this.nodeIdProvider.getNodeId(), compatibilityModeResponseData), new ResponseWithoutAuthnStatementStatusEvent(this.nodeIdProvider.getNodeId(), compatibilityModeResponseData), new ResponseWithMultipleAuthnStatementStatusEvent(this.nodeIdProvider.getNodeId(), compatibilityModeResponseData), new JitProvisionedUsersCountEvent(this.nodeIdProvider.getNodeId(), this.jitUserProvisionedCount.getAndSet(0L))).forEach(this::tryPublish);
        return JobRunnerResponse.success();
    }
}

