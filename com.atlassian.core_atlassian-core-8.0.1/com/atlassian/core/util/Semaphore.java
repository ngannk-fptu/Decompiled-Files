/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util;

public class Semaphore {
    protected long permits;

    public Semaphore(long initialPermits) {
        this.permits = initialPermits;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void acquire() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        Semaphore semaphore = this;
        synchronized (semaphore) {
            try {
                while (this.permits <= 0L) {
                    this.wait();
                }
                --this.permits;
            }
            catch (InterruptedException ex) {
                this.notify();
                throw ex;
            }
        }
    }

    public boolean attempt(long msecs) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        Semaphore semaphore = this;
        synchronized (semaphore) {
            if (this.permits > 0L) {
                --this.permits;
                return true;
            }
            if (msecs <= 0L) {
                return false;
            }
            try {
                long startTime = System.currentTimeMillis();
                long waitTime = msecs;
                do {
                    this.wait(waitTime);
                    if (this.permits <= 0L) continue;
                    --this.permits;
                    return true;
                } while ((waitTime = msecs - (System.currentTimeMillis() - startTime)) > 0L);
                return false;
            }
            catch (InterruptedException ex) {
                this.notify();
                throw ex;
            }
        }
    }

    public synchronized void release() {
        ++this.permits;
        this.notify();
    }

    public synchronized void release(long n) {
        if (n < 0L) {
            throw new IllegalArgumentException("Negative argument");
        }
        this.permits += n;
        for (long i = 0L; i < n; ++i) {
            this.notify();
        }
    }

    public synchronized long permits() {
        return this.permits;
    }
}

