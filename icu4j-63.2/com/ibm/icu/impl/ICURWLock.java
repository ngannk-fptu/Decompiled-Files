/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ICURWLock {
    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private Stats stats = null;

    public synchronized Stats resetStats() {
        Stats result = this.stats;
        this.stats = new Stats();
        return result;
    }

    public synchronized Stats clearStats() {
        Stats result = this.stats;
        this.stats = null;
        return result;
    }

    public synchronized Stats getStats() {
        return this.stats == null ? null : new Stats(this.stats);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void acquireRead() {
        if (this.stats != null) {
            ICURWLock iCURWLock = this;
            synchronized (iCURWLock) {
                ++this.stats._rc;
                if (this.rwl.getReadLockCount() > 0) {
                    ++this.stats._mrc;
                }
                if (this.rwl.isWriteLocked()) {
                    ++this.stats._wrc;
                }
            }
        }
        this.rwl.readLock().lock();
    }

    public void releaseRead() {
        this.rwl.readLock().unlock();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void acquireWrite() {
        if (this.stats != null) {
            ICURWLock iCURWLock = this;
            synchronized (iCURWLock) {
                ++this.stats._wc;
                if (this.rwl.getReadLockCount() > 0 || this.rwl.isWriteLocked()) {
                    ++this.stats._wwc;
                }
            }
        }
        this.rwl.writeLock().lock();
    }

    public void releaseWrite() {
        this.rwl.writeLock().unlock();
    }

    public static final class Stats {
        public int _rc;
        public int _mrc;
        public int _wrc;
        public int _wc;
        public int _wwc;

        private Stats() {
        }

        private Stats(int rc, int mrc, int wrc, int wc, int wwc) {
            this._rc = rc;
            this._mrc = mrc;
            this._wrc = wrc;
            this._wc = wc;
            this._wwc = wwc;
        }

        private Stats(Stats rhs) {
            this(rhs._rc, rhs._mrc, rhs._wrc, rhs._wc, rhs._wwc);
        }

        public String toString() {
            return " rc: " + this._rc + " mrc: " + this._mrc + " wrc: " + this._wrc + " wc: " + this._wc + " wwc: " + this._wwc;
        }
    }
}

