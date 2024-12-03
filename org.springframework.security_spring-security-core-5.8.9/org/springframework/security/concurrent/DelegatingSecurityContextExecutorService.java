/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import org.springframework.security.core.context.SecurityContext;

public class DelegatingSecurityContextExecutorService
extends DelegatingSecurityContextExecutor
implements ExecutorService {
    public DelegatingSecurityContextExecutorService(ExecutorService delegateExecutorService, SecurityContext securityContext) {
        super(delegateExecutorService, securityContext);
    }

    public DelegatingSecurityContextExecutorService(ExecutorService delegate) {
        this(delegate, null);
    }

    @Override
    public final void shutdown() {
        this.getDelegate().shutdown();
    }

    @Override
    public final List<Runnable> shutdownNow() {
        return this.getDelegate().shutdownNow();
    }

    @Override
    public final boolean isShutdown() {
        return this.getDelegate().isShutdown();
    }

    @Override
    public final boolean isTerminated() {
        return this.getDelegate().isTerminated();
    }

    @Override
    public final boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.getDelegate().awaitTermination(timeout, unit);
    }

    @Override
    public final <T> Future<T> submit(Callable<T> task) {
        return this.getDelegate().submit(this.wrap(task));
    }

    @Override
    public final <T> Future<T> submit(Runnable task, T result) {
        return this.getDelegate().submit(this.wrap(task), result);
    }

    @Override
    public final Future<?> submit(Runnable task) {
        return this.getDelegate().submit(this.wrap(task));
    }

    public final List invokeAll(Collection tasks) throws InterruptedException {
        tasks = this.createTasks(tasks);
        return this.getDelegate().invokeAll(tasks);
    }

    public final List invokeAll(Collection tasks, long timeout, TimeUnit unit) throws InterruptedException {
        tasks = this.createTasks(tasks);
        return this.getDelegate().invokeAll(tasks, timeout, unit);
    }

    public final Object invokeAny(Collection tasks) throws InterruptedException, ExecutionException {
        tasks = this.createTasks(tasks);
        return this.getDelegate().invokeAny(tasks);
    }

    public final Object invokeAny(Collection tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        tasks = this.createTasks(tasks);
        return this.getDelegate().invokeAny(tasks, timeout, unit);
    }

    private <T> Collection<Callable<T>> createTasks(Collection<Callable<T>> tasks) {
        if (tasks == null) {
            return null;
        }
        ArrayList<Callable<T>> results = new ArrayList<Callable<T>>(tasks.size());
        for (Callable<T> task : tasks) {
            results.add(this.wrap(task));
        }
        return results;
    }

    private ExecutorService getDelegate() {
        return (ExecutorService)this.getDelegateExecutor();
    }
}

