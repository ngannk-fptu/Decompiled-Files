/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.record;

import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InternalReplicatedMapStorage<K, V> {
    private final ConcurrentMap<K, ReplicatedRecord<K, V>> storage = new ConcurrentHashMap<K, ReplicatedRecord<K, V>>(1000, 0.75f, 1);
    private long version;
    private boolean stale;

    public long getVersion() {
        return this.version;
    }

    public void syncVersion(long version) {
        this.stale = false;
        this.version = version;
    }

    public void setVersion(long version) {
        if (!this.stale) {
            this.stale = version != this.version + 1L;
        }
        this.version = version;
    }

    public long incrementVersion() {
        return this.version++;
    }

    public ReplicatedRecord<K, V> get(Object key) {
        return (ReplicatedRecord)this.storage.get(key);
    }

    public ReplicatedRecord<K, V> put(K key, ReplicatedRecord<K, V> replicatedRecord) {
        return this.storage.put(key, replicatedRecord);
    }

    public boolean remove(K key, ReplicatedRecord<K, V> replicatedRecord) {
        return this.storage.remove(key, replicatedRecord);
    }

    public boolean containsKey(Object key) {
        return this.storage.containsKey(key);
    }

    public Set<Map.Entry<K, ReplicatedRecord<K, V>>> entrySet() {
        return this.storage.entrySet();
    }

    public Collection<ReplicatedRecord<K, V>> values() {
        return this.storage.values();
    }

    public Set<K> keySet() {
        return this.storage.keySet();
    }

    public void clear() {
        this.storage.clear();
    }

    public boolean isEmpty() {
        return this.storage.isEmpty();
    }

    public int size() {
        int count = 0;
        for (ReplicatedRecord record : this.storage.values()) {
            if (record.isTombstone()) continue;
            ++count;
        }
        return count;
    }

    public boolean isStale(long version) {
        return this.stale || version > this.version;
    }
}

