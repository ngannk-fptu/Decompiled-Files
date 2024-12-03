/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.concurrent.FailureCallback
 *  org.springframework.util.concurrent.ListenableFuture
 *  org.springframework.util.concurrent.ListenableFutureCallback
 *  org.springframework.util.concurrent.SuccessCallback
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

    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return true;
    }

    @Nullable
    public V get() throws ExecutionException {
        if (this.executionException != null) {
            throw this.executionException instanceof ExecutionException ? (ExecutionException)this.executionException : new ExecutionException(this.executionException);
        }
        return this.value;
    }

    @Nullable
    public V get(long timeout, TimeUnit unit) throws ExecutionException {
        return this.get();
    }

    public void addCallback(ListenableFutureCallback<? super V> callback) {
        this.addCallback((SuccessCallback<? super V>)callback, (FailureCallback)callback);
    }

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

