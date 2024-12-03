/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.executionservice;

import com.hazelcast.spi.ExecutionService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface InternalExecutionService
extends ExecutionService {
    public ExecutorService getDurable(String var1);

    public ExecutorService getScheduledDurable(String var1);

    public void executeDurable(String var1, Runnable var2);

    public ScheduledFuture<?> scheduleDurable(String var1, Runnable var2, long var3, TimeUnit var5);

    public <V> ScheduledFuture<Future<V>> scheduleDurable(String var1, Callable<V> var2, long var3, TimeUnit var5);

    public ScheduledFuture<?> scheduleDurableWithRepetition(String var1, Runnable var2, long var3, long var5, TimeUnit var7);

    public void shutdownDurableExecutor(String var1);

    public void shutdownScheduledDurableExecutor(String var1);
}

