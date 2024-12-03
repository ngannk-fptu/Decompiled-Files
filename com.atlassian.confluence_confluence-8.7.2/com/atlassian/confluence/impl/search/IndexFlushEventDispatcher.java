/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.impl.search;

import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.impl.search.DefaultIndexFlushRequester;
import com.atlassian.confluence.impl.search.IndexFlushScheduler;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class IndexFlushEventDispatcher {
    private final Map<SearchIndex, IndexFlushScheduler> indexFlushSchedulers;
    private final EventListenerRegistrar eventListenerRegistrar;

    public IndexFlushEventDispatcher(Map<SearchIndex, IndexFlushScheduler> indexFlushSchedulers, EventListenerRegistrar eventListenerRegistrar) {
        this.indexFlushSchedulers = indexFlushSchedulers;
        this.eventListenerRegistrar = eventListenerRegistrar;
    }

    @PostConstruct
    public void init() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void onIndexFlushRequested(DefaultIndexFlushRequester.RequestIndexFlushEvent event) {
        SearchIndex flushSearchIndexType = event.getAffectedSearchIndex();
        if (!this.indexFlushSchedulers.containsKey((Object)flushSearchIndexType)) {
            throw new IllegalArgumentException(String.format("Flushing the %s index queue is not supported", new Object[]{flushSearchIndexType}));
        }
        this.indexFlushSchedulers.get((Object)flushSearchIndexType).requestFlush();
    }

    @EventListener
    public void onIndexFlushRequested(ClusterEventWrapper wrapper) {
        Event event = wrapper.getEvent();
        if (event instanceof DefaultIndexFlushRequester.RequestIndexFlushEvent) {
            this.onIndexFlushRequested((DefaultIndexFlushRequester.RequestIndexFlushEvent)event);
        }
    }
}

