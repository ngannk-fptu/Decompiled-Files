/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.util.ExceptionUtil;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class SimpleCompletedFuture<E>
implements InternalCompletableFuture<E> {
    private final Object result;

    public SimpleCompletedFuture(E result) {
        this.result = result;
    }

    public SimpleCompletedFuture(Throwable exceptionalResult) {
        this.result = exceptionalResult;
    }

    @Override
    public E join() {
        if (this.result instanceof Throwable) {
            ExceptionUtil.sneakyThrow((Throwable)this.result);
        }
        return (E)this.result;
    }

    @Override
    public boolean complete(Object value) {
        return false;
    }

    @Override
    public void andThen(ExecutionCallback<E> callback) {
        if (this.result instanceof Throwable) {
            callback.onFailure((Throwable)this.result);
        } else {
            callback.onResponse(this.result);
        }
    }

    @Override
    public void andThen(final ExecutionCallback<E> callback, Executor executor) {
        executor.execute(new Runnable(){

            @Override
            public void run() {
                if (SimpleCompletedFuture.this.result instanceof Throwable) {
                    callback.onFailure((Throwable)SimpleCompletedFuture.this.result);
                } else {
                    callback.onResponse(SimpleCompletedFuture.this.result);
                }
            }
        });
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return this.result instanceof CancellationException;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public E get() throws ExecutionException {
        if (this.result instanceof Throwable) {
            throw new ExecutionException((Throwable)this.result);
        }
        return (E)this.result;
    }

    @Override
    public E get(long timeout, TimeUnit unit) throws ExecutionException {
        return this.get();
    }
}

