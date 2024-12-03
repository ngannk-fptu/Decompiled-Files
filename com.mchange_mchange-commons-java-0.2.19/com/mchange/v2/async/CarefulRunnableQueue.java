/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.async;

import com.mchange.v2.async.Queuable;
import com.mchange.v2.async.RunnableQueue;
import com.mchange.v2.async.StrandedTaskReporting;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.util.ResourceClosedException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CarefulRunnableQueue
implements RunnableQueue,
Queuable,
StrandedTaskReporting {
    private static final MLogger logger = MLog.getLogger(CarefulRunnableQueue.class);
    private List taskList = new LinkedList();
    private TaskThread t = new TaskThread();
    private boolean shutdown_on_interrupt;
    private boolean gentle_close_requested = false;
    private List strandedTasks = null;

    public CarefulRunnableQueue(boolean bl, boolean bl2) {
        this.shutdown_on_interrupt = bl2;
        this.t.setDaemon(bl);
        this.t.start();
    }

    @Override
    public RunnableQueue asRunnableQueue() {
        return this;
    }

    @Override
    public synchronized void postRunnable(Runnable runnable) {
        try {
            if (this.gentle_close_requested) {
                throw new ResourceClosedException("Attempted to post a task to a closing CarefulRunnableQueue.");
            }
            this.taskList.add(runnable);
            this.notifyAll();
        }
        catch (NullPointerException nullPointerException) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "NullPointerException while posting Runnable.", nullPointerException);
            }
            if (this.taskList == null) {
                throw new ResourceClosedException("Attempted to post a task to a CarefulRunnableQueue which has been closed, or whose TaskThread has been interrupted.");
            }
            throw nullPointerException;
        }
    }

    @Override
    public synchronized void close(boolean bl) {
        if (bl) {
            this.t.safeStop();
            this.t.interrupt();
        } else {
            this.gentle_close_requested = true;
        }
    }

    @Override
    public synchronized void close() {
        this.close(true);
    }

    @Override
    public synchronized List getStrandedTasks() {
        try {
            while (this.gentle_close_requested && this.taskList != null) {
                this.wait();
            }
            return this.strandedTasks;
        }
        catch (InterruptedException interruptedException) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, Thread.currentThread() + " interrupted while waiting for stranded tasks from CarefulRunnableQueue.", interruptedException);
            }
            throw new RuntimeException(Thread.currentThread() + " interrupted while waiting for stranded tasks from CarefulRunnableQueue.");
        }
    }

    private synchronized Runnable dequeueRunnable() {
        Runnable runnable = (Runnable)this.taskList.get(0);
        this.taskList.remove(0);
        return runnable;
    }

    private synchronized void awaitTask() throws InterruptedException {
        while (this.taskList.size() == 0) {
            if (this.gentle_close_requested) {
                this.t.safeStop();
                this.t.interrupt();
            }
            this.wait();
        }
    }

    class TaskThread
    extends Thread {
        boolean should_stop;

        TaskThread() {
            super("CarefulRunnableQueue.TaskThread");
            this.should_stop = false;
        }

        public synchronized void safeStop() {
            this.should_stop = true;
        }

        private synchronized boolean shouldStop() {
            return this.should_stop;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        public void run() {
            Object object;
            try {
                while (!this.shouldStop()) {
                    try {
                        CarefulRunnableQueue.this.awaitTask();
                        object = CarefulRunnableQueue.this.dequeueRunnable();
                        try {
                            object.run();
                        }
                        catch (Exception exception) {
                            if (!logger.isLoggable(MLevel.WARNING)) continue;
                            logger.log(MLevel.WARNING, this.getClass().getName() + " -- Unexpected exception in task!", exception);
                        }
                    }
                    catch (InterruptedException interruptedException) {
                        if (CarefulRunnableQueue.this.shutdown_on_interrupt) {
                            CarefulRunnableQueue.this.close(false);
                            if (!logger.isLoggable(MLevel.INFO)) continue;
                            logger.info(this.toString() + " interrupted. Shutting down after current tasks have completed.");
                            continue;
                        }
                        logger.info(this.toString() + " received interrupt. IGNORING.");
                    }
                }
                return;
            }
            finally {
                object = CarefulRunnableQueue.this;
                synchronized (object) {
                    CarefulRunnableQueue.this.strandedTasks = Collections.unmodifiableList(CarefulRunnableQueue.this.taskList);
                    CarefulRunnableQueue.this.taskList = null;
                    CarefulRunnableQueue.this.t = null;
                    CarefulRunnableQueue.this.notifyAll();
                }
            }
        }
    }
}

