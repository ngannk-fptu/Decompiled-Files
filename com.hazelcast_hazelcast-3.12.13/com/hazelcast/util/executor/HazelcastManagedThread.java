/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.executor;

import com.hazelcast.instance.OutOfMemoryErrorDispatcher;

public class HazelcastManagedThread
extends Thread {
    public HazelcastManagedThread() {
    }

    public HazelcastManagedThread(Runnable target) {
        super(target);
    }

    public HazelcastManagedThread(String name) {
        super(name);
    }

    public HazelcastManagedThread(Runnable target, String name) {
        super(target, name);
    }

    @Override
    public void setContextClassLoader(ClassLoader cl) {
        if (cl != null) {
            super.setContextClassLoader(cl);
        }
    }

    protected void beforeRun() {
    }

    protected void executeRun() {
        super.run();
    }

    protected void afterRun() {
    }

    @Override
    public void run() {
        try {
            this.beforeRun();
            this.executeRun();
        }
        catch (OutOfMemoryError e) {
            OutOfMemoryErrorDispatcher.onOutOfMemory(e);
        }
        finally {
            this.afterRun();
        }
    }
}

