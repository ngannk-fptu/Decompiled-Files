/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction;

public interface Evictable<V> {
    public long getCreationTime();

    public long getLastAccessTime();

    public int getAccessHit();

    public V getValue();
}

