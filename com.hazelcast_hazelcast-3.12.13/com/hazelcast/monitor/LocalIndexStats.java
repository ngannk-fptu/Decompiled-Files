/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.monitor.LocalInstanceStats;

public interface LocalIndexStats
extends LocalInstanceStats {
    public long getQueryCount();

    public long getHitCount();

    public long getAverageHitLatency();

    public double getAverageHitSelectivity();

    public long getInsertCount();

    public long getTotalInsertLatency();

    public long getUpdateCount();

    public long getTotalUpdateLatency();

    public long getRemoveCount();

    public long getTotalRemoveLatency();

    public long getMemoryCost();
}

