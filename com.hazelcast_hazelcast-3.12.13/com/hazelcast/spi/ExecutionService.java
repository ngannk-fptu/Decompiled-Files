/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.util.executor.ExecutorType;
import com.hazelcast.util.executor.ManagedExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public interface ExecutionService {
    public static final String SYSTEM_EXECUTOR = "hz:system";
    public static final String ASYNC_EXECUTOR = "hz:async";
    public static final String SCHEDULED_EXECUTOR = "hz:scheduled";
    public static final String CLIENT_EXECUTOR = "hz:client";
    public static final String CLIENT_QUERY_EXECUTOR = "hz:client-query";
    public static final String CLIENT_MANAGEMENT_EXECUTOR = "hz:client-management";
    public static final String CLIENT_BLOCKING_EXECUTOR = "hz:client-blocking-tasks";
    public static final String QUERY_EXECUTOR = "hz:query";
    public static final String IO_EXECUTOR = "hz:io";
    public static final String OFFLOADABLE_EXECUTOR = "hz:offloadable";
    public static final String MAP_LOADER_EXECUTOR = "hz:map-load";
    public static final String MAP_LOAD_ALL_KEYS_EXECUTOR = "hz:map-loadAllKeys";

    public ManagedExecutorService register(String var1, int var2, int var3, ExecutorType var4);

    public ManagedExecutorService register(String var1, int var2, int var3, ThreadFactory var4);

    public ManagedExecutorService getExecutor(String var1);

    public void shutdownExecutor(String var1);

    public void execute(String var1, Runnable var2);

    public Future<?> submit(String var1, Runnable var2);

    public <T> Future<T> submit(String var1, Callable<T> var2);

    public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4);

    public ScheduledFuture<?> schedule(String var1, Runnable var2, long var3, TimeUnit var5);

    public ScheduledFuture<?> scheduleWithRepetition(Runnable var1, long var2, long var4, TimeUnit var6);

    public ScheduledFuture<?> scheduleWithRepetition(String var1, Runnable var2, long var3, long var5, TimeUnit var7);

    public TaskScheduler getGlobalTaskScheduler();

    public TaskScheduler getTaskScheduler(String var1);

    public <V> ICompletableFuture<V> asCompletableFuture(Future<V> var1);
}

