/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapperService;
import com.atlassian.event.Event;
import com.atlassian.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterEventBroadcaster
implements EventListener {
    private static final Logger log = LoggerFactory.getLogger(ClusterEventBroadcaster.class);
    private ClusterManager clusterManager;
    private ClusterEventWrapperService clusterEventWrapperService;

    public void handleEvent(Event event) {
        if (!this.clusterManager.isClustered() || !(event instanceof ClusterEvent)) {
            return;
        }
        try {
            ConfluenceEvent wrappedEvent = this.clusterEventWrapperService.wrap(this.clusterManager.getThisNodeInformation(), event);
            this.clusterManager.publishEvent(wrappedEvent);
        }
        catch (IllegalStateException ise) {
            log.error("Error sending event", (Throwable)ise);
        }
    }

    public Class[] getHandledEventClasses() {
        return new Class[]{ClusterEvent.class};
    }

    public void setClusterManager(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    public void setClusterEventWrapperService(ClusterEventWrapperService clusterEventWrapperService) {
        this.clusterEventWrapperService = clusterEventWrapperService;
    }
}

