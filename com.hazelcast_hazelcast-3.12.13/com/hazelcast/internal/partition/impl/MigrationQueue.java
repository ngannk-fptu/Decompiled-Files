/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.internal.partition.impl.MigrationRunnable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class MigrationQueue {
    private final AtomicInteger migrateTaskCount = new AtomicInteger();
    private final BlockingQueue<MigrationRunnable> queue = new LinkedBlockingQueue<MigrationRunnable>();

    MigrationQueue() {
    }

    @SuppressFBWarnings(value={"RV_RETURN_VALUE_IGNORED"}, justification="offer will always be successful since queue is unbounded")
    public void add(MigrationRunnable task) {
        this.migrateTaskCount.incrementAndGet();
        this.queue.offer(task);
    }

    public MigrationRunnable poll(int timeout, TimeUnit unit) throws InterruptedException {
        return this.queue.poll(timeout, unit);
    }

    public void clear() {
        ArrayList sink = new ArrayList();
        this.queue.drainTo(sink);
        for (MigrationRunnable task : sink) {
            this.afterTaskCompletion(task);
        }
    }

    public void afterTaskCompletion(MigrationRunnable task) {
        if (this.migrateTaskCount.decrementAndGet() < 0) {
            throw new IllegalStateException();
        }
    }

    public int migrationTaskCount() {
        return this.migrateTaskCount.get();
    }

    public boolean hasMigrationTasks() {
        return this.migrateTaskCount.get() > 0;
    }

    public String toString() {
        return "MigrationQueue{migrateTaskCount=" + this.migrateTaskCount + ", queue=" + this.queue + '}';
    }
}

