/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.executor;

import com.hazelcast.util.executor.ManagedExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NamedThreadPoolExecutor
extends ThreadPoolExecutor
implements ManagedExecutorService {
    private final String name;

    public NamedThreadPoolExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getQueueSize() {
        return this.getQueue().size();
    }

    @Override
    public int getRemainingQueueCapacity() {
        return this.getQueue().remainingCapacity();
    }

    @Override
    public String toString() {
        return "ThreadPoolExecutor{name='" + this.name + '\'' + '}';
    }
}

