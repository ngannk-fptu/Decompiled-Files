/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.executionservice.impl;

import java.util.concurrent.Executor;

class DelegatingTaskDecorator
implements Runnable {
    private final Executor executor;
    private final Runnable runnable;

    public DelegatingTaskDecorator(Runnable runnable, Executor executor) {
        this.executor = executor;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        this.executor.execute(this.runnable);
    }

    public String toString() {
        return "DelegateDecorator{executor=" + this.executor + ", runnable=" + this.runnable + '}';
    }
}

