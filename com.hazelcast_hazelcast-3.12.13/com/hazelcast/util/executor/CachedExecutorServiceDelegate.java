/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.util.executor;

import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.executor.CompletableFutureTask;
import com.hazelcast.util.executor.ManagedExecutorService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class CachedExecutorServiceDelegate
implements ExecutorService,
ManagedExecutorService {
    public static final long TIME = 250L;
    private static final AtomicLongFieldUpdater<CachedExecutorServiceDelegate> EXECUTED_COUNT = AtomicLongFieldUpdater.newUpdater(CachedExecutorServiceDelegate.class, "executedCount");
    private volatile long executedCount;
    private final String name;
    private final int maxPoolSize;
    private final ExecutorService cachedExecutor;
    private final NodeEngine nodeEngine;
    private final BlockingQueue<Runnable> taskQ;
    private final Lock lock = new ReentrantLock();
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private volatile int size;

    public CachedExecutorServiceDelegate(NodeEngine nodeEngine, String name, ExecutorService cachedExecutor, int maxPoolSize, int queueCapacity) {
        if (maxPoolSize <= 0) {
            throw new IllegalArgumentException("Max pool size must be positive!");
        }
        if (queueCapacity <= 0) {
            throw new IllegalArgumentException("Queue capacity must be positive!");
        }
        this.name = name;
        this.maxPoolSize = maxPoolSize;
        this.cachedExecutor = cachedExecutor;
        this.taskQ = new LinkedBlockingQueue<Runnable>(queueCapacity);
        this.nodeEngine = nodeEngine;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getCompletedTaskCount() {
        return this.executedCount;
    }

    @Override
    public int getMaximumPoolSize() {
        return this.maxPoolSize;
    }

    @Override
    public int getPoolSize() {
        return this.size;
    }

    @Override
    public int getQueueSize() {
        return this.taskQ.size();
    }

    @Override
    public int getRemainingQueueCapacity() {
        return this.taskQ.remainingCapacity();
    }

    @Override
    public void execute(Runnable command) {
        if (this.shutdown.get()) {
            throw new RejectedExecutionException();
        }
        if (!this.taskQ.offer(command)) {
            throw new RejectedExecutionException("Executor[" + this.name + "] is overloaded!");
        }
        this.addNewWorkerIfRequired();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        CompletableFutureTask<T> rf = new CompletableFutureTask<T>(task, this.getAsyncExecutor());
        this.execute(rf);
        return rf;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        CompletableFutureTask<T> rf = new CompletableFutureTask<T>(task, result, this.getAsyncExecutor());
        this.execute(rf);
        return rf;
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.submit(task, null);
    }

    @SuppressFBWarnings(value={"VO_VOLATILE_INCREMENT"})
    private void addNewWorkerIfRequired() {
        if (this.size < this.maxPoolSize) {
            try {
                this.lock.lockInterruptibly();
                try {
                    if (this.size < this.maxPoolSize && this.getQueueSize() > 0) {
                        ++this.size;
                        this.cachedExecutor.execute(new Worker());
                    }
                }
                finally {
                    this.lock.unlock();
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void shutdown() {
        this.shutdown.set(true);
    }

    @Override
    public List<Runnable> shutdownNow() {
        if (!this.shutdown.compareAndSet(false, true)) {
            return Collections.emptyList();
        }
        LinkedList<Runnable> tasks = new LinkedList<Runnable>();
        this.taskQ.drainTo(tasks);
        for (Runnable task : tasks) {
            if (!(task instanceof RunnableFuture)) continue;
            ((RunnableFuture)task).cancel(false);
        }
        return tasks;
    }

    @Override
    public boolean isShutdown() {
        return this.shutdown.get();
    }

    @Override
    public boolean isTerminated() {
        return this.shutdown.get() && this.taskQ.isEmpty();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException();
    }

    private ExecutorService getAsyncExecutor() {
        return this.nodeEngine.getExecutionService().getExecutor("hz:async");
    }

    private class Worker
    implements Runnable {
        private Worker() {
        }

        @Override
        public void run() {
            try {
                Runnable r;
                do {
                    if ((r = (Runnable)CachedExecutorServiceDelegate.this.taskQ.poll(1L, TimeUnit.MILLISECONDS)) == null) continue;
                    r.run();
                    EXECUTED_COUNT.incrementAndGet(CachedExecutorServiceDelegate.this);
                } while (r != null);
            }
            catch (InterruptedException ignored) {
                EmptyStatement.ignore(ignored);
            }
            finally {
                this.exit();
            }
        }

        void exit() {
            CachedExecutorServiceDelegate.this.lock.lock();
            try {
                CachedExecutorServiceDelegate.this.size--;
                if (CachedExecutorServiceDelegate.this.taskQ.peek() != null) {
                    CachedExecutorServiceDelegate.this.addNewWorkerIfRequired();
                }
            }
            finally {
                CachedExecutorServiceDelegate.this.lock.unlock();
            }
        }
    }
}

