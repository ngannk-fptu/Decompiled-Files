/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.monitor.LocalInstanceStats;

public interface LocalFlakeIdGeneratorStats
extends LocalInstanceStats {
    public long getBatchCount();

    public long getIdCount();
}

