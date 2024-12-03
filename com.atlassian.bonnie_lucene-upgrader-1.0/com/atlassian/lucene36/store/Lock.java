/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.LockObtainFailedException;
import com.atlassian.lucene36.util.ThreadInterruptedException;
import java.io.IOException;

public abstract class Lock {
    public static long LOCK_POLL_INTERVAL = 1000L;
    public static final long LOCK_OBTAIN_WAIT_FOREVER = -1L;
    protected Throwable failureReason;

    public abstract boolean obtain() throws IOException;

    public boolean obtain(long lockWaitTimeout) throws LockObtainFailedException, IOException {
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

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Object run() throws LockObtainFailedException, IOException {
            Object object;
            block2: {
                boolean locked = false;
                try {
                    locked = this.lock.obtain(this.lockWaitTimeout);
                    object = this.doBody();
                    Object var4_3 = null;
                    if (!locked) break block2;
                }
                catch (Throwable throwable) {
                    block3: {
                        Object var4_4 = null;
                        if (!locked) break block3;
                        this.lock.release();
                    }
                    throw throwable;
                }
                this.lock.release();
            }
            return object;
        }
    }
}

