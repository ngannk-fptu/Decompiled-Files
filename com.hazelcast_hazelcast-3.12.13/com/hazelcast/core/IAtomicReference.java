/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IFunction;

public interface IAtomicReference<E>
extends DistributedObject {
    public boolean compareAndSet(E var1, E var2);

    public E get();

    public void set(E var1);

    public E getAndSet(E var1);

    public E setAndGet(E var1);

    public boolean isNull();

    public void clear();

    public boolean contains(E var1);

    public void alter(IFunction<E, E> var1);

    public E alterAndGet(IFunction<E, E> var1);

    public E getAndAlter(IFunction<E, E> var1);

    public <R> R apply(IFunction<E, R> var1);

    public ICompletableFuture<Boolean> compareAndSetAsync(E var1, E var2);

    public ICompletableFuture<E> getAsync();

    public ICompletableFuture<Void> setAsync(E var1);

    public ICompletableFuture<E> getAndSetAsync(E var1);

    public ICompletableFuture<Boolean> isNullAsync();

    public ICompletableFuture<Void> clearAsync();

    public ICompletableFuture<Boolean> containsAsync(E var1);

    public ICompletableFuture<Void> alterAsync(IFunction<E, E> var1);

    public ICompletableFuture<E> alterAndGetAsync(IFunction<E, E> var1);

    public ICompletableFuture<E> getAndAlterAsync(IFunction<E, E> var1);

    public <R> ICompletableFuture<R> applyAsync(IFunction<E, R> var1);
}

