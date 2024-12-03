/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.monitor.LocalInstanceStats;

public interface LocalExecutorStats
extends LocalInstanceStats {
    public long getPendingTaskCount();

    public long getStartedTaskCount();

    public long getCompletedTaskCount();

    public long getCancelledTaskCount();

    public long getTotalStartLatency();

    public long getTotalExecutionLatency();
}

