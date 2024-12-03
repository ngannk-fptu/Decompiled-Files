/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.executor;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ExceptionUtil;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class CompletedFuture<V>
implements InternalCompletableFuture<V> {
    private final SerializationService serializationService;
    private final Executor userExecutor;
    private final Object value;

    public CompletedFuture(SerializationService serializationService, Object value, Executor userExecutor) {
        this.serializationService = serializationService;
        this.userExecutor = userExecutor;
        this.value = value;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        Object object = this.value;
        if (object instanceof Data) {
            object = this.serializationService.toObject(object);
        }
        if (object instanceof Throwable) {
            if (object instanceof ExecutionException) {
                throw (ExecutionException)object;
            }
            if (object instanceof InterruptedException) {
                throw (InterruptedException)object;
            }
            throw new ExecutionException((Throwable)object);
        }
        return (V)object;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.get();
    }

    @Override
    public V join() {
        try {
            return this.get();
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    @Override
    public boolean complete(Object value) {
        return false;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public void andThen(ExecutionCallback<V> callback) {
        this.andThen(callback, this.userExecutor);
    }

    @Override
    public void andThen(final ExecutionCallback<V> callback, Executor executor) {
        executor.execute(new Runnable(){

            @Override
            public void run() {
                Object object = CompletedFuture.this.value;
                if (object instanceof Data) {
                    object = CompletedFuture.this.serializationService.toObject(object);
                }
                if (object instanceof Throwable) {
                    callback.onFailure((Throwable)object);
                } else {
                    callback.onResponse(object);
                }
            }
        });
    }
}

