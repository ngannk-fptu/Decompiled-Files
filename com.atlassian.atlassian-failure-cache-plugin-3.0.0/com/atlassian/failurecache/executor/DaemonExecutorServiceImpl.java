/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  io.atlassian.util.concurrent.ThreadFactories
 *  io.atlassian.util.concurrent.ThreadFactories$Type
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.failurecache.executor;

import com.atlassian.failurecache.executor.DaemonExecutorService;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.DisposableBean;

public class DaemonExecutorServiceImpl
implements DaemonExecutorService,
DisposableBean {
    private final ScheduledExecutorService executor;

    public DaemonExecutorServiceImpl(ThreadLocalDelegateExecutorFactory delegateExecutorFactory) {
        ThreadFactory factory = ThreadFactories.namedThreadFactory((String)"Failure Cache Plugin Executor", (ThreadFactories.Type)ThreadFactories.Type.DAEMON, (int)THREAD_POOL_PRIORITY);
        ScheduledThreadPoolExecutor delegate = new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE, factory);
        delegate.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        this.executor = delegateExecutorFactory.createScheduledExecutorService((ScheduledExecutorService)delegate);
    }

    @Override
    public void execute(Runnable command) {
        this.executor.execute(command);
    }

    @Override
    public void shutdown() {
        this.executor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.executor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.executor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.executor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.executor.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.executor.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.executor.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.executor.submit(task);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return this.executor.schedule(command, delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return this.executor.schedule(callable, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.executor.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return this.executor.scheduleAtFixedRate(command, initialDelay, delay, unit);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.executor.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.executor.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.executor.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.executor.invokeAny(tasks, timeout, unit);
    }

    @Override
    public <O, I extends Iterable<O>> ImmutableList<O> invokeAllAndGet(Iterable<? extends Callable<I>> callables, long timeout, TimeUnit unit) throws ExecutionException, InterruptedException {
        ImmutableList.Builder builder = ImmutableList.builder();
        List futures = this.executor.invokeAll(Lists.newArrayList(callables), timeout, unit);
        for (Future future : futures) {
            if (future.isCancelled()) continue;
            builder.addAll((Iterable)future.get());
        }
        return builder.build();
    }

    public void destroy() throws Exception {
        this.executor.shutdown();
    }
}

