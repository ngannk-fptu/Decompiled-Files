/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.threads;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

public class TaskQueue
extends LinkedBlockingQueue<Runnable> {
    private static final long serialVersionUID = 1L;
    protected static final StringManager sm = StringManager.getManager(TaskQueue.class);
    private volatile transient ThreadPoolExecutor parent = null;

    public TaskQueue() {
    }

    public TaskQueue(int capacity) {
        super(capacity);
    }

    public TaskQueue(Collection<? extends Runnable> c) {
        super(c);
    }

    public void setParent(ThreadPoolExecutor tp) {
        this.parent = tp;
    }

    public boolean force(Runnable o) {
        if (this.parent == null || this.parent.isShutdown()) {
            throw new RejectedExecutionException(sm.getString("taskQueue.notRunning"));
        }
        return super.offer(o);
    }

    @Deprecated
    public boolean force(Runnable o, long timeout, TimeUnit unit) throws InterruptedException {
        if (this.parent == null || this.parent.isShutdown()) {
            throw new RejectedExecutionException(sm.getString("taskQueue.notRunning"));
        }
        return super.offer(o, timeout, unit);
    }

    @Override
    public boolean offer(Runnable o) {
        if (this.parent == null) {
            return super.offer(o);
        }
        if (this.parent.getPoolSizeNoLock() == this.parent.getMaximumPoolSize()) {
            return super.offer(o);
        }
        if (this.parent.getSubmittedCount() <= this.parent.getPoolSizeNoLock()) {
            return super.offer(o);
        }
        if (this.parent.getPoolSizeNoLock() < this.parent.getMaximumPoolSize()) {
            return false;
        }
        return super.offer(o);
    }

    @Override
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
        Runnable runnable = (Runnable)super.poll(timeout, unit);
        if (runnable == null && this.parent != null) {
            this.parent.stopCurrentThreadIfNeeded();
        }
        return runnable;
    }

    @Override
    public Runnable take() throws InterruptedException {
        if (this.parent != null && this.parent.currentThreadShouldBeStopped()) {
            return this.poll(this.parent.getKeepAliveTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
        }
        return (Runnable)super.take();
    }
}

