/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind.entry;

public interface DelayedEntry<K, V> {
    public K getKey();

    public V getValue();

    public long getStoreTime();

    public int getPartitionId();

    public void setStoreTime(long var1);

    public void setSequence(long var1);

    public long getSequence();
}

