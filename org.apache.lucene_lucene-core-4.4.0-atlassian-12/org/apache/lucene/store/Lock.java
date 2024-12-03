/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.IOException;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.ThreadInterruptedException;

public abstract class Lock {
    public static long LOCK_POLL_INTERVAL = 1000L;
    public static final long LOCK_OBTAIN_WAIT_FOREVER = -1L;
    protected Throwable failureReason;

    public abstract boolean obtain() throws IOException;

    public boolean obtain(long lockWaitTimeout) throws IOException {
        this.failureReason = null;
        boolean locked = this.obtain();
        if (lockWaitTimeout < 0L && lockWaitTimeout != -1L) {
            throw new IllegalArgumentException("lockWaitTimeout should be LOCK_OBTAIN_WAIT_FOREVER or a non-negative number (got " + lockWaitTimeout + ")");
        }
        long maxSleepCount = lockWaitTimeout / LOCK_POLL_INTERVAL;
        long sleepCount = 0L;
        while (!locked) {
            if (lockWaitTimeout != -1L && sleepCount++ >= maxSleepCount) {
                String reason = "Lock obtain timed out: " + this.toString();
                if (this.failureReason != null) {
                    reason = reason + ": " + this.failureReason;
                }
                LockObtainFailedException e = new LockObtainFailedException(reason);
                if (this.failureReason != null) {
                    e.initCause(this.failureReason);
                }
                throw e;
            }
            try {
                Thread.sleep(LOCK_POLL_INTERVAL);
            }
            catch (InterruptedException ie) {
                throw new ThreadInterruptedException(ie);
            }
            locked = this.obtain();
        }
        return locked;
    }

    public abstract void release() throws IOException;

    public abstract boolean isLocked() throws IOException;

    public static abstract class With {
        private Lock lock;
        private long lockWaitTimeout;

        public With(Lock lock, long lockWaitTimeout) {
            this.lock = lock;
            this.lockWaitTimeout = lockWaitTimeout;
        }

        protected abstract Object doBody() throws IOException;

        public Object run() throws IOException {
            boolean locked = false;
            try {
                locked = this.lock.obtain(this.lockWaitTimeout);
                Object object = this.doBody();
                return object;
            }
            finally {
                if (locked) {
                    this.lock.release();
                }
            }
        }
    }
}

