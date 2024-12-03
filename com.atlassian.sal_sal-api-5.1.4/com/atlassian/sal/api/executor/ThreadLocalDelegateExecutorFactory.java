/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public interface ThreadLocalDelegateExecutorFactory {
    public Executor createExecutor(Executor var1);

    public ExecutorService createExecutorService(ExecutorService var1);

    public ScheduledExecutorService createScheduledExecutorService(ScheduledExecutorService var1);

    public Runnable createRunnable(Runnable var1);

    public <T> Callable<T> createCallable(Callable<T> var1);
}

