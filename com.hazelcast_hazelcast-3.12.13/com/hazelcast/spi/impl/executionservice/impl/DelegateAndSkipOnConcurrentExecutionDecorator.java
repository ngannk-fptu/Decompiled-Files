/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.executionservice.impl;

import com.hazelcast.util.ExceptionUtil;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public class DelegateAndSkipOnConcurrentExecutionDecorator
implements Runnable {
    private final AtomicBoolean isAlreadyRunning = new AtomicBoolean();
    private final Runnable runnable;
    private final Executor executor;
    private volatile Throwable throwable;

    public DelegateAndSkipOnConcurrentExecutionDecorator(Runnable runnable, Executor executor) {
        this.runnable = new DelegateDecorator(runnable);
        this.executor = executor;
    }

    @Override
    public void run() {
        if (this.isAlreadyRunning.compareAndSet(false, true)) {
            if (this.throwable != null) {
                ExceptionUtil.rethrow(this.throwable);
                return;
            }
            this.executor.execute(this.runnable);
        }
    }

    public String toString() {
        return "DelegateAndSkipOnConcurrentExecutionDecorator{isAlreadyRunning=" + this.isAlreadyRunning + ", runnable=" + this.runnable + ", executor=" + this.executor + ", throwable=" + this.throwable + '}';
    }

    private class DelegateDecorator
    implements Runnable {
        private final Runnable runnable;

        DelegateDecorator(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                this.runnable.run();
            }
            catch (Throwable t) {
                DelegateAndSkipOnConcurrentExecutionDecorator.this.throwable = t;
            }
            finally {
                DelegateAndSkipOnConcurrentExecutionDecorator.this.isAlreadyRunning.set(false);
            }
        }

        public String toString() {
            return "DelegateDecorator{runnable=" + this.runnable + '}';
        }
    }
}

