/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.scheduler.core.LifecycleAwareSchedulerService
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.safety;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.safety.ClusterPanicEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.scheduler.core.LifecycleAwareSchedulerService;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterPanicListener {
    private static final Logger log = LoggerFactory.getLogger(ClusterPanicListener.class);
    private final ClusterManager clusterManager;
    private final LifecycleAwareSchedulerService schedulerService;

    public ClusterPanicListener(ClusterManager clusterManager, LifecycleAwareSchedulerService schedulerService) {
        this.clusterManager = (ClusterManager)Preconditions.checkNotNull((Object)clusterManager);
        this.schedulerService = (LifecycleAwareSchedulerService)Preconditions.checkNotNull((Object)schedulerService);
    }

    @EventListener
    public void onClusterPanicEvent(ClusterPanicEvent event) {
        log.error("Received a panic event, stopping processing on the node: {}", (Object)event.getDescription());
        if (this.clusterManager.isClustered()) {
            log.warn(this.clusterManager.getClusterInformation().toString());
        }
        JohnsonUtils.raiseJohnsonEvent(JohnsonEventType.CLUSTER, event.getDescription(), null, JohnsonEventLevel.FATAL);
        log.warn("Shutting down scheduler");
        try {
            this.schedulerService.shutdown();
        }
        catch (Exception e) {
            log.error("Error shutting down atlassian-scheduler. Database consistency may be at risk. Shut down Confluence as soon as possible and fix above clustering errors.", (Throwable)e);
        }
    }

    @EventListener
    public void onRemoteEvent(ClusterEventWrapper wrapper) {
        Event event = wrapper.getEvent();
        if (event instanceof ClusterPanicEvent) {
            this.onClusterPanicEvent((ClusterPanicEvent)event);
        }
    }
}

