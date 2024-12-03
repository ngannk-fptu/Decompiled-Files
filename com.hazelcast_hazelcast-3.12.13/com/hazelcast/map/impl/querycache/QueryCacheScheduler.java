/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache;

import java.util.concurrent.ScheduledFuture;

public interface QueryCacheScheduler {
    public void execute(Runnable var1);

    public ScheduledFuture<?> scheduleWithRepetition(Runnable var1, long var2);

    public void shutdown();
}

