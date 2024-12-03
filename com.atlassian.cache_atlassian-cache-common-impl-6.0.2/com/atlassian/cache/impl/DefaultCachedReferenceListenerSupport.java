/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CachedReferenceListener
 *  com.google.common.base.Preconditions
 */
package com.atlassian.cache.impl;

import com.atlassian.cache.CachedReferenceListener;
import com.atlassian.cache.impl.CachedReferenceListenerSupport;
import com.atlassian.cache.impl.CachedReferenceNotificationSupport;
import com.atlassian.cache.impl.DefaultCachedReferenceEvent;
import com.google.common.base.Preconditions;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class DefaultCachedReferenceListenerSupport<V>
implements CachedReferenceListenerSupport<V> {
    private final Set<CachedReferenceListener<V>> listeners = new CopyOnWriteArraySet<CachedReferenceListener<V>>();
    private final CachedReferenceNotificationSupport notificationSupport = CachedReferenceNotificationSupport.getInstance();

    @Override
    public void add(CachedReferenceListener<V> listener, boolean includeValues) {
        this.listeners.add((CachedReferenceListener<V>)Preconditions.checkNotNull(listener));
    }

    @Override
    public void remove(CachedReferenceListener<V> listener) {
        this.listeners.remove(Preconditions.checkNotNull(listener));
    }

    @Override
    public void notifyEvict(V oldValue) {
        this.notificationSupport.notifyEvict(this.listeners, new DefaultCachedReferenceEvent<V>(oldValue));
    }

    @Override
    public void notifySet(V value) {
        this.notificationSupport.notifySet(this.listeners, new DefaultCachedReferenceEvent<V>(value));
    }

    @Override
    public void notifyReset(V oldValue) {
        this.notificationSupport.notifyReset(this.listeners, new DefaultCachedReferenceEvent<V>(oldValue));
    }
}

