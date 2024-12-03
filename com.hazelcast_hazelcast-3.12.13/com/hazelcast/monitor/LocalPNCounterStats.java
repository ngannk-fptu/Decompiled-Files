/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.monitor.LocalInstanceStats;

public interface LocalPNCounterStats
extends LocalInstanceStats {
    public long getValue();

    public long getTotalIncrementOperationCount();

    public long getTotalDecrementOperationCount();
}

