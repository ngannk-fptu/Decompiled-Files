/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import com.sun.media.jai.util.Job;
import com.sun.media.jai.util.SunTileScheduler;
import java.util.LinkedList;
import java.util.Vector;

class WorkerThread
extends Thread {
    public static final Object TERMINATE = new Object();
    SunTileScheduler scheduler;
    boolean isPrefetch;

    public WorkerThread(ThreadGroup group, SunTileScheduler scheduler, boolean isPrefetch) {
        super(group, group.getName() + group.activeCount());
        this.scheduler = scheduler;
        this.isPrefetch = isPrefetch;
        this.setDaemon(true);
        this.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        LinkedList jobQueue = this.scheduler.getQueue(this.isPrefetch);
        while (true) {
            Object object;
            Object dequeuedObject = null;
            LinkedList linkedList = jobQueue;
            synchronized (linkedList) {
                if (jobQueue.size() > 0) {
                    dequeuedObject = jobQueue.removeFirst();
                } else {
                    try {
                        jobQueue.wait();
                        continue;
                    }
                    catch (InterruptedException ie) {
                        // empty catch block
                    }
                }
            }
            if (dequeuedObject == TERMINATE || this.getThreadGroup() == null || this.getThreadGroup().isDestroyed()) {
                Vector threads = this.scheduler.getWorkers(this.isPrefetch);
                object = threads;
                synchronized (threads) {
                    threads.remove(this);
                    // ** MonitorExit[var4_4] (shouldn't be in output)
                    return;
                }
            }
            Job job = dequeuedObject;
            if (job == null) continue;
            job.compute();
            if (!job.isBlocking()) continue;
            object = this.scheduler;
            synchronized (object) {
                this.scheduler.notify();
            }
        }
    }
}

