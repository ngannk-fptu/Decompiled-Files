/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind.entry;

import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntry;

class NullValueDelayedEntry<K, V>
implements DelayedEntry<K, V> {
    private final K key;

    public NullValueDelayedEntry(K key) {
        this.key = key;
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
        return -1L;
    }

    @Override
    public int getPartitionId() {
        return -1;
    }

    @Override
    public void setStoreTime(long storeTime) {
    }

    @Override
    public void setSequence(long sequence) {
    }

    @Override
    public long getSequence() {
        return -1L;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NullValueDelayedEntry)) {
            return false;
        }
        NullValueDelayedEntry that = (NullValueDelayedEntry)o;
        return !(this.key == null ? that.key != null : !this.key.equals(that.key));
    }

    public int hashCode() {
        return this.key != null ? this.key.hashCode() : 0;
    }
}

