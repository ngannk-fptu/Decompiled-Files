/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.executionservice.impl;

import com.hazelcast.util.ExceptionUtil;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class DelegatingCallableTaskDecorator<V>
implements Callable<Future<V>> {
    private final ExecutorService executor;
    private final Callable<V> callable;

    public DelegatingCallableTaskDecorator(Callable<V> callable, ExecutorService executor) {
        this.executor = executor;
        this.callable = callable;
    }

    @Override
    public Future<V> call() {
        try {
            return this.executor.submit(this.callable);
        }
        catch (Throwable t) {
            ExceptionUtil.sneakyThrow(t);
            return null;
        }
    }
}

