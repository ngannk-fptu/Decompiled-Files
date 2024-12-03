/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.util.threads;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class LimitLatch {
    private static final Log log = LogFactory.getLog(LimitLatch.class);
    private final Sync sync;
    private final AtomicLong count;
    private volatile long limit;
    private volatile boolean released = false;

    public LimitLatch(long limit) {
        this.limit = limit;
        this.count = new AtomicLong(0L);
        this.sync = new Sync();
    }

    public long getCount() {
        return this.count.get();
    }

    public long getLimit() {
        return this.limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public void countUpOrAwait() throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Counting up[" + Thread.currentThread().getName() + "] latch=" + this.getCount()));
        }
        this.sync.acquireSharedInterruptibly(1);
    }

    public long countDown() {
        this.sync.releaseShared(0);
        long result = this.getCount();
        if (log.isDebugEnabled()) {
            log.debug((Object)("Counting down[" + Thread.currentThread().getName() + "] latch=" + result));
        }
        return result;
    }

    public boolean releaseAll() {
        this.released = true;
        return this.sync.releaseShared(0);
    }

    public void reset() {
        this.count.set(0L);
        this.released = false;
    }

    public boolean hasQueuedThreads() {
        return this.sync.hasQueuedThreads();
    }

    public Collection<Thread> getQueuedThreads() {
        return this.sync.getQueuedThreads();
    }

    private class Sync
    extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 1L;

        Sync() {
        }

        @Override
        protected int tryAcquireShared(int ignored) {
            long newCount = LimitLatch.this.count.incrementAndGet();
            if (!LimitLatch.this.released && newCount > LimitLatch.this.limit) {
                LimitLatch.this.count.decrementAndGet();
                return -1;
            }
            return 1;
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            LimitLatch.this.count.decrementAndGet();
            return true;
        }
    }
}

