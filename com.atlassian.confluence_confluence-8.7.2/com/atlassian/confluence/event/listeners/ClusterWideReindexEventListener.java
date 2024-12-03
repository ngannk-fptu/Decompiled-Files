/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.event.listeners;

import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.event.events.cluster.ClusterReindexRequiredEvent;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.event.api.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClusterWideReindexEventListener {
    private static final Logger log = LoggerFactory.getLogger(ClusterWideReindexEventListener.class);
    private final IndexManager indexManager;

    public ClusterWideReindexEventListener(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    @EventListener
    public void handleClusterEventWrapper(ClusterEventWrapper clusterEventWrapper) {
        if (clusterEventWrapper.getEvent() instanceof ClusterReindexRequiredEvent) {
            log.info("Full reindex requested by event.");
            this.indexManager.reIndex();
        }
    }
}

