/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.event.application.ApplicationReadyEvent
 *  com.atlassian.crowd.service.cluster.ClusterService
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.event.application.ApplicationReadyEvent;
import com.atlassian.crowd.manager.directory.FailedSynchronisationManager;
import com.atlassian.crowd.service.cluster.ClusterService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronisationStatusFinalizer {
    private static final Logger log = LoggerFactory.getLogger(SynchronisationStatusFinalizer.class);
    private final EventListenerRegistrar eventListenerRegistrar;
    private final FailedSynchronisationManager failedSynchronisationManager;
    private final ClusterService clusterService;

    public SynchronisationStatusFinalizer(EventListenerRegistrar eventListenerRegistrar, FailedSynchronisationManager failedSynchronisationManager, ClusterService clusterService) {
        this.eventListenerRegistrar = eventListenerRegistrar;
        this.failedSynchronisationManager = failedSynchronisationManager;
        this.clusterService = clusterService;
    }

    @PostConstruct
    public void register() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    public void unregister() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void handleEvent(ApplicationReadyEvent applicationReadyEvent) {
        if (this.clusterService.isAvailable()) {
            log.debug("Not updating synchronisation status on startup in clustered configuration");
            return;
        }
        this.failedSynchronisationManager.finalizeSynchronisationStatuses();
    }
}

