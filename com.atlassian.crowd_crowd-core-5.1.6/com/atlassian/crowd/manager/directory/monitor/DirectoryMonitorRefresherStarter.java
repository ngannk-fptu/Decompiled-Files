/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.event.application.ApplicationReadyEvent
 *  com.atlassian.crowd.event.directory.DirectoryCreatedEvent
 *  com.atlassian.crowd.event.directory.DirectoryDeletedEvent
 *  com.atlassian.crowd.event.directory.DirectoryUpdatedEvent
 *  com.atlassian.crowd.event.migration.XMLRestoreFinishedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.google.common.base.Preconditions
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.directory.monitor;

import com.atlassian.crowd.event.application.ApplicationReadyEvent;
import com.atlassian.crowd.event.directory.DirectoryCreatedEvent;
import com.atlassian.crowd.event.directory.DirectoryDeletedEvent;
import com.atlassian.crowd.event.directory.DirectoryUpdatedEvent;
import com.atlassian.crowd.event.migration.XMLRestoreFinishedEvent;
import com.atlassian.crowd.manager.directory.monitor.DirectoryMonitorRefresherJob;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.google.common.base.Preconditions;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DirectoryMonitorRefresherStarter {
    static final JobId JOB_ID = JobId.of((String)(DirectoryMonitorRefresherStarter.class.getName() + "-job"));
    private static final Logger log = LoggerFactory.getLogger(DirectoryMonitorRefresherStarter.class);
    private final EventPublisher eventPublisher;
    private final SchedulerService schedulerService;
    private final long refresherJobIntervalMillis;

    public DirectoryMonitorRefresherStarter(EventPublisher eventPublisher, SchedulerService schedulerService, long refresherJobIntervalMillis) {
        this.eventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)eventPublisher);
        this.schedulerService = schedulerService;
        this.refresherJobIntervalMillis = refresherJobIntervalMillis;
    }

    @PostConstruct
    public void registerListener() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void unregisterListener() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        this.triggerDirectoryMonitoringJob(event);
    }

    @EventListener
    public void onXMLRestore(XMLRestoreFinishedEvent event) {
        this.triggerDirectoryMonitoringJob(event);
    }

    @EventListener
    public void handleEvent(DirectoryUpdatedEvent event) {
        this.triggerDirectoryMonitoringJob(event);
    }

    @EventListener
    public void handleEvent(DirectoryDeletedEvent event) {
        this.triggerDirectoryMonitoringJob(event);
    }

    @EventListener
    public void handleEvent(DirectoryCreatedEvent event) {
        this.triggerDirectoryMonitoringJob(event);
    }

    private void triggerDirectoryMonitoringJob(Object cause) {
        try {
            log.debug("Rescheduling directory monitoring job due to {}", cause);
            this.schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)DirectoryMonitorRefresherJob.JOB_RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.forInterval((long)this.refresherJobIntervalMillis, null)));
        }
        catch (SchedulerServiceException e) {
            log.warn("Failed to reschedule directory monitoring job", (Throwable)e);
        }
    }
}

