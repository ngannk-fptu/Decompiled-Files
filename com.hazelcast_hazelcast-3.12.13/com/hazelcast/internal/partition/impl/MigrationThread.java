/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.internal.partition.impl.MigrationManager;
import com.hazelcast.internal.partition.impl.MigrationQueue;
import com.hazelcast.internal.partition.impl.MigrationRunnable;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.ThreadUtil;
import java.util.concurrent.TimeUnit;

class MigrationThread
extends Thread
implements Runnable {
    private static final long DEFAULT_MIGRATION_SLEEP_INTERVAL = 250L;
    private final MigrationManager migrationManager;
    private final MigrationQueue queue;
    private final ILogger logger;
    private final long partitionMigrationInterval;
    private final long sleepTime;
    private volatile MigrationRunnable activeTask;
    private volatile boolean running = true;

    MigrationThread(MigrationManager migrationManager, String hzName, ILogger logger, MigrationQueue queue) {
        super(ThreadUtil.createThreadName(hzName, "migration"));
        this.migrationManager = migrationManager;
        this.queue = queue;
        this.partitionMigrationInterval = migrationManager.partitionMigrationInterval;
        this.sleepTime = Math.max(250L, this.partitionMigrationInterval);
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            while (this.running) {
                this.doRun();
            }
        }
        catch (InterruptedException e) {
            if (this.logger.isFinestEnabled()) {
                this.logger.finest("MigrationThread is interrupted: " + e.getMessage());
            }
        }
        catch (OutOfMemoryError e) {
            OutOfMemoryErrorDispatcher.onOutOfMemory(e);
        }
        finally {
            this.queue.clear();
        }
    }

    private void doRun() throws InterruptedException {
        boolean hasNoTasks;
        MigrationRunnable runnable;
        boolean migrating = false;
        while (this.migrationManager.areMigrationTasksAllowed() && (runnable = this.queue.poll(1, TimeUnit.SECONDS)) != null) {
            this.processTask(runnable);
            if (!(migrating |= runnable instanceof MigrationManager.MigrateTask) || this.partitionMigrationInterval <= 0L) continue;
            Thread.sleep(this.partitionMigrationInterval);
        }
        boolean bl = hasNoTasks = !this.queue.hasMigrationTasks();
        if (hasNoTasks) {
            if (migrating) {
                this.logger.info("All migration tasks have been completed. (" + this.migrationManager.getStats().formatToString(this.logger.isFineEnabled()) + ")");
            }
            Thread.sleep(this.sleepTime);
        } else if (!this.migrationManager.areMigrationTasksAllowed()) {
            Thread.sleep(this.sleepTime);
        }
    }

    private boolean processTask(MigrationRunnable runnable) {
        try {
            if (runnable == null || !this.running) {
                boolean bl = false;
                return bl;
            }
            this.activeTask = runnable;
            runnable.run();
        }
        catch (Throwable t) {
            this.logger.warning(t);
        }
        finally {
            this.queue.afterTaskCompletion(runnable);
            this.activeTask = null;
        }
        return true;
    }

    MigrationRunnable getActiveTask() {
        return this.activeTask;
    }

    void stopNow() {
        assert (MigrationThread.currentThread() != this) : "stopNow must not be called on the migration thread";
        this.running = false;
        this.queue.clear();
        this.interrupt();
        boolean currentThreadInterrupted = false;
        while (true) {
            try {
                this.join();
            }
            catch (InterruptedException e) {
                currentThreadInterrupted = true;
                continue;
            }
            break;
        }
        if (currentThreadInterrupted) {
            MigrationThread.currentThread().interrupt();
        }
    }
}

