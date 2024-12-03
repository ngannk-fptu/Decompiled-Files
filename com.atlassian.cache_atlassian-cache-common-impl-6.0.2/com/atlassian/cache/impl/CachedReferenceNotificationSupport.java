/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CachedReferenceEvent
 *  com.atlassian.cache.CachedReferenceListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.impl;

import com.atlassian.cache.CachedReferenceEvent;
import com.atlassian.cache.CachedReferenceListener;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachedReferenceNotificationSupport {
    private static final Logger log = LoggerFactory.getLogger(CachedReferenceNotificationSupport.class);
    private static final CachedReferenceNotificationSupport INSTANCE = new CachedReferenceNotificationSupport();

    public static CachedReferenceNotificationSupport getInstance() {
        return INSTANCE;
    }

    public <V> void notifyEvict(Iterable<CachedReferenceListener<V>> listeners, final CachedReferenceEvent<V> event) {
        this.notify(listeners, new Consumer<CachedReferenceListener<V>>(){

            @Override
            public void accept(CachedReferenceListener<V> listener) {
                listener.onEvict(event);
            }
        });
    }

    public <V> void notifySet(Iterable<CachedReferenceListener<V>> listeners, final CachedReferenceEvent<V> event) {
        this.notify(listeners, new Consumer<CachedReferenceListener<V>>(){

            @Override
            public void accept(CachedReferenceListener<V> listener) {
                listener.onSet(event);
            }
        });
    }

    public <V> void notifyReset(Iterable<CachedReferenceListener<V>> listeners, final CachedReferenceEvent<V> event) {
        this.notify(listeners, new Consumer<CachedReferenceListener<V>>(){

            @Override
            public void accept(CachedReferenceListener<V> listener) {
                listener.onReset(event);
            }
        });
    }

    private <V> void notify(Iterable<CachedReferenceListener<V>> listeners, Consumer<CachedReferenceListener<V>> effect) {
        for (CachedReferenceListener<V> listener : listeners) {
            try {
                effect.accept(listener);
            }
            catch (RuntimeException exc) {
                log.error("Exception while notifying listeners", (Throwable)exc);
            }
        }
    }
}

