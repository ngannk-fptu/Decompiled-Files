/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalContextManager
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.google.common.base.Preconditions
 */
package com.atlassian.sal.core.executor;

import com.atlassian.sal.api.executor.ThreadLocalContextManager;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.core.executor.ThreadLocalDelegateCallable;
import com.atlassian.sal.core.executor.ThreadLocalDelegateExecutor;
import com.atlassian.sal.core.executor.ThreadLocalDelegateExecutorService;
import com.atlassian.sal.core.executor.ThreadLocalDelegateRunnable;
import com.atlassian.sal.core.executor.ThreadLocalDelegateScheduledExecutorService;
import com.google.common.base.Preconditions;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class DefaultThreadLocalDelegateExecutorFactory<C>
implements ThreadLocalDelegateExecutorFactory {
    private final ThreadLocalContextManager<C> manager;

    protected DefaultThreadLocalDelegateExecutorFactory(ThreadLocalContextManager<C> manager) {
        this.manager = (ThreadLocalContextManager)Preconditions.checkNotNull(manager);
    }

    public Executor createExecutor(Executor delegate) {
        return new ThreadLocalDelegateExecutor(delegate, this);
    }

    public ExecutorService createExecutorService(ExecutorService delegate) {
        return new ThreadLocalDelegateExecutorService(delegate, this);
    }

    public ScheduledExecutorService createScheduledExecutorService(ScheduledExecutorService delegate) {
        return new ThreadLocalDelegateScheduledExecutorService(delegate, (ThreadLocalDelegateExecutorFactory)this);
    }

    public Runnable createRunnable(Runnable delegate) {
        return new ThreadLocalDelegateRunnable<C>(this.manager, delegate);
    }

    public <T> Callable<T> createCallable(Callable<T> delegate) {
        return new ThreadLocalDelegateCallable<C, T>(this.manager, delegate);
    }
}

