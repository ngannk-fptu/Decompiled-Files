/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.v1.util.RunnableQueue;
import java.util.LinkedList;
import java.util.List;

public class SimpleRunnableQueue
implements RunnableQueue {
    private List taskList = new LinkedList();
    private Thread t = new TaskThread();

    public SimpleRunnableQueue(boolean bl) {
        this.t.setDaemon(bl);
        this.t.start();
    }

    public SimpleRunnableQueue() {
        this(true);
    }

    @Override
    public synchronized void postRunnable(Runnable runnable) {
        this.taskList.add(runnable);
        this.notifyAll();
    }

    public synchronized void close() {
        this.t.interrupt();
        this.taskList = null;
        this.t = null;
    }

    private synchronized Runnable dequeueRunnable() {
        Runnable runnable = (Runnable)this.taskList.get(0);
        this.taskList.remove(0);
        return runnable;
    }

    private synchronized void awaitTask() throws InterruptedException {
        while (this.taskList.size() == 0) {
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
            block4: while (true) {
                try {
                    while (true) {
                        SimpleRunnableQueue.this.awaitTask();
                        Runnable runnable = SimpleRunnableQueue.this.dequeueRunnable();
                        try {
                            runnable.run();
                            continue block4;
                        }
                        catch (Exception exception) {
                            System.err.println(this.getClass().getName() + " -- Unexpected exception in task!");
                            exception.printStackTrace();
                            continue;
                        }
                        break;
                    }
                }
                catch (InterruptedException interruptedException) {
                    System.err.println(this.toString() + " interrupted. Shutting down.");
                    return;
                }
            }
        }
    }
}

