/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Collections2
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.sal.core.executor;

import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ThreadLocalDelegateExecutorService
implements ExecutorService {
    private final ExecutorService delegate;
    private final ThreadLocalDelegateExecutorFactory delegateExecutorFactory;

    public ThreadLocalDelegateExecutorService(ExecutorService delegate, ThreadLocalDelegateExecutorFactory delegateExecutorFactory) {
        this.delegate = (ExecutorService)Preconditions.checkNotNull((Object)delegate);
        this.delegateExecutorFactory = (ThreadLocalDelegateExecutorFactory)Preconditions.checkNotNull((Object)delegateExecutorFactory);
    }

    @Override
    public void shutdown() {
        this.delegate.shutdown();
    }

    @Override
    @Nonnull
    public List<Runnable> shutdownNow() {
        return this.delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.awaitTermination(timeout, unit);
    }

    @Override
    @Nonnull
    public <T> Future<T> submit(Callable<T> callable) {
        return this.delegate.submit(this.threadLocalDelegateCallable(callable));
    }

    @Override
    @Nonnull
    public <T> Future<T> submit(Runnable runnable, @Nullable T result) {
        return this.delegate.submit(this.threadLocalDelegateRunnable(runnable), result);
    }

    @Override
    @Nonnull
    public Future<?> submit(Runnable runnable) {
        return this.delegate.submit(this.threadLocalDelegateRunnable(runnable));
    }

    @Override
    @Nonnull
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> callables) throws InterruptedException {
        return this.delegate.invokeAll(this.threadLocalDelegateCallableCollection(callables));
    }

    @Override
    @Nonnull
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> callables, long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.invokeAll(this.threadLocalDelegateCallableCollection(callables), timeout, unit);
    }

    @Override
    @Nonnull
    public <T> T invokeAny(Collection<? extends Callable<T>> callables) throws InterruptedException, ExecutionException {
        return this.delegate.invokeAny(this.threadLocalDelegateCallableCollection(callables));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> callables, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate.invokeAny(this.threadLocalDelegateCallableCollection(callables), timeout, unit);
    }

    @Override
    public void execute(Runnable runnable) {
        this.delegate.execute(this.threadLocalDelegateRunnable(runnable));
    }

    private Runnable threadLocalDelegateRunnable(Runnable runnable) {
        return this.delegateExecutorFactory.createRunnable(runnable);
    }

    private <T> Callable<T> threadLocalDelegateCallable(Callable<T> callable) {
        return this.delegateExecutorFactory.createCallable(callable);
    }

    private <T> Collection<? extends Callable<T>> threadLocalDelegateCallableCollection(Collection<? extends Callable<T>> callables) {
        return Collections2.transform(callables, this::threadLocalDelegateCallable);
    }
}

