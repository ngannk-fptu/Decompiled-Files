/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.threads;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class InlineExecutorService
extends AbstractExecutorService {
    private volatile boolean shutdown;
    private volatile boolean taskRunning;
    private volatile boolean terminated;
    private final Object lock = new Object();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void shutdown() {
        this.shutdown = true;
        Object object = this.lock;
        synchronized (object) {
            this.terminated = !this.taskRunning;
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        this.shutdown();
        return null;
    }

    @Override
    public boolean isShutdown() {
        return this.shutdown;
    }

    @Override
    public boolean isTerminated() {
        return this.terminated;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        Object object = this.lock;
        synchronized (object) {
            if (this.terminated) {
                return true;
            }
            this.lock.wait(unit.toMillis(timeout));
            return this.terminated;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute(Runnable command) {
        Object object = this.lock;
        synchronized (object) {
            if (this.shutdown) {
                throw new RejectedExecutionException();
            }
            this.taskRunning = true;
        }
        command.run();
        object = this.lock;
        synchronized (object) {
            this.taskRunning = false;
            if (this.shutdown) {
                this.terminated = true;
                this.lock.notifyAll();
            }
        }
    }
}

