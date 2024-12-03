/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class WorkerAnt
extends Thread {
    private Task task;
    private Object notify;
    private volatile boolean finished = false;
    private volatile BuildException buildException;
    private volatile Throwable exception;
    public static final String ERROR_NO_TASK = "No task defined";

    public WorkerAnt(Task task, Object notify) {
        this.task = task;
        this.notify = notify != null ? notify : this;
    }

    public WorkerAnt(Task task) {
        this(task, null);
    }

    public synchronized BuildException getBuildException() {
        return this.buildException;
    }

    public synchronized Throwable getException() {
        return this.exception;
    }

    public Task getTask() {
        return this.task;
    }

    public synchronized boolean isFinished() {
        return this.finished;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void waitUntilFinished(long timeout) throws InterruptedException {
        long start = System.currentTimeMillis();
        long end = start + timeout;
        Object object = this.notify;
        synchronized (object) {
            long now = System.currentTimeMillis();
            while (!this.finished && now < end) {
                this.notify.wait(end - now);
                now = System.currentTimeMillis();
            }
        }
    }

    public void rethrowAnyBuildException() {
        BuildException ex = this.getBuildException();
        if (ex != null) {
            throw ex;
        }
    }

    private synchronized void caught(Throwable thrown) {
        this.exception = thrown;
        this.buildException = thrown instanceof BuildException ? (BuildException)thrown : new BuildException(thrown);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try {
            if (this.task != null) {
                this.task.execute();
            }
        }
        catch (Throwable thrown) {
            this.caught(thrown);
        }
        finally {
            Object object = this.notify;
            synchronized (object) {
                this.finished = true;
                this.notify.notifyAll();
            }
        }
    }
}

