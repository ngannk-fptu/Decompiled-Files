/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheEntryListener
 *  com.google.common.base.Preconditions
 */
package com.atlassian.cache.impl;

import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.impl.CacheEntryListenerSupport;
import com.atlassian.cache.impl.CacheEntryNotificationSupport;
import com.atlassian.cache.impl.DefaultCacheEntryEvent;
import com.google.common.base.Preconditions;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class DefaultCacheEntryListenerSupport<K, V>
implements CacheEntryListenerSupport<K, V> {
    private final Set<CacheEntryListener<K, V>> valueListeners = new CopyOnWriteArraySet<CacheEntryListener<K, V>>();
    private final Set<CacheEntryListener<K, V>> valuelessListeners = new CopyOnWriteArraySet<CacheEntryListener<K, V>>();
    private final CacheEntryNotificationSupport notificationSupport = CacheEntryNotificationSupport.getInstance();

    @Override
    public void add(CacheEntryListener<K, V> listener, boolean includeValues) {
        (includeValues ? this.valueListeners : this.valuelessListeners).add((CacheEntryListener<K, V>)Preconditions.checkNotNull(listener));
    }

    @Override
    public void remove(CacheEntryListener<K, V> listener) {
        this.valueListeners.remove(Preconditions.checkNotNull(listener));
        this.valuelessListeners.remove(Preconditions.checkNotNull(listener));
    }

    @Override
    public void notifyAdd(K key, V value) {
        this.notificationSupport.notifyAdd(this.valueListeners, new DefaultCacheEntryEvent<K, Object>(key, value, null));
        this.notificationSupport.notifyAdd(this.valuelessListeners, new DefaultCacheEntryEvent(key));
    }

    @Override
    public void notifyEvict(K key, V oldValue) {
        this.notificationSupport.notifyEvict(this.valueListeners, new DefaultCacheEntryEvent<K, Object>(key, null, oldValue));
        this.notificationSupport.notifyEvict(this.valuelessListeners, new DefaultCacheEntryEvent(key));
    }

    @Override
    public void notifyRemove(K key, V oldValue) {
        this.notificationSupport.notifyRemove(this.valueListeners, new DefaultCacheEntryEvent<K, Object>(key, null, oldValue));
        this.notificationSupport.notifyRemove(this.valuelessListeners, new DefaultCacheEntryEvent(key));
    }

    @Override
    public void notifyUpdate(K key, V value, V oldValue) {
        this.notificationSupport.notifyUpdate(this.valueListeners, new DefaultCacheEntryEvent<K, V>(key, value, oldValue));
        this.notificationSupport.notifyUpdate(this.valuelessListeners, new DefaultCacheEntryEvent(key));
    }
}

