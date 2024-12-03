/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.google.common.util.concurrent.MoreExecutors
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.util.concurrent.ThreadFactories;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class SingleJobExecutor<U> {
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 30L;
    private final Executor executor;
    private ReentrantLock lock;
    private CompletableFuture<U> future;

    SingleJobExecutor(String name) {
        this.executor = this.createSingleThreadExitingExecutor(name);
        this.lock = new ReentrantLock();
    }

    public CompletableFuture<U> execute(Supplier<U> supplier) {
        this.lock.lock();
        try {
            if (this.future == null) {
                this.future = CompletableFuture.supplyAsync(() -> {
                    this.lock.lock();
                    try {
                        Object t = supplier.get();
                        return t;
                    }
                    finally {
                        this.future = null;
                        this.lock.unlock();
                    }
                }, this.executor);
            }
        }
        finally {
            this.lock.unlock();
        }
        return this.future;
    }

    private ExecutorService createSingleThreadExitingExecutor(String name) {
        return MoreExecutors.getExitingExecutorService((ThreadPoolExecutor)new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), ThreadFactories.namedThreadFactory((String)name)), (long)30L, (TimeUnit)TimeUnit.SECONDS);
    }
}

