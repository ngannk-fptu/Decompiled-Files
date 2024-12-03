/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IFunction;

@Deprecated
public interface AsyncAtomicLong
extends IAtomicLong {
    public ICompletableFuture<Long> asyncAddAndGet(long var1);

    public ICompletableFuture<Boolean> asyncCompareAndSet(long var1, long var3);

    public ICompletableFuture<Long> asyncDecrementAndGet();

    public ICompletableFuture<Long> asyncGet();

    public ICompletableFuture<Long> asyncGetAndAdd(long var1);

    public ICompletableFuture<Long> asyncGetAndSet(long var1);

    public ICompletableFuture<Long> asyncIncrementAndGet();

    public ICompletableFuture<Long> asyncGetAndIncrement();

    public ICompletableFuture<Void> asyncSet(long var1);

    public ICompletableFuture<Void> asyncAlter(IFunction<Long, Long> var1);

    public ICompletableFuture<Long> asyncAlterAndGet(IFunction<Long, Long> var1);

    public ICompletableFuture<Long> asyncGetAndAlter(IFunction<Long, Long> var1);

    public <R> ICompletableFuture<R> asyncApply(IFunction<Long, R> var1);
}

