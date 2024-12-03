/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.executor;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DelegatingFuture<V>
implements InternalCompletableFuture<V> {
    private static final AtomicReferenceFieldUpdater<DelegatingFuture, Object> DESERIALIZED_VALUE = AtomicReferenceFieldUpdater.newUpdater(DelegatingFuture.class, Object.class, "deserializedValue");
    private static final Object VOID = new Object(){

        public String toString() {
            return "void";
        }
    };
    private final InternalCompletableFuture future;
    private final InternalSerializationService serializationService;
    private final Object result;
    private volatile Object deserializedValue = VOID;

    public DelegatingFuture(InternalCompletableFuture future, SerializationService serializationService) {
        this(future, serializationService, null);
    }

    public DelegatingFuture(InternalCompletableFuture future, SerializationService serializationService, V result) {
        this.future = future;
        this.serializationService = (InternalSerializationService)serializationService;
        this.result = result;
    }

    @Override
    public final V get() throws InterruptedException, ExecutionException {
        return this.resolve(this.future.get());
    }

    @Override
    public final V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.resolve(this.future.get(timeout, unit));
    }

    private V resolve(Object object) {
        block3: {
            if (this.result != null) {
                return (V)this.result;
            }
            if (this.deserializedValue != VOID) {
                return (V)this.deserializedValue;
            }
            if (!(object instanceof Data)) break block3;
            Data data = (Data)object;
            object = this.serializationService.toObject(data);
            this.serializationService.disposeData(data);
            do {
                Object current;
                if ((current = this.deserializedValue) == VOID) continue;
                object = current;
                break;
            } while (!DESERIALIZED_VALUE.compareAndSet(this, VOID, object));
        }
        return (V)object;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return this.future.isCancelled();
    }

    @Override
    public final boolean isDone() {
        return this.future.isDone();
    }

    @Override
    public boolean complete(Object value) {
        return this.future.complete(value);
    }

    protected void setError(Throwable error) {
        this.future.complete(error);
    }

    protected ICompletableFuture getFuture() {
        return this.future;
    }

    @Override
    public V join() {
        return this.resolve(this.future.join());
    }

    @Override
    public void andThen(ExecutionCallback<V> callback) {
        this.future.andThen(new DelegatingExecutionCallback(callback));
    }

    @Override
    public void andThen(ExecutionCallback<V> callback, Executor executor) {
        this.future.andThen(new DelegatingExecutionCallback(callback), executor);
    }

    private class DelegatingExecutionCallback
    implements ExecutionCallback<V> {
        private final ExecutionCallback<V> callback;

        DelegatingExecutionCallback(ExecutionCallback<V> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(Object response) {
            this.callback.onResponse(DelegatingFuture.this.resolve(response));
        }

        @Override
        public void onFailure(Throwable t) {
            this.callback.onFailure(t);
        }
    }
}

