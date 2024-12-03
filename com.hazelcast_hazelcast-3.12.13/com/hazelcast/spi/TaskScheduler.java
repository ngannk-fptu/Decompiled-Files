/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface TaskScheduler
extends Executor {
    public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4);

    public <V> ScheduledFuture<Future<V>> schedule(Callable<V> var1, long var2, TimeUnit var4);

    public ScheduledFuture<?> scheduleWithRepetition(Runnable var1, long var2, long var4, TimeUnit var6);
}

