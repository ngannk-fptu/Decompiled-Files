/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.IdentityHashMap;
import java.util.Map;
import org.apache.lucene.util.ThreadInterruptedException;

final class DocumentsWriterStallControl {
    private volatile boolean stalled;
    private int numWaiting;
    private boolean wasStalled;
    private final Map<Thread, Boolean> waiting = new IdentityHashMap<Thread, Boolean>();

    DocumentsWriterStallControl() {
    }

    synchronized void updateStalled(boolean stalled) {
        this.stalled = stalled;
        if (stalled) {
            this.wasStalled = true;
        }
        this.notifyAll();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void waitIfStalled() {
        if (this.stalled) {
            DocumentsWriterStallControl documentsWriterStallControl = this;
            synchronized (documentsWriterStallControl) {
                if (this.stalled) {
                    try {
                        assert (this.incWaiters());
                        this.wait();
                        assert (this.decrWaiters());
                    }
                    catch (InterruptedException e) {
                        throw new ThreadInterruptedException(e);
                    }
                }
            }
        }
    }

    boolean anyStalledThreads() {
        return this.stalled;
    }

    private boolean incWaiters() {
        ++this.numWaiting;
        assert (this.waiting.put(Thread.currentThread(), Boolean.TRUE) == null);
        return this.numWaiting > 0;
    }

    private boolean decrWaiters() {
        --this.numWaiting;
        assert (this.waiting.remove(Thread.currentThread()) != null);
        return this.numWaiting >= 0;
    }

    synchronized boolean hasBlocked() {
        return this.numWaiting > 0;
    }

    boolean isHealthy() {
        return !this.stalled;
    }

    synchronized boolean isThreadQueued(Thread t) {
        return this.waiting.containsKey(t);
    }

    synchronized boolean wasStalled() {
        return this.wasStalled;
    }
}

