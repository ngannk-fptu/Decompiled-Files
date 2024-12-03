/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind.entry;

import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntry;

class AddedDelayedEntry<K, V>
implements DelayedEntry<K, V> {
    private final K key;
    private final V value;
    private final int partitionId;
    private long storeTime;
    private long sequence;

    public AddedDelayedEntry(K key, V value, long storeTime, int partitionId) {
        this.key = key;
        this.storeTime = storeTime;
        this.partitionId = partitionId;
        this.value = value;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
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
        return "AddedDelayedEntry{key=" + this.key + ", value=" + this.value + ", partitionId=" + this.partitionId + ", storeTime=" + this.storeTime + ", sequence=" + this.sequence + '}';
    }
}

