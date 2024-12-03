/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.record;

import com.hazelcast.util.Clock;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class ReplicatedRecord<K, V> {
    private static final AtomicLongFieldUpdater<ReplicatedRecord> HITS = AtomicLongFieldUpdater.newUpdater(ReplicatedRecord.class, "hits");
    private volatile long hits;
    private volatile long lastAccessTime = Clock.currentTimeMillis();
    private K key;
    private V value;
    private long ttlMillis;
    private volatile long updateTime = Clock.currentTimeMillis();
    private volatile long creationTime = Clock.currentTimeMillis();

    public ReplicatedRecord(K key, V value, long ttlMillis) {
        this.key = key;
        this.value = value;
        this.ttlMillis = ttlMillis;
    }

    public K getKey() {
        this.access();
        return this.getKeyInternal();
    }

    public K getKeyInternal() {
        return this.key;
    }

    public V getValue() {
        this.access();
        return this.getValueInternal();
    }

    public V getValueInternal() {
        return this.value;
    }

    public boolean isTombstone() {
        return this.value == null;
    }

    public long getTtlMillis() {
        return this.ttlMillis;
    }

    public V setValue(V value, long ttlMillis) {
        this.access();
        return this.setValueInternal(value, ttlMillis);
    }

    public V setValueInternal(V value, long ttlMillis) {
        V oldValue = this.value;
        this.value = value;
        this.updateTime = Clock.currentTimeMillis();
        this.ttlMillis = ttlMillis;
        return oldValue;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getHits() {
        return this.hits;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }

    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    private void access() {
        HITS.incrementAndGet(this);
        this.lastAccessTime = Clock.currentTimeMillis();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ReplicatedRecord that = (ReplicatedRecord)o;
        if (this.ttlMillis != that.ttlMillis) {
            return false;
        }
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        return !(this.value != null ? !this.value.equals(that.value) : that.value != null);
    }

    public int hashCode() {
        int result = this.key != null ? this.key.hashCode() : 0;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        result = 31 * result + (int)(this.ttlMillis ^ this.ttlMillis >>> 32);
        return result;
    }

    public String toString() {
        return "ReplicatedRecord{key=" + this.key + ", value=" + this.value + ", ttlMillis=" + this.ttlMillis + ", hits=" + HITS.get(this) + ", creationTime=" + this.creationTime + ", lastAccessTime=" + this.lastAccessTime + ", updateTime=" + this.updateTime + '}';
    }
}

