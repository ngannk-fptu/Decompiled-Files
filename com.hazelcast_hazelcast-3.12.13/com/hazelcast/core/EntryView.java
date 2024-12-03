/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

public interface EntryView<K, V> {
    public K getKey();

    public V getValue();

    public long getCost();

    public long getCreationTime();

    public long getExpirationTime();

    public long getHits();

    public long getLastAccessTime();

    public long getLastStoredTime();

    public long getLastUpdateTime();

    public long getVersion();

    public long getTtl();

    public Long getMaxIdle();
}

