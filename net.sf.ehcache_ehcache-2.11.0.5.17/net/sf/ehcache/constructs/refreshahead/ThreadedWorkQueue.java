/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.refreshahead;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadedWorkQueue<W> {
    private static final int MINUTES_OF_THE_IDLE_LIFE = 5;
    private final LinkedBlockingQueue<W> queue;
    private final ExecutorService threadPool;
    private volatile boolean isAlive;
    private final AtomicInteger offerCounter = new AtomicInteger();
    private final AtomicInteger droppedCounter = new AtomicInteger();
    private final AtomicInteger processedCounter = new AtomicInteger();
    private final BatchWorker<W> dispatcher;
    private final int batchSize;

    public ThreadedWorkQueue(BatchWorker<W> dispatcher, int numberOfThreads, ThreadFactory factory, int maximumQueueSize, int batchSize) {
        this.threadPool = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 5L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(), factory);
        this.batchSize = batchSize;
        this.dispatcher = dispatcher;
        this.isAlive = true;
        this.queue = new LinkedBlockingQueue(maximumQueueSize);
        for (int i = 0; i < numberOfThreads; ++i) {
            this.threadPool.submit(new Runnable(){

                @Override
                public void run() {
                    while (ThreadedWorkQueue.this.isAlive()) {
                        try {
                            ThreadedWorkQueue.this.pullFromQueueAndDispatch();
                        }
                        catch (Throwable throwable) {}
                    }
                }
            });
        }
    }

    public void offer(W workUnit) {
        this.offerCounter.incrementAndGet();
        while (!this.queue.offer(workUnit)) {
            if (this.queue.poll() == null) continue;
            this.droppedCounter.incrementAndGet();
        }
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public long getBacklogCount() {
        return this.offerCounter.get() - (this.processedCounter.get() + this.droppedCounter.get());
    }

    public int getOfferedCount() {
        return this.offerCounter.get();
    }

    public int getDroppedCount() {
        return this.droppedCounter.get();
    }

    public int getProcessedCount() {
        return this.processedCounter.get();
    }

    public BatchWorker<W> getDispatcher() {
        return this.dispatcher;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public void shutdown() {
        this.isAlive = false;
        this.threadPool.shutdownNow();
        this.queue.clear();
    }

    private void pullFromQueueAndDispatch() throws InterruptedException {
        ArrayList<W> batch = new ArrayList<W>(this.getBatchSize());
        int currentCount = 0;
        W r = this.queue.take();
        while (r != null) {
            batch.add(r);
            if (++currentCount >= this.getBatchSize()) break;
            r = this.queue.poll();
        }
        if (currentCount > 0 && this.isAlive()) {
            this.processedCounter.addAndGet(batch.size());
            this.getDispatcher().process(batch);
        }
    }

    public static interface BatchWorker<WW> {
        public void process(Collection<? extends WW> var1);
    }
}

