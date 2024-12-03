/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.durableexecutor.DurableExecutorServiceFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public interface DurableExecutorService
extends ExecutorService,
DistributedObject {
    public <T> DurableExecutorServiceFuture<T> submit(Callable<T> var1);

    public <T> DurableExecutorServiceFuture<T> submit(Runnable var1, T var2);

    public DurableExecutorServiceFuture<?> submit(Runnable var1);

    public <T> Future<T> retrieveResult(long var1);

    public void disposeResult(long var1);

    public <T> Future<T> retrieveAndDisposeResult(long var1);

    public void executeOnKeyOwner(Runnable var1, Object var2);

    public <T> DurableExecutorServiceFuture<T> submitToKeyOwner(Callable<T> var1, Object var2);

    public DurableExecutorServiceFuture<?> submitToKeyOwner(Runnable var1, Object var2);
}

