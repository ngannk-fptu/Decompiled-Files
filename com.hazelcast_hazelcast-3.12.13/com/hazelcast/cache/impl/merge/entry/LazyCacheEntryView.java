/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.merge.entry;

import com.hazelcast.cache.CacheEntryView;
import com.hazelcast.spi.serialization.SerializationService;

public class LazyCacheEntryView<K, V>
implements CacheEntryView<K, V> {
    private Object key;
    private Object value;
    private Object expiryPolicy;
    private long creationTime;
    private long expirationTime;
    private long lastAccessTime;
    private long accessHit;
    private SerializationService serializationService;

    public LazyCacheEntryView(Object key, Object value, long creationTime, long expirationTime, long lastAccessTime, long accessHit, Object expiryPolicy) {
        this(key, value, creationTime, expirationTime, lastAccessTime, accessHit, expiryPolicy, null);
    }

    public LazyCacheEntryView(Object key, Object value, long creationTime, long expirationTime, long lastAccessTime, long accessHit, Object expiryPolicy, SerializationService serializationService) {
        this.key = key;
        this.value = value;
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
        this.lastAccessTime = lastAccessTime;
        this.accessHit = accessHit;
        this.expiryPolicy = expiryPolicy;
        this.serializationService = serializationService;
    }

    @Override
    public K getKey() {
        if (this.serializationService != null) {
            this.key = this.serializationService.toObject(this.key);
        }
        return (K)this.key;
    }

    @Override
    public V getValue() {
        if (this.serializationService != null) {
            this.value = this.serializationService.toObject(this.value);
        }
        return (V)this.value;
    }

    @Override
    public Object getExpiryPolicy() {
        if (this.serializationService != null) {
            this.expiryPolicy = this.serializationService.toObject(this.expiryPolicy);
        }
        return this.expiryPolicy;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public long getExpirationTime() {
        return this.expirationTime;
    }

    @Override
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    @Override
    public long getAccessHit() {
        return this.accessHit;
    }
}

