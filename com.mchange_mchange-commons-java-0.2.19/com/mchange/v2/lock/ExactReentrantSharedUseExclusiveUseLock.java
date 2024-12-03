/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.lock;

import com.mchange.v2.lock.SharedUseExclusiveUseLock;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ExactReentrantSharedUseExclusiveUseLock
implements SharedUseExclusiveUseLock {
    Set waitingShared = new HashSet();
    List activeShared = new LinkedList();
    Set waitingExclusive = new HashSet();
    Thread activeExclusive = null;
    int exclusive_shared_reentries = 0;
    int exclusive_exclusive_reentries = 0;
    String name;

    public ExactReentrantSharedUseExclusiveUseLock(String string) {
        this.name = string;
    }

    public ExactReentrantSharedUseExclusiveUseLock() {
        this(null);
    }

    void status(String string) {
        System.err.println(this + " -- after " + string);
        System.err.println("waitingShared: " + this.waitingShared);
        System.err.println("activeShared: " + this.activeShared);
        System.err.println("waitingExclusive: " + this.waitingExclusive);
        System.err.println("activeExclusive: " + this.activeExclusive);
        System.err.println("exclusive_shared_reentries: " + this.exclusive_shared_reentries);
        System.err.println("exclusive_exclusive_reentries: " + this.exclusive_exclusive_reentries);
        System.err.println(" ---- ");
        System.err.println();
    }

    @Override
    public synchronized void acquireShared() throws InterruptedException {
        Thread thread = Thread.currentThread();
        if (thread == this.activeExclusive) {
            ++this.exclusive_shared_reentries;
        } else {
            try {
                this.waitingShared.add(thread);
                while (!this.okayForShared()) {
                    this.wait();
                }
                this.activeShared.add(thread);
            }
            finally {
                this.waitingShared.remove(thread);
            }
        }
    }

    @Override
    public synchronized void relinquishShared() {
        Thread thread = Thread.currentThread();
        if (thread == this.activeExclusive) {
            --this.exclusive_shared_reentries;
            if (this.exclusive_shared_reentries < 0) {
                throw new IllegalStateException(thread + " relinquished a shared lock (reentrant on exclusive) it did not hold!");
            }
        } else {
            boolean bl = this.activeShared.remove(thread);
            if (!bl) {
                throw new IllegalStateException(thread + " relinquished a shared lock it did not hold!");
            }
            this.notifyAll();
        }
    }

    @Override
    public synchronized void acquireExclusive() throws InterruptedException {
        Thread thread = Thread.currentThread();
        if (thread == this.activeExclusive) {
            ++this.exclusive_exclusive_reentries;
        } else {
            try {
                this.waitingExclusive.add(thread);
                while (!this.okayForExclusive(thread)) {
                    this.wait();
                }
                this.activeExclusive = thread;
            }
            finally {
                this.waitingExclusive.remove(thread);
            }
        }
    }

    @Override
    public synchronized void relinquishExclusive() {
        Thread thread = Thread.currentThread();
        if (thread != this.activeExclusive) {
            throw new IllegalStateException(thread + " relinquished an exclusive lock it did not hold!");
        }
        if (this.exclusive_exclusive_reentries > 0) {
            --this.exclusive_exclusive_reentries;
        } else {
            if (this.exclusive_shared_reentries != 0) {
                throw new IllegalStateException(thread + " relinquished an exclusive lock while it had reentered but not yet relinquished shared lock acquisitions!");
            }
            this.activeExclusive = null;
            this.notifyAll();
        }
    }

    private boolean okayForShared() {
        return this.activeExclusive == null && this.waitingExclusive.size() == 0;
    }

    private boolean okayForExclusive(Thread thread) {
        int n = this.activeShared.size();
        if (n == 0) {
            return this.activeExclusive == null;
        }
        if (n == 1) {
            return this.activeShared.get(0) == thread;
        }
        HashSet hashSet = new HashSet(this.activeShared);
        return hashSet.size() == 1 && hashSet.contains(thread);
    }

    public String toString() {
        return super.toString() + " [name=" + this.name + ']';
    }
}

