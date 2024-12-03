/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheEntryEvent
 *  com.atlassian.cache.CacheEntryListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.impl;

import com.atlassian.cache.CacheEntryEvent;
import com.atlassian.cache.CacheEntryListener;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheEntryNotificationSupport {
    private static final Logger log = LoggerFactory.getLogger(CacheEntryNotificationSupport.class);
    private static final CacheEntryNotificationSupport INSTANCE = new CacheEntryNotificationSupport();

    public static CacheEntryNotificationSupport getInstance() {
        return INSTANCE;
    }

    public <K, V> void notifyAdd(Iterable<CacheEntryListener<K, V>> listeners, CacheEntryEvent<K, V> event) {
        this.notify(listeners, listener -> listener.onAdd(event));
    }

    public <K, V> void notifyEvict(Iterable<CacheEntryListener<K, V>> listeners, CacheEntryEvent<K, V> event) {
        this.notify(listeners, listener -> listener.onEvict(event));
    }

    public <K, V> void notifyRemove(Iterable<CacheEntryListener<K, V>> listeners, CacheEntryEvent<K, V> event) {
        this.notify(listeners, listener -> listener.onRemove(event));
    }

    public <K, V> void notifyUpdate(Iterable<CacheEntryListener<K, V>> listeners, CacheEntryEvent<K, V> event) {
        this.notify(listeners, listener -> listener.onUpdate(event));
    }

    public <K, V> void notify(Iterable<CacheEntryListener<K, V>> listeners, Consumer<CacheEntryListener<K, V>> effect) {
        listeners.forEach(listener -> {
            try {
                effect.accept((CacheEntryListener)listener);
            }
            catch (RuntimeException exc) {
                log.error("Exception while notifying listeners", (Throwable)exc);
            }
        });
    }
}

