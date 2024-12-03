/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.executor;

import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.internal.util.counters.SwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.HashUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.executor.StripedRunnable;
import com.hazelcast.util.executor.TimeoutRunnable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public final class StripedExecutor
implements Executor {
    public static final AtomicLong THREAD_ID_GENERATOR = new AtomicLong();
    private final int size;
    private final ILogger logger;
    private final Worker[] workers;
    private final Random rand = new Random();
    private volatile boolean live = true;

    public StripedExecutor(ILogger logger, String threadNamePrefix, int threadCount, int queueCapacity) {
        this(logger, threadNamePrefix, threadCount, queueCapacity, false);
    }

    public StripedExecutor(ILogger logger, String threadNamePrefix, int threadCount, int queueCapacity, boolean lazyThreads) {
        Preconditions.checkPositive(threadCount, "threadCount should be positive but found " + threadCount);
        Preconditions.checkPositive(queueCapacity, "queueCapacity should be positive but found " + queueCapacity);
        this.logger = logger;
        this.size = threadCount;
        this.workers = new Worker[threadCount];
        int perThreadMaxQueueCapacity = (int)Math.ceil(1.0 * (double)queueCapacity / (double)threadCount);
        for (int i = 0; i < threadCount; ++i) {
            Worker worker = new Worker(threadNamePrefix, perThreadMaxQueueCapacity);
            if (!lazyThreads) {
                worker.started.set(true);
                worker.start();
            }
            this.workers[i] = worker;
        }
    }

    public int getWorkQueueSize() {
        int size = 0;
        for (Worker worker : this.workers) {
            size += worker.taskQueue.size();
        }
        return size;
    }

    public long processedCount() {
        long size = 0L;
        for (Worker worker : this.workers) {
            size += worker.processed.inc();
        }
        return size;
    }

    public void shutdown() {
        this.live = false;
        for (Worker worker : this.workers) {
            worker.shutdown();
        }
    }

    public boolean isLive() {
        return this.live;
    }

    @Override
    public void execute(Runnable task) {
        Preconditions.checkNotNull(task, "task can't be null");
        if (!this.live) {
            throw new RejectedExecutionException("Executor is terminated!");
        }
        Worker worker = this.getWorker(task);
        worker.schedule(task);
    }

    private Worker getWorker(Runnable task) {
        int key = task instanceof StripedRunnable ? ((StripedRunnable)task).getKey() : this.rand.nextInt();
        int index = HashUtil.hashToIndex(key, this.size);
        return this.workers[index];
    }

    public List<BlockingQueue<Runnable>> getTaskQueues() {
        ArrayList<BlockingQueue<Runnable>> taskQueues = new ArrayList<BlockingQueue<Runnable>>(this.workers.length);
        for (Worker worker : this.workers) {
            taskQueues.add(worker.taskQueue);
        }
        return taskQueues;
    }

    Worker[] getWorkers() {
        return this.workers;
    }

    final class Worker
    extends Thread {
        private final BlockingQueue<Runnable> taskQueue;
        private final SwCounter processed;
        private final int queueCapacity;
        private final AtomicBoolean started;

        private Worker(String threadNamePrefix, int queueCapacity) {
            super(threadNamePrefix + "-" + THREAD_ID_GENERATOR.incrementAndGet());
            this.processed = SwCounter.newSwCounter();
            this.started = new AtomicBoolean();
            this.taskQueue = new LinkedBlockingQueue<Runnable>(queueCapacity);
            this.queueCapacity = queueCapacity;
        }

        private void schedule(Runnable task) {
            if (!this.started.get() && this.started.compareAndSet(false, true)) {
                this.start();
            }
            long timeoutNanos = this.timeoutNanos(task);
            try {
                boolean offered;
                boolean bl = offered = timeoutNanos == 0L ? this.taskQueue.offer(task) : this.taskQueue.offer(task, timeoutNanos, TimeUnit.NANOSECONDS);
                if (!offered) {
                    throw new RejectedExecutionException("Task: " + task + " is rejected, the taskqueue of " + this.getName() + " is full!");
                }
            }
            catch (InterruptedException e) {
                Worker.currentThread().interrupt();
                throw new RejectedExecutionException("Thread is interrupted while offering work");
            }
        }

        private long timeoutNanos(Runnable task) {
            if (task instanceof TimeoutRunnable) {
                TimeoutRunnable r = (TimeoutRunnable)task;
                return r.getTimeUnit().toNanos(r.getTimeout());
            }
            return 0L;
        }

        @Override
        public void run() {
            try {
                while (StripedExecutor.this.live) {
                    try {
                        Runnable task = this.taskQueue.take();
                        this.process(task);
                    }
                    catch (InterruptedException ignore) {
                        EmptyStatement.ignore(ignore);
                    }
                }
            }
            catch (Throwable t) {
                StripedExecutor.this.logger.severe(this.getName() + " caught an exception", t);
            }
        }

        private void process(Runnable task) {
            this.processed.inc();
            try {
                task.run();
            }
            catch (Throwable e) {
                OutOfMemoryErrorDispatcher.inspectOutOfMemoryError(e);
                StripedExecutor.this.logger.severe(this.getName() + " caught an exception while processing:" + task, e);
            }
        }

        int getQueueCapacity() {
            return this.queueCapacity;
        }

        private void shutdown() {
            this.taskQueue.clear();
            this.interrupt();
        }
    }
}

