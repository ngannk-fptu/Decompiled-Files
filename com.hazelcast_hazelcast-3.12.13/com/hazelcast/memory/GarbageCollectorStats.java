/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.memory;

public interface GarbageCollectorStats {
    public long getMajorCollectionCount();

    public long getMajorCollectionTime();

    public long getMinorCollectionCount();

    public long getMinorCollectionTime();

    public long getUnknownCollectionCount();

    public long getUnknownCollectionTime();
}

