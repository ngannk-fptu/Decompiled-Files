/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer.internal.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CompositeFuture<T>
implements Future<List<T>> {
    private final List<? extends Future<T>> futures;

    public CompositeFuture(List<? extends Future<T>> futures) {
        this.futures = futures;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean anyCancelled = false;
        for (Future<T> future : this.futures) {
            anyCancelled |= future.cancel(mayInterruptIfRunning);
        }
        return anyCancelled;
    }

    @Override
    public boolean isCancelled() {
        for (Future<T> future : this.futures) {
            if (future.isCancelled()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isDone() {
        for (Future<T> future : this.futures) {
            if (future.isDone()) continue;
            return false;
        }
        return true;
    }

    @Override
    public List<T> get() throws InterruptedException, ExecutionException {
        ArrayList<T> results = new ArrayList<T>();
        for (Future<T> future : this.futures) {
            results.add(future.get());
        }
        return results;
    }

    @Override
    public List<T> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        long doneTime = System.nanoTime() + unit.toNanos(timeout);
        ArrayList<T> results = new ArrayList<T>();
        for (Future<T> future : this.futures) {
            long timeLeft = doneTime - System.nanoTime();
            results.add(future.get(timeLeft, TimeUnit.NANOSECONDS));
        }
        return results;
    }
}

