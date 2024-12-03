/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.CachedIntrospectionResults
 */
package com.atlassian.confluence.impl.spring;

import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.CachedIntrospectionResults;

public class CachedIntrospectionResultsFlusher {
    private final EventListenerRegistrar eventListenerRegistrar;

    public CachedIntrospectionResultsFlusher(EventListenerRegistrar eventListenerRegistrar) {
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
    public void onPluginFrameworkStartedEvent(PluginFrameworkStartedEvent event) {
        ClassLoader webAppClassLoader = this.getClass().getClassLoader();
        CachedIntrospectionResults.clearClassLoader((ClassLoader)webAppClassLoader);
        CachedIntrospectionResults.acceptClassLoader((ClassLoader)webAppClassLoader);
    }
}

