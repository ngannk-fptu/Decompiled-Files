/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IFunction;

public interface IAtomicLong
extends DistributedObject {
    @Override
    public String getName();

    public long addAndGet(long var1);

    public boolean compareAndSet(long var1, long var3);

    public long decrementAndGet();

    public long get();

    public long getAndAdd(long var1);

    public long getAndSet(long var1);

    public long incrementAndGet();

    public long getAndIncrement();

    public void set(long var1);

    public void alter(IFunction<Long, Long> var1);

    public long alterAndGet(IFunction<Long, Long> var1);

    public long getAndAlter(IFunction<Long, Long> var1);

    public <R> R apply(IFunction<Long, R> var1);

    public ICompletableFuture<Long> addAndGetAsync(long var1);

    public ICompletableFuture<Boolean> compareAndSetAsync(long var1, long var3);

    public ICompletableFuture<Long> decrementAndGetAsync();

    public ICompletableFuture<Long> getAsync();

    public ICompletableFuture<Long> getAndAddAsync(long var1);

    public ICompletableFuture<Long> getAndSetAsync(long var1);

    public ICompletableFuture<Long> incrementAndGetAsync();

    public ICompletableFuture<Long> getAndIncrementAsync();

    public ICompletableFuture<Void> setAsync(long var1);

    public ICompletableFuture<Void> alterAsync(IFunction<Long, Long> var1);

    public ICompletableFuture<Long> alterAndGetAsync(IFunction<Long, Long> var1);

    public ICompletableFuture<Long> getAndAlterAsync(IFunction<Long, Long> var1);

    public <R> ICompletableFuture<R> applyAsync(IFunction<Long, R> var1);
}

