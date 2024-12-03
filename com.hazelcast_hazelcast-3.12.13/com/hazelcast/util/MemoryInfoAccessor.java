/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

public interface MemoryInfoAccessor {
    public long getTotalMemory();

    public long getFreeMemory();

    public long getMaxMemory();
}

