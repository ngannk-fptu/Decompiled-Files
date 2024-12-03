/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.util.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
final class LimitedExecutor
implements Executor {
    private final Executor delegate;
    private final BlockingQueue<Runnable> overflow = new LinkedBlockingQueue<Runnable>();
    private final Semaphore semaphore;

    LimitedExecutor(Executor delegate, int limit) {
        this.delegate = delegate;
        this.semaphore = new Semaphore(limit);
    }

    @Override
    public void execute(Runnable command) {
        if (this.semaphore.tryAcquire()) {
            try {
                this.delegate.execute(new Runner(command));
            }
            catch (RejectedExecutionException rej) {
                this.semaphore.release();
                throw rej;
            }
        } else {
            this.overflow.add(command);
            while (this.semaphore.availablePermits() > 0) {
                if (this.resubmit()) continue;
                return;
            }
        }
    }

    private boolean resubmit() {
        Runnable next = (Runnable)this.overflow.poll();
        if (next != null) {
            this.execute(next);
            return true;
        }
        return false;
    }

    class Runner
    implements Runnable {
        private final Runnable delegate;

        Runner(Runnable delegate) {
            this.delegate = delegate;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            try {
                this.delegate.run();
            }
            finally {
                LimitedExecutor.this.semaphore.release();
                LimitedExecutor.this.resubmit();
            }
        }
    }
}

