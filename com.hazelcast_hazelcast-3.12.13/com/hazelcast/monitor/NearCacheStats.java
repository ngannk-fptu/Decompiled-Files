/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.monitor.LocalInstanceStats;

public interface NearCacheStats
extends LocalInstanceStats {
    @Override
    public long getCreationTime();

    public long getOwnedEntryCount();

    public long getOwnedEntryMemoryCost();

    public long getHits();

    public long getMisses();

    public double getRatio();

    public long getEvictions();

    public long getExpirations();

    public long getInvalidations();

    public long getPersistenceCount();

    public long getLastPersistenceTime();

    public long getLastPersistenceDuration();

    public long getLastPersistenceWrittenBytes();

    public long getLastPersistenceKeyCount();

    public String getLastPersistenceFailure();
}

