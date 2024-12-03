/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction;

public interface EvictableEntryView<K, V> {
    public long getCreationTime();

    public long getLastAccessTime();

    public long getAccessHit();

    public K getKey();

    public V getValue();
}

