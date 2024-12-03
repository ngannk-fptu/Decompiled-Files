/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer.internal.future;

import com.amazonaws.services.s3.transfer.internal.future.CompletedFuture;
import com.amazonaws.services.s3.transfer.internal.future.DelegatingFuture;
import com.amazonaws.services.s3.transfer.internal.future.FailedFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureImpl<T>
implements Future<T> {
    public final DelegatingFuture<T> delegatingFuture = new DelegatingFuture();

    public void complete(T value) {
        this.delegatingFuture.setDelegateIfUnset(new CompletedFuture<T>(value));
    }

    public void fail(Throwable t) {
        this.delegatingFuture.setDelegateIfUnset(new FailedFuture(t));
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.delegatingFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return this.delegatingFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return this.delegatingFuture.isDone();
    }

    public T getOrThrowUnchecked(String errorMessage) {
        try {
            return this.get();
        }
        catch (Throwable e) {
            throw new RuntimeException(errorMessage, e);
        }
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return this.delegatingFuture.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegatingFuture.get(timeout, unit);
    }
}

