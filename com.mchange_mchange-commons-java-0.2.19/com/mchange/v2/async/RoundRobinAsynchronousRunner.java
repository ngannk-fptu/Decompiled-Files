/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.async;

import com.mchange.v2.async.AsynchronousRunner;
import com.mchange.v2.async.CarefulRunnableQueue;
import com.mchange.v2.async.Queuable;
import com.mchange.v2.async.RunnableQueue;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.util.ResourceClosedException;

public class RoundRobinAsynchronousRunner
implements AsynchronousRunner,
Queuable {
    private static final MLogger logger = MLog.getLogger(RoundRobinAsynchronousRunner.class);
    final RunnableQueue[] rqs;
    int task_turn = 0;
    int view_turn = 0;

    public RoundRobinAsynchronousRunner(int n, boolean bl) {
        this.rqs = new RunnableQueue[n];
        for (int i = 0; i < n; ++i) {
            this.rqs[i] = new CarefulRunnableQueue(bl, false);
        }
    }

    @Override
    public synchronized void postRunnable(Runnable runnable) {
        try {
            int n = this.task_turn;
            this.task_turn = (this.task_turn + 1) % this.rqs.length;
            this.rqs[n].postRunnable(runnable);
        }
        catch (NullPointerException nullPointerException) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "NullPointerException while posting Runnable -- Probably we're closed.", nullPointerException);
            }
            this.close(true);
            throw new ResourceClosedException("Attempted to use a RoundRobinAsynchronousRunner in a closed or broken state.");
        }
    }

    @Override
    public synchronized RunnableQueue asRunnableQueue() {
        try {
            int n = this.view_turn;
            this.view_turn = (this.view_turn + 1) % this.rqs.length;
            return new RunnableQueueView(n);
        }
        catch (NullPointerException nullPointerException) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "NullPointerException in asRunnableQueue() -- Probably we're closed.", nullPointerException);
            }
            this.close(true);
            throw new ResourceClosedException("Attempted to use a RoundRobinAsynchronousRunner in a closed or broken state.");
        }
    }

    @Override
    public synchronized void close(boolean bl) {
        int n = this.rqs.length;
        for (int i = 0; i < n; ++i) {
            RoundRobinAsynchronousRunner.attemptClose(this.rqs[i], bl);
            this.rqs[i] = null;
        }
    }

    @Override
    public void close() {
        this.close(true);
    }

    static void attemptClose(RunnableQueue runnableQueue, boolean bl) {
        block2: {
            try {
                runnableQueue.close(bl);
            }
            catch (Exception exception) {
                if (!logger.isLoggable(MLevel.WARNING)) break block2;
                logger.log(MLevel.WARNING, "RunnableQueue close FAILED.", exception);
            }
        }
    }

    class RunnableQueueView
    implements RunnableQueue {
        final int rq_num;

        RunnableQueueView(int n) {
            this.rq_num = n;
        }

        @Override
        public void postRunnable(Runnable runnable) {
            RoundRobinAsynchronousRunner.this.rqs[this.rq_num].postRunnable(runnable);
        }

        @Override
        public void close(boolean bl) {
        }

        @Override
        public void close() {
        }
    }
}

