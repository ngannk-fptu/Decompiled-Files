/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.transport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.catalina.tribes.transport.AbstractRxTask;

public class RxTaskPool {
    final List<AbstractRxTask> idle = new ArrayList<AbstractRxTask>();
    final List<AbstractRxTask> used = new ArrayList<AbstractRxTask>();
    final Object mutex = new Object();
    boolean running = true;
    private int maxTasks;
    private int minTasks;
    private final TaskCreator creator;

    public RxTaskPool(int maxTasks, int minTasks, TaskCreator creator) throws Exception {
        this.maxTasks = maxTasks;
        this.minTasks = minTasks;
        this.creator = creator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void configureTask(AbstractRxTask task) {
        AbstractRxTask abstractRxTask = task;
        synchronized (abstractRxTask) {
            task.setTaskPool(this);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AbstractRxTask getRxTask() {
        AbstractRxTask worker = null;
        Object object = this.mutex;
        synchronized (object) {
            while (worker == null && this.running) {
                if (this.idle.size() > 0) {
                    try {
                        worker = this.idle.remove(0);
                    }
                    catch (NoSuchElementException x) {
                        worker = null;
                    }
                    continue;
                }
                if (this.used.size() < this.maxTasks && this.creator != null) {
                    worker = this.creator.createRxTask();
                    this.configureTask(worker);
                    continue;
                }
                try {
                    this.mutex.wait();
                }
                catch (InterruptedException x) {
                    Thread.currentThread().interrupt();
                }
            }
            if (worker != null) {
                this.used.add(worker);
            }
        }
        return worker;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int available() {
        Object object = this.mutex;
        synchronized (object) {
            return this.idle.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void returnWorker(AbstractRxTask worker) {
        if (this.running) {
            Object object = this.mutex;
            synchronized (object) {
                this.used.remove(worker);
                if (this.idle.size() < this.maxTasks && !this.idle.contains(worker)) {
                    this.idle.add(worker);
                } else {
                    worker.close();
                }
                this.mutex.notifyAll();
            }
        } else {
            worker.close();
        }
    }

    public int getMaxThreads() {
        return this.maxTasks;
    }

    public int getMinThreads() {
        return this.minTasks;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop() {
        this.running = false;
        Object object = this.mutex;
        synchronized (object) {
            Iterator<AbstractRxTask> i = this.idle.iterator();
            while (i.hasNext()) {
                AbstractRxTask worker = i.next();
                this.returnWorker(worker);
                i.remove();
            }
        }
    }

    public void setMaxTasks(int maxThreads) {
        this.maxTasks = maxThreads;
    }

    public void setMinTasks(int minThreads) {
        this.minTasks = minThreads;
    }

    public TaskCreator getTaskCreator() {
        return this.creator;
    }

    public static interface TaskCreator {
        public AbstractRxTask createRxTask();
    }
}

