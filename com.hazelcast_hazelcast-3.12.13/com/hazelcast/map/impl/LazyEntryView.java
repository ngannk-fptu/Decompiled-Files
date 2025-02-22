/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.EntryView;
import com.hazelcast.map.merge.HigherHitsMapMergePolicy;
import com.hazelcast.map.merge.LatestUpdateMapMergePolicy;
import com.hazelcast.map.merge.MapMergePolicy;
import com.hazelcast.map.merge.PassThroughMergePolicy;
import com.hazelcast.map.merge.PutIfAbsentMapMergePolicy;
import com.hazelcast.spi.serialization.SerializationService;

class LazyEntryView<K, V>
implements EntryView<K, V> {
    private K key;
    private V value;
    private long cost;
    private long creationTime;
    private long expirationTime;
    private long hits;
    private long lastAccessTime;
    private long lastStoredTime;
    private long lastUpdateTime;
    private long version;
    private long ttl;
    private Long maxIdle;
    private SerializationService serializationService;
    private MapMergePolicy mergePolicy;

    public LazyEntryView() {
    }

    public LazyEntryView(K key, V value, SerializationService serializationService, MapMergePolicy mergePolicy) {
        this.value = value;
        this.key = key;
        this.serializationService = serializationService;
        this.mergePolicy = mergePolicy;
    }

    @Override
    public K getKey() {
        this.key = this.serializationService.toObject(this.key);
        return this.key;
    }

    public LazyEntryView<K, V> setKey(K key) {
        this.key = key;
        return this;
    }

    @Override
    public V getValue() {
        if (this.returnRawData(this.mergePolicy)) {
            return this.value;
        }
        this.value = this.serializationService.toObject(this.value);
        return this.value;
    }

    private boolean returnRawData(MapMergePolicy mergePolicy) {
        return mergePolicy instanceof PutIfAbsentMapMergePolicy || mergePolicy instanceof PassThroughMergePolicy || mergePolicy instanceof HigherHitsMapMergePolicy || mergePolicy instanceof LatestUpdateMapMergePolicy;
    }

    public LazyEntryView<K, V> setValue(V value) {
        this.value = value;
        return this;
    }

    @Override
    public long getCost() {
        return this.cost;
    }

    public LazyEntryView<K, V> setCost(long cost) {
        this.cost = cost;
        return this;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    public LazyEntryView<K, V> setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    @Override
    public long getExpirationTime() {
        return this.expirationTime;
    }

    public LazyEntryView<K, V> setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    @Override
    public long getHits() {
        return this.hits;
    }

    public LazyEntryView<K, V> setHits(long hits) {
        this.hits = hits;
        return this;
    }

    @Override
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public LazyEntryView<K, V> setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
        return this;
    }

    @Override
    public long getLastStoredTime() {
        return this.lastStoredTime;
    }

    public LazyEntryView<K, V> setLastStoredTime(long lastStoredTime) {
        this.lastStoredTime = lastStoredTime;
        return this;
    }

    @Override
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public LazyEntryView<K, V> setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
        return this;
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    public LazyEntryView<K, V> setVersion(long version) {
        this.version = version;
        return this;
    }

    public long getEvictionCriteriaNumber() {
        return 0L;
    }

    public LazyEntryView<K, V> setEvictionCriteriaNumber(long evictionCriteriaNumber) {
        return this;
    }

    @Override
    public long getTtl() {
        return this.ttl;
    }

    public LazyEntryView<K, V> setTtl(long ttl) {
        this.ttl = ttl;
        return this;
    }

    @Override
    public Long getMaxIdle() {
        return this.maxIdle;
    }

    public LazyEntryView<K, V> setMaxIdle(Long maxIdle) {
        this.maxIdle = maxIdle;
        return this;
    }
}

