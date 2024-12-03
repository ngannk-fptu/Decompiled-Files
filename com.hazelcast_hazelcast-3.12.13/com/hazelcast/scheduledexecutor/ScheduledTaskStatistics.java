/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor;

import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.util.concurrent.TimeUnit;

public interface ScheduledTaskStatistics
extends IdentifiedDataSerializable {
    public long getTotalRuns();

    public long getLastRunDuration(TimeUnit var1);

    public long getLastIdleTime(TimeUnit var1);

    public long getTotalRunTime(TimeUnit var1);

    public long getTotalIdleTime(TimeUnit var1);
}

