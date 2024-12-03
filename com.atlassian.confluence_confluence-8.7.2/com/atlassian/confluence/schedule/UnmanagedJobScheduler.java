/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  javax.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.schedule;

import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.confluence.schedule.ScheduledJob;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import java.util.Collection;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnmanagedJobScheduler {
    private static final Logger log = LoggerFactory.getLogger(UnmanagedJobScheduler.class);
    private final SchedulerService schedulerService;
    private final TimeZoneManager timeZoneManager;
    private final Collection<ScheduledJob> unmanagedJobs;
    private final EventListenerRegistrar eventListenerRegistrar;

    public UnmanagedJobScheduler(SchedulerService schedulerService, TimeZoneManager timeZoneManager, Collection<ScheduledJob> unmanagedJobs, EventListenerRegistrar eventListenerRegistrar) {
        this.schedulerService = schedulerService;
        this.timeZoneManager = timeZoneManager;
        this.unmanagedJobs = unmanagedJobs;
        this.eventListenerRegistrar = eventListenerRegistrar;
    }

    @PostConstruct
    public void listenApplicationStartedEvent() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @EventListener
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        this.eventListenerRegistrar.unregister((Object)this);
        TimeZone timeZone = this.timeZoneManager.getDefaultTimeZone();
        for (ScheduledJob job : this.unmanagedJobs) {
            this.schedulerService.registerJobRunner(job.getJobConfig().getJobRunnerKey(), job.getJobRunner());
            JobConfig jobConfig = job.getJobConfig();
            JobConfig jobConfigWithTimeZone = ScheduleUtil.withTimeZone(jobConfig, timeZone);
            try {
                this.schedulerService.scheduleJob(ScheduledJob.sameJobId(job), jobConfigWithTimeZone);
            }
            catch (SchedulerServiceException e) {
                log.error("Could not schedule ScheduledJob: " + job, (Throwable)e);
            }
        }
    }
}

