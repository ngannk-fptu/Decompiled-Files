/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind.entry;

import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntry;

class DeletedDelayedEntry<K, V>
implements DelayedEntry<K, V> {
    private final K key;
    private final int partitionId;
    private long storeTime;
    private long sequence;

    public DeletedDelayedEntry(K key, long storeTime, int partitionId) {
        this.key = key;
        this.storeTime = storeTime;
        this.partitionId = partitionId;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return null;
    }

    @Override
    public long getStoreTime() {
        return this.storeTime;
    }

    @Override
    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public void setStoreTime(long storeTime) {
        this.storeTime = storeTime;
    }

    @Override
    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    @Override
    public long getSequence() {
        return this.sequence;
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    public String toString() {
        return "DeletedDelayedEntry{key=" + this.key + ", partitionId=" + this.partitionId + ", storeTime=" + this.storeTime + ", sequence=" + this.sequence + '}';
    }
}

