/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.scheduler.config.JobId
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.event.listeners;

import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.event.events.cluster.ClusterUpdateCronJobScheduleEvent;
import com.atlassian.confluence.event.events.cluster.ClusterUpdateSimpleJobScheduleEvent;
import com.atlassian.confluence.impl.schedule.managers.ScheduledJobNodeManager;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.scheduler.config.JobId;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterUpdateJobScheduleListener {
    private static final Logger log = LoggerFactory.getLogger(ClusterUpdateJobScheduleListener.class);
    private final ScheduledJobNodeManager scheduledJobNodeManager;

    public ClusterUpdateJobScheduleListener(ScheduledJobNodeManager scheduledJobNodeManager) {
        this.scheduledJobNodeManager = (ScheduledJobNodeManager)Preconditions.checkNotNull((Object)scheduledJobNodeManager);
    }

    public void onUpdateSimpleJobScheduleEvent(ClusterUpdateSimpleJobScheduleEvent event) {
        JobId jobId = JobId.of((String)event.getScheduledJobKey().getJobId());
        long repeatInterval = event.getNewRepeatInterval();
        log.debug("updating {} job interval to {}", (Object)jobId, (Object)repeatInterval);
        this.scheduledJobNodeManager.updateSimpleSchedule(jobId, repeatInterval);
    }

    public void onUpdateCronJobScheduleEvent(ClusterUpdateCronJobScheduleEvent event) {
        JobId jobId = JobId.of((String)event.getScheduledJobKey().getJobId());
        String cronSchedule = event.getNewCronSchedule();
        log.debug("updating {} job schedule to {}", (Object)jobId, (Object)cronSchedule);
        this.scheduledJobNodeManager.updateCronSchedule(jobId, cronSchedule);
    }

    @EventListener
    public void onRemoteEvent(ClusterEventWrapper wrapper) {
        Event event = wrapper.getEvent();
        if (event instanceof ClusterUpdateCronJobScheduleEvent) {
            this.onUpdateCronJobScheduleEvent((ClusterUpdateCronJobScheduleEvent)event);
        } else if (event instanceof ClusterUpdateSimpleJobScheduleEvent) {
            this.onUpdateSimpleJobScheduleEvent((ClusterUpdateSimpleJobScheduleEvent)event);
        }
    }
}

