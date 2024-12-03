/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.util.concurrent;

import com.atlassian.confluence.impl.util.concurrent.TaskWrapper;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.checkerframework.checker.nullness.qual.NonNull;

class TaskWrappingExecutorService
implements ExecutorService {
    private final ExecutorService delegate;
    protected final TaskWrapper taskWrapper;

    public TaskWrappingExecutorService(ExecutorService delegate, TaskWrapper taskWrapper) {
        this.delegate = Objects.requireNonNull(delegate);
        this.taskWrapper = Objects.requireNonNull(taskWrapper);
    }

    @Override
    public void shutdown() {
        this.delegate.shutdown();
    }

    @Override
    public @NonNull List<Runnable> shutdownNow() {
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
    public <T> @NonNull Future<T> submit(Callable<T> callable) {
        return this.delegate.submit(this.taskWrapper.wrap(callable));
    }

    @Override
    public <T> @NonNull Future<T> submit(Runnable runnable, T result) {
        return this.delegate.submit(this.taskWrapper.wrap(runnable), result);
    }

    @Override
    public @NonNull Future<?> submit(Runnable runnable) {
        return this.delegate.submit(this.taskWrapper.wrap(runnable));
    }

    @Override
    public <T> @NonNull List<Future<T>> invokeAll(Collection<? extends Callable<T>> callables) throws InterruptedException {
        return this.delegate.invokeAll(this.taskWrapper.wrap(callables));
    }

    @Override
    public <T> @NonNull List<Future<T>> invokeAll(Collection<? extends Callable<T>> callables, long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.invokeAll(this.taskWrapper.wrap(callables), timeout, unit);
    }

    @Override
    public <T> @NonNull T invokeAny(Collection<? extends Callable<T>> callables) throws InterruptedException, ExecutionException {
        return this.delegate.invokeAny(this.taskWrapper.wrap(callables));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> callables, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate.invokeAny(this.taskWrapper.wrap(callables), timeout, unit);
    }

    @Override
    public void execute(Runnable runnable) {
        this.delegate.execute(this.taskWrapper.wrap(runnable));
    }
}

