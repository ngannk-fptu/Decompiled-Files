/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.event.CacheEntryEvent
 *  javax.cache.event.EventType
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.ICache;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.EventType;

public class CacheEntryEventImpl<K, V>
extends CacheEntryEvent<K, V> {
    private final K key;
    private final V newValue;
    private final V oldValue;

    public CacheEntryEventImpl(ICache<K, V> source, EventType eventType, K key, V newValue, V oldValue) {
        super(source, eventType);
        this.key = key;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    public V getOldValue() {
        return this.oldValue;
    }

    public boolean isOldValueAvailable() {
        return this.oldValue != null;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.newValue;
    }

    public <T> T unwrap(Class<T> clazz) {
        if (clazz.isAssignableFrom(((Object)((Object)this)).getClass())) {
            return clazz.cast((Object)this);
        }
        throw new IllegalArgumentException("Unwrapping to " + clazz + " is not supported by this implementation");
    }

    public String toString() {
        return "CacheEntryEventImpl{eventType=" + this.getEventType() + ", key=" + this.key + ", newValue=" + this.newValue + ", oldValue=" + this.oldValue + ", source=" + this.getSource() + '}';
    }
}

