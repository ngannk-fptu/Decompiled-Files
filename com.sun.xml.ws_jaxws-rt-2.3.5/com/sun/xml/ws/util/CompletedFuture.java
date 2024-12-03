/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CompletedFuture<T>
implements Future<T> {
    private final T v;
    private final Throwable re;

    public CompletedFuture(T v, Throwable re) {
        this.v = v;
        this.re = re;
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
    public T get() throws ExecutionException {
        if (this.re != null) {
            throw new ExecutionException(this.re);
        }
        return this.v;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws ExecutionException {
        return this.get();
    }
}

