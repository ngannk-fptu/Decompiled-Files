/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.impl.search;

import com.atlassian.confluence.event.events.permission.ContentTreePermissionReindexEvent;
import com.atlassian.confluence.search.ChangeIndexer;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class IndexerEventListener {
    private final ConfluenceIndexer indexer;
    private final ChangeIndexer changeIndexer;
    private final EventListenerRegistrar eventListenerRegistrar;

    IndexerEventListener(ConfluenceIndexer indexer, ChangeIndexer changeIndexer, EventListenerRegistrar eventListenerRegistrar) {
        this.indexer = indexer;
        this.changeIndexer = changeIndexer;
        this.eventListenerRegistrar = eventListenerRegistrar;
    }

    @PostConstruct
    void registerListeners() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    void unregisterListeners() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void onEvent(VisitorEvent event) {
        event.visit(this.indexer, this.changeIndexer);
    }

    @EventListener
    public void onContentTreePermissionReindexEvent(ContentTreePermissionReindexEvent event) {
        this.indexer.reIndex(event.getContent());
        this.changeIndexer.reIndexAllVersions(event.getContent());
    }

    public static interface VisitorEvent {
        public void visit(ConfluenceIndexer var1, ChangeIndexer var2);
    }
}

