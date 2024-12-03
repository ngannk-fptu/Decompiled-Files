/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  javax.annotation.Nonnull
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.inject.Inject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.analytics;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.analytics.events.AnalyticsEvent;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractStatisticsCollectionService
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(AbstractStatisticsCollectionService.class);
    private static final String COLLECTION_SCHEDULE = "0 0 23 * * ?";
    private static final String DEBUG_COLLECTION_SCHEDULE = "0/30 * * * * ?";
    public static final String DEBUG_ANALYTICS_SYSTEM_PROPERTY = "atlassian.authentication.debug.analytics";
    protected final SchedulerService schedulerService;
    protected final EventPublisher eventPublisher;

    @Inject
    public AbstractStatisticsCollectionService(@ComponentImport EventPublisher eventPublisher, @ComponentImport SchedulerService schedulerService) {
        this.eventPublisher = eventPublisher;
        this.schedulerService = schedulerService;
    }

    @PostConstruct
    public void register() throws SchedulerServiceException {
        try {
            Class.forName("com.atlassian.analytics.api.annotations.EventName");
        }
        catch (ClassNotFoundException e) {
            log.debug("No analytics api, not registering analytics collection");
            return;
        }
        this.schedulerService.registerJobRunner(this.getJobRunnerKey(), (JobRunner)this);
        String collectionSchedule = Boolean.getBoolean(DEBUG_ANALYTICS_SYSTEM_PROPERTY) ? DEBUG_COLLECTION_SCHEDULE : COLLECTION_SCHEDULE;
        this.schedulerService.scheduleJob(this.getJobId(), JobConfig.forJobRunnerKey((JobRunnerKey)this.getJobRunnerKey()).withSchedule(Schedule.forCronExpression((String)collectionSchedule)).withRunMode(this.getRunMode()));
        log.debug("Registered analytics collection job with schedule {}", (Object)collectionSchedule);
    }

    @PreDestroy
    public void unregister() {
        this.schedulerService.unregisterJobRunner(this.getJobRunnerKey());
        log.debug("Unregistered analytics collection job");
    }

    protected void tryPublish(AnalyticsEvent event) {
        try {
            if (event.shouldPublish()) {
                log.debug("Publishing {}", (Object)event.getClass().getSimpleName());
                this.eventPublisher.publish((Object)event);
            }
        }
        catch (Exception e) {
            log.info("Error collecting analytics data", (Throwable)e);
        }
    }

    @Nonnull
    protected abstract RunMode getRunMode();

    protected abstract JobId getJobId();

    protected abstract JobRunnerKey getJobRunnerKey();
}

