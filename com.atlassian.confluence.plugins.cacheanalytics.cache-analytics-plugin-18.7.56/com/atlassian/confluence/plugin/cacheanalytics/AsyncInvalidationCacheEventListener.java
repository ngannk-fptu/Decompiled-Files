/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.cache.hazelcast.AsyncInvalidationCacheFactory$CacheInvalidationOutOfSequenceEvent
 *  com.atlassian.confluence.cache.hazelcast.AsyncInvalidationCacheFactory$CacheSequenceSnapshotInconsistentEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  io.atlassian.util.concurrent.LazyReference
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.cacheanalytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.cache.hazelcast.AsyncInvalidationCacheFactory;
import com.atlassian.confluence.plugin.cacheanalytics.EventUtil;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import io.atlassian.util.concurrent.LazyReference;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AsyncInvalidationCacheEventListener {
    private static final Logger log = LoggerFactory.getLogger(AsyncInvalidationCacheEventListener.class);
    private final EventPublisher eventPublisher;
    private final LazyReference<?> listenerRef = new LazyReference<Object>(){

        protected Object create() {
            log.debug("Creating Listener instance");
            return new Listener();
        }
    };

    @Autowired
    public AsyncInvalidationCacheEventListener(@ComponentImport EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void registerListener() {
        log.debug("Registering listener");
        try {
            this.eventPublisher.register(this.listenerRef.get());
            log.debug("Registered listener OK");
        }
        catch (NoClassDefFoundError err) {
            log.debug("Event is not supported, skipping listener registration");
        }
    }

    @PreDestroy
    public void unregisterListener() {
        if (this.listenerRef.isInitialized()) {
            log.debug("Unregistering listener");
            this.eventPublisher.unregister(this.listenerRef.get());
        }
    }

    public static class AnalyticsEvent {
        private final String eventName;
        private final String cacheName;

        public AnalyticsEvent(String eventName, String cacheName) {
            this.eventName = eventName;
            this.cacheName = cacheName;
        }

        public String getCacheName() {
            return this.cacheName;
        }

        public int getCacheNameHash() {
            return EventUtil.simpleHash(this.cacheName);
        }

        @EventName
        public String getEventName() {
            return this.eventName;
        }
    }

    public class Listener {
        @EventListener
        public void onEvent(AsyncInvalidationCacheFactory.CacheInvalidationOutOfSequenceEvent event) {
            AsyncInvalidationCacheEventListener.this.eventPublisher.publish((Object)new AnalyticsEvent("confluence.cache.asyncInvalidation.cacheInvalidationOutOfSequence", event.getCacheName()));
        }

        @EventListener
        public void onEvent(AsyncInvalidationCacheFactory.CacheSequenceSnapshotInconsistentEvent event) {
            AsyncInvalidationCacheEventListener.this.eventPublisher.publish((Object)new AnalyticsEvent("confluence.cache.asyncInvalidation.sequenceSnapshotInconsistent", event.getCacheName()));
        }
    }
}

