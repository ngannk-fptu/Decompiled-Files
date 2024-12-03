/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.nonstop.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CountingThreadFactory
implements ThreadFactory {
    private final AtomicInteger count = new AtomicInteger();
    private final ThreadFactory actualFactory;

    public CountingThreadFactory(ThreadFactory actualFactory) {
        this.actualFactory = actualFactory;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread newThread = this.actualFactory.newThread(new RunnableWithLifeCycle(this, r));
        if (newThread != null) {
            this.count.incrementAndGet();
        }
        return newThread;
    }

    public int getNumberOfThreads() {
        return this.count.get();
    }

    private void threadExecutionComplete() {
        this.count.decrementAndGet();
    }

    private static class RunnableWithLifeCycle
    implements Runnable {
        private final Runnable actualRunnable;
        private final CountingThreadFactory countingThreadFactory;

        public RunnableWithLifeCycle(CountingThreadFactory countingThreadFactory, Runnable actualRunnable) {
            this.countingThreadFactory = countingThreadFactory;
            this.actualRunnable = actualRunnable;
        }

        @Override
        public void run() {
            try {
                if (this.actualRunnable != null) {
                    this.actualRunnable.run();
                }
            }
            finally {
                this.countingThreadFactory.threadExecutionComplete();
            }
        }
    }
}

