/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DelegatingScheduledFutureStripper<V>
implements ScheduledFuture<V> {
    private final ScheduledFuture<V> original;

    public DelegatingScheduledFutureStripper(ScheduledFuture<V> original) {
        Preconditions.checkNotNull(original, "Original is null.");
        this.original = original;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return this.original.getDelay(unit);
    }

    @Override
    public int compareTo(Delayed o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancelled = this.original.cancel(mayInterruptIfRunning);
        try {
            return this.peel().cancel(mayInterruptIfRunning);
        }
        catch (CancellationException e) {
            this.ignore();
            return cancelled;
        }
    }

    @Override
    public boolean isCancelled() {
        return this.original.isCancelled() || this.peel().isCancelled();
    }

    @Override
    public boolean isDone() {
        return this.original.isDone() && this.peel().isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return this.peel().get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException();
    }

    private Future<V> peel() {
        try {
            return (Future)this.original.get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ExceptionUtil.sneakyThrow(e);
        }
        catch (ExecutionException e) {
            ExceptionUtil.sneakyThrow(e);
        }
        return null;
    }

    private void ignore() {
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DelegatingScheduledFutureStripper that = (DelegatingScheduledFutureStripper)o;
        return this.original.equals(that.original);
    }

    public int hashCode() {
        return this.original.hashCode();
    }
}

