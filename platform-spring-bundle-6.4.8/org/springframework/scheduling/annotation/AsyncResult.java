/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.annotation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;

public class AsyncResult<V>
implements ListenableFuture<V> {
    @Nullable
    private final V value;
    @Nullable
    private final Throwable executionException;

    public AsyncResult(@Nullable V value) {
        this(value, null);
    }

    private AsyncResult(@Nullable V value, @Nullable Throwable ex) {
        this.value = value;
        this.executionException = ex;
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
    @Nullable
    public V get() throws ExecutionException {
        if (this.executionException != null) {
            throw this.executionException instanceof ExecutionException ? (ExecutionException)this.executionException : new ExecutionException(this.executionException);
        }
        return this.value;
    }

    @Override
    @Nullable
    public V get(long timeout, TimeUnit unit) throws ExecutionException {
        return this.get();
    }

    @Override
    public void addCallback(ListenableFutureCallback<? super V> callback) {
        this.addCallback((SuccessCallback<? super V>)callback, (FailureCallback)callback);
    }

    @Override
    public void addCallback(SuccessCallback<? super V> successCallback, FailureCallback failureCallback) {
        try {
            if (this.executionException != null) {
                failureCallback.onFailure(AsyncResult.exposedException(this.executionException));
            } else {
                successCallback.onSuccess(this.value);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    @Override
    public CompletableFuture<V> completable() {
        if (this.executionException != null) {
            CompletableFuture completable = new CompletableFuture();
            completable.completeExceptionally(AsyncResult.exposedException(this.executionException));
            return completable;
        }
        return CompletableFuture.completedFuture(this.value);
    }

    public static <V> ListenableFuture<V> forValue(V value) {
        return new AsyncResult<V>(value, null);
    }

    public static <V> ListenableFuture<V> forExecutionException(Throwable ex) {
        return new AsyncResult<Object>(null, ex);
    }

    private static Throwable exposedException(Throwable original) {
        Throwable cause;
        if (original instanceof ExecutionException && (cause = original.getCause()) != null) {
            return cause;
        }
        return original;
    }
}

