/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.executor;

import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import java.util.concurrent.ExecutorService;

public interface ManagedExecutorService
extends ExecutorService {
    public String getName();

    @Probe
    public long getCompletedTaskCount();

    @Probe
    public int getMaximumPoolSize();

    @Probe
    public int getPoolSize();

    @Probe(level=ProbeLevel.MANDATORY)
    public int getQueueSize();

    @Probe
    public int getRemainingQueueCapacity();
}

