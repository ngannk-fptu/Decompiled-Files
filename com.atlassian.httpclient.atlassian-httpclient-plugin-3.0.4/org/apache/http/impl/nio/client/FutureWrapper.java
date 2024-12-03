/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.http.concurrent.Cancellable;

final class FutureWrapper<T>
implements Future<T> {
    private final Future<T> future;
    private final Cancellable cancellable;

    public FutureWrapper(Future<T> future, Cancellable cancellable) {
        this.future = future;
        this.cancellable = cancellable;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        try {
            if (this.cancellable != null) {
                this.cancellable.cancel();
            }
        }
        finally {
            return this.future.cancel(mayInterruptIfRunning);
        }
    }

    @Override
    public boolean isCancelled() {
        return this.future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return this.future.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return this.future.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.future.get(timeout, unit);
    }

    public String toString() {
        return this.future.toString();
    }
}

