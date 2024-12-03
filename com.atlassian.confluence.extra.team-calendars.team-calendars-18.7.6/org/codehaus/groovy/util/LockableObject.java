/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class LockableObject
extends AbstractQueuedSynchronizer {
    private static final long serialVersionUID = 2284470475073785118L;
    transient Thread owner;

    @Override
    protected final boolean isHeldExclusively() {
        return this.getState() != 0 && this.owner == Thread.currentThread();
    }

    public final void lock() {
        if (this.compareAndSetState(0, 1)) {
            this.owner = Thread.currentThread();
        } else {
            this.acquire(1);
        }
    }

    public final void unlock() {
        this.release(1);
    }

    @Override
    protected final boolean tryAcquire(int acquires) {
        Thread current = Thread.currentThread();
        int c = this.getState();
        if (c == 0) {
            if (this.compareAndSetState(0, acquires)) {
                this.owner = current;
                return true;
            }
        } else if (current == this.owner) {
            this.setState(c + acquires);
            return true;
        }
        return false;
    }

    @Override
    protected final boolean tryRelease(int releases) {
        int c = this.getState() - releases;
        if (Thread.currentThread() != this.owner) {
            throw new IllegalMonitorStateException();
        }
        boolean free = false;
        if (c == 0) {
            free = true;
            this.owner = null;
        }
        this.setState(c);
        return free;
    }
}

