/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.async;

import com.mchange.v2.async.Queuable;
import com.mchange.v2.async.RunnableQueue;
import java.util.LinkedList;
import java.util.List;

public class SimpleRunnableQueue
implements RunnableQueue,
Queuable {
    private List taskList = new LinkedList();
    private Thread t = new TaskThread();
    boolean gentle_close_requested = false;

    public SimpleRunnableQueue(boolean bl) {
        this.t.setDaemon(bl);
        this.t.start();
    }

    public SimpleRunnableQueue() {
        this(true);
    }

    @Override
    public RunnableQueue asRunnableQueue() {
        return this;
    }

    @Override
    public synchronized void postRunnable(Runnable runnable) {
        if (this.gentle_close_requested) {
            throw new IllegalStateException("Attempted to post a task to a closed AsynchronousRunner.");
        }
        this.taskList.add(runnable);
        this.notifyAll();
    }

    @Override
    public synchronized void close(boolean bl) {
        if (bl) {
            this.t.interrupt();
        } else {
            this.gentle_close_requested = true;
        }
    }

    @Override
    public synchronized void close() {
        this.close(true);
    }

    private synchronized Runnable dequeueRunnable() {
        Runnable runnable = (Runnable)this.taskList.get(0);
        this.taskList.remove(0);
        return runnable;
    }

    private synchronized void awaitTask() throws InterruptedException {
        while (this.taskList.size() == 0) {
            if (this.gentle_close_requested) {
                this.t.interrupt();
            }
            this.wait();
        }
    }

    class TaskThread
    extends Thread {
        TaskThread() {
            super("SimpleRunnableQueue.TaskThread");
        }

        @Override
        public void run() {
            block8: {
                block6: while (true) {
                    try {
                        while (!this.isInterrupted()) {
                            SimpleRunnableQueue.this.awaitTask();
                            Runnable runnable = SimpleRunnableQueue.this.dequeueRunnable();
                            try {
                                runnable.run();
                                continue block6;
                            }
                            catch (Exception exception) {
                                System.err.println(this.getClass().getName() + " -- Unexpected exception in task!");
                                exception.printStackTrace();
                            }
                        }
                        break block8;
                    }
                    catch (InterruptedException interruptedException) {
                        break block8;
                    }
                }
                finally {
                    SimpleRunnableQueue.this.taskList = null;
                    SimpleRunnableQueue.this.t = null;
                }
            }
        }
    }
}

