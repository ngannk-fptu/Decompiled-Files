/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.failurecache.executor;

import com.google.common.collect.ImmutableList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public interface DaemonExecutorService
extends ScheduledExecutorService {
    public static final int THREAD_POOL_SIZE = Integer.getInteger("failure.cache.threadpool", 2);
    public static final int THREAD_POOL_PRIORITY = Integer.getInteger("failure.cache.priority", 5);
    public static final int DEFAULT_TIMEOUT_MS = Integer.getInteger("failure.cache.timeout", 5000);

    public <O, I extends Iterable<O>> ImmutableList<O> invokeAllAndGet(Iterable<? extends Callable<I>> var1, long var2, TimeUnit var4) throws ExecutionException, InterruptedException;
}

