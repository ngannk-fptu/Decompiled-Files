/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.event.listeners;

import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.event.events.cluster.ClusterAccessModeEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.internal.accessmode.AccessModeManager;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterAccessModeEventListener {
    private static final Logger log = LoggerFactory.getLogger(ClusterAccessModeEventListener.class);
    private final AccessModeManager accessModeManager;

    public ClusterAccessModeEventListener(AccessModeManager accessModeManager) {
        this.accessModeManager = accessModeManager;
    }

    void handleAccessModeEvent(ClusterAccessModeEvent clusterReadOnlyModeEvent) {
        if (log.isDebugEnabled()) {
            log.debug("clusterReadOnlyModeEvent received: accessMode = {}", (Object)clusterReadOnlyModeEvent.getAccessMode().name());
        }
        try {
            AccessMode currentAccessMode = this.accessModeManager.getAccessMode();
            if (clusterReadOnlyModeEvent.getAccessMode().equals((Object)currentAccessMode)) {
                return;
            }
            this.accessModeManager.updateAccessMode(clusterReadOnlyModeEvent.getAccessMode());
        }
        catch (ConfigurationException e) {
            if (log.isDebugEnabled()) {
                log.debug("", (Throwable)e);
            }
            log.error("Error occurred while trying to update the access mode: {}", (Object)e.getMessage());
        }
    }

    @EventListener
    public void onRemoteEvent(ClusterEventWrapper wrapper) {
        Event event = wrapper.getEvent();
        if (event instanceof ClusterAccessModeEvent) {
            this.handleAccessModeEvent((ClusterAccessModeEvent)event);
        }
    }
}

