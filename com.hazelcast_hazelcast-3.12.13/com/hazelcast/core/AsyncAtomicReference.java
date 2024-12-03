/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IFunction;

@Deprecated
public interface AsyncAtomicReference<E>
extends IAtomicReference<E> {
    public ICompletableFuture<Boolean> asyncCompareAndSet(E var1, E var2);

    public ICompletableFuture<E> asyncGet();

    public ICompletableFuture<Void> asyncSet(E var1);

    public ICompletableFuture<E> asyncGetAndSet(E var1);

    public ICompletableFuture<E> asyncSetAndGet(E var1);

    public ICompletableFuture<Boolean> asyncIsNull();

    public ICompletableFuture<Void> asyncClear();

    public ICompletableFuture<Boolean> asyncContains(E var1);

    public ICompletableFuture<Void> asyncAlter(IFunction<E, E> var1);

    public ICompletableFuture<E> asyncAlterAndGet(IFunction<E, E> var1);

    public ICompletableFuture<E> asyncGetAndAlter(IFunction<E, E> var1);

    public <R> ICompletableFuture<R> asyncApply(IFunction<E, R> var1);
}

