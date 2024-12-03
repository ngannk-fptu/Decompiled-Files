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

import com.atlassian.confluence.event.events.cluster.ClusterDisableJobEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEnableJobEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.impl.schedule.managers.ScheduledJobNodeManager;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.scheduler.config.JobId;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterChangeJobStatusListener {
    private static final Logger log = LoggerFactory.getLogger(ClusterChangeJobStatusListener.class);
    private final ScheduledJobNodeManager scheduledJobNodeManager;

    public ClusterChangeJobStatusListener(ScheduledJobNodeManager scheduledJobNodeManager) {
        this.scheduledJobNodeManager = (ScheduledJobNodeManager)Preconditions.checkNotNull((Object)scheduledJobNodeManager);
    }

    public void onClusterDisableJobEvent(ClusterDisableJobEvent event) {
        JobId jobId = JobId.of((String)event.getScheduledJobKey().getJobId());
        log.debug("disabling {} across all nodes", (Object)jobId);
        this.scheduledJobNodeManager.disableJob(jobId);
    }

    public void onClusterEnableJobEvent(ClusterEnableJobEvent event) {
        JobId jobId = JobId.of((String)event.getScheduledJobKey().getJobId());
        log.debug("enabling {} across all nodes", (Object)jobId);
        this.scheduledJobNodeManager.enableJob(jobId);
    }

    @EventListener
    public void onRemoteEvent(ClusterEventWrapper wrapper) {
        Event event = wrapper.getEvent();
        if (event instanceof ClusterDisableJobEvent) {
            this.onClusterDisableJobEvent((ClusterDisableJobEvent)event);
        } else if (event instanceof ClusterEnableJobEvent) {
            this.onClusterEnableJobEvent((ClusterEnableJobEvent)event);
        }
    }
}

