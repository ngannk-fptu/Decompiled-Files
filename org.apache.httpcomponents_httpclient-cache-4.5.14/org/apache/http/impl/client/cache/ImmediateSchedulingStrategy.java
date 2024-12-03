/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.client.cache;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.impl.client.cache.AsynchronousValidationRequest;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.SchedulingStrategy;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.SAFE)
public class ImmediateSchedulingStrategy
implements SchedulingStrategy {
    private final ExecutorService executor;

    public ImmediateSchedulingStrategy(CacheConfig cacheConfig) {
        this(new ThreadPoolExecutor(cacheConfig.getAsynchronousWorkersCore(), cacheConfig.getAsynchronousWorkersMax(), cacheConfig.getAsynchronousWorkerIdleLifetimeSecs(), TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(cacheConfig.getRevalidationQueueSize())));
    }

    ImmediateSchedulingStrategy(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void schedule(AsynchronousValidationRequest revalidationRequest) {
        Args.notNull((Object)revalidationRequest, (String)"AsynchronousValidationRequest");
        this.executor.execute(revalidationRequest);
    }

    @Override
    public void close() {
        this.executor.shutdown();
    }

    void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        this.executor.awaitTermination(timeout, unit);
    }
}

