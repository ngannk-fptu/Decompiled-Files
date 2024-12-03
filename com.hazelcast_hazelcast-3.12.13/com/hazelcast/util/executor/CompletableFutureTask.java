/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.executor;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.logging.Logger;
import com.hazelcast.spi.impl.AbstractCompletableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class CompletableFutureTask<V>
extends AbstractCompletableFuture<V>
implements ICompletableFuture<V>,
RunnableFuture<V> {
    private static final AtomicReferenceFieldUpdater<CompletableFutureTask, Thread> RUNNER = AtomicReferenceFieldUpdater.newUpdater(CompletableFutureTask.class, Thread.class, "runner");
    private final Callable<V> callable;
    private volatile Thread runner;

    public CompletableFutureTask(Callable<V> callable, ExecutorService asyncExecutor) {
        super(asyncExecutor, Logger.getLogger(CompletableFutureTask.class));
        this.callable = callable;
    }

    public CompletableFutureTask(Runnable runnable, V result, ExecutorService asyncExecutor) {
        super(asyncExecutor, Logger.getLogger(CompletableFutureTask.class));
        this.callable = Executors.callable(runnable, result);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        block9: {
            if (this.isDone()) {
                return;
            }
            if (this.runner != null || !RUNNER.compareAndSet(this, null, Thread.currentThread())) {
                return;
            }
            try {
                Callable<V> c = this.callable;
                if (c == null) break block9;
                ExecutionException result = null;
                try {
                    result = (ExecutionException)c.call();
                }
                catch (Throwable ex) {
                    try {
                        result = new ExecutionException(ex);
                    }
                    catch (Throwable throwable) {
                        this.setResult(result);
                        throw throwable;
                    }
                    this.setResult(result);
                    break block9;
                }
                this.setResult(result);
            }
            finally {
                this.runner = null;
            }
        }
    }

    @Override
    protected void cancelled(boolean mayInterruptIfRunning) {
        Thread executingThread;
        if (mayInterruptIfRunning && (executingThread = this.runner) != null) {
            executingThread.interrupt();
        }
    }

    public String toString() {
        return "CompletableFutureTask{callable=" + this.callable + ", runner=" + this.runner + '}';
    }
}

