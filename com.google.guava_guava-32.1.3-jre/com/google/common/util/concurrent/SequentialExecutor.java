/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.concurrent.GuardedBy
 *  com.google.j2objc.annotations.RetainedWith
 *  javax.annotation.CheckForNull
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ElementTypesAreNonnullByDefault;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import com.google.j2objc.annotations.RetainedWith;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
final class SequentialExecutor
implements Executor {
    private static final Logger log = Logger.getLogger(SequentialExecutor.class.getName());
    private final Executor executor;
    @GuardedBy(value="queue")
    private final Deque<Runnable> queue = new ArrayDeque<Runnable>();
    @GuardedBy(value="queue")
    private WorkerRunningState workerRunningState = WorkerRunningState.IDLE;
    @GuardedBy(value="queue")
    private long workerRunCount = 0L;
    @RetainedWith
    private final QueueWorker worker = new QueueWorker();

    SequentialExecutor(Executor executor) {
        this.executor = Preconditions.checkNotNull(executor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute(final Runnable task) {
        boolean alreadyMarkedQueued;
        Runnable submittedTask;
        long oldRunCount;
        Preconditions.checkNotNull(task);
        Deque<Runnable> deque = this.queue;
        synchronized (deque) {
            if (this.workerRunningState == WorkerRunningState.RUNNING || this.workerRunningState == WorkerRunningState.QUEUED) {
                this.queue.add(task);
                return;
            }
            oldRunCount = this.workerRunCount;
            submittedTask = new Runnable(this){

                @Override
                public void run() {
                    task.run();
                }

                public String toString() {
                    return task.toString();
                }
            };
            this.queue.add(submittedTask);
            this.workerRunningState = WorkerRunningState.QUEUING;
        }
        try {
            this.executor.execute(this.worker);
        }
        catch (Error | RuntimeException t) {
            Deque<Runnable> deque2 = this.queue;
            synchronized (deque2) {
                boolean removed;
                boolean bl = removed = (this.workerRunningState == WorkerRunningState.IDLE || this.workerRunningState == WorkerRunningState.QUEUING) && this.queue.removeLastOccurrence(submittedTask);
                if (!(t instanceof RejectedExecutionException) || removed) {
                    throw t;
                }
            }
            return;
        }
        boolean bl = alreadyMarkedQueued = this.workerRunningState != WorkerRunningState.QUEUING;
        if (alreadyMarkedQueued) {
            return;
        }
        Deque<Runnable> deque3 = this.queue;
        synchronized (deque3) {
            if (this.workerRunCount == oldRunCount && this.workerRunningState == WorkerRunningState.QUEUING) {
                this.workerRunningState = WorkerRunningState.QUEUED;
            }
        }
    }

    public String toString() {
        return "SequentialExecutor@" + System.identityHashCode(this) + "{" + this.executor + "}";
    }

    private final class QueueWorker
    implements Runnable {
        @CheckForNull
        Runnable task;

        private QueueWorker() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            try {
                this.workOnQueue();
            }
            catch (Error e) {
                Deque deque = SequentialExecutor.this.queue;
                synchronized (deque) {
                    SequentialExecutor.this.workerRunningState = WorkerRunningState.IDLE;
                }
                throw e;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         * Converted monitor instructions to comments
         * Lifted jumps to return sites
         */
        private void workOnQueue() {
            boolean interruptedDuringTask = false;
            boolean hasSetRunning = false;
            while (true) {
                block14: {
                    block15: {
                        try {
                            Deque deque = SequentialExecutor.this.queue;
                            // MONITORENTER : deque
                            if (hasSetRunning) break block14;
                            if (SequentialExecutor.this.workerRunningState != WorkerRunningState.RUNNING) break block15;
                            // MONITOREXIT : deque
                            if (!interruptedDuringTask) return;
                            Thread.currentThread().interrupt();
                            return;
                        }
                        catch (Throwable throwable) {
                            if (!interruptedDuringTask) throw throwable;
                            Thread.currentThread().interrupt();
                            throw throwable;
                        }
                    }
                    SequentialExecutor.this.workerRunCount++;
                    SequentialExecutor.this.workerRunningState = WorkerRunningState.RUNNING;
                    hasSetRunning = true;
                }
                this.task = (Runnable)SequentialExecutor.this.queue.poll();
                if (this.task == null) {
                    SequentialExecutor.this.workerRunningState = WorkerRunningState.IDLE;
                    // MONITOREXIT : deque
                    if (!interruptedDuringTask) return;
                    Thread.currentThread().interrupt();
                    return;
                }
                // MONITOREXIT : deque
                interruptedDuringTask |= Thread.interrupted();
                try {
                    this.task.run();
                    continue;
                }
                catch (RuntimeException e) {
                    log.log(Level.SEVERE, "Exception while executing runnable " + this.task, e);
                    continue;
                }
                finally {
                    this.task = null;
                    continue;
                }
                break;
            }
        }

        public String toString() {
            Runnable currentlyRunning = this.task;
            if (currentlyRunning != null) {
                return "SequentialExecutorWorker{running=" + currentlyRunning + "}";
            }
            return "SequentialExecutorWorker{state=" + (Object)((Object)SequentialExecutor.this.workerRunningState) + "}";
        }
    }

    static enum WorkerRunningState {
        IDLE,
        QUEUING,
        QUEUED,
        RUNNING;

    }
}

