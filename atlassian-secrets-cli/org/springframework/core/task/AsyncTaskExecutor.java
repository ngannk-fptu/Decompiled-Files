/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.springframework.core.task.TaskExecutor;

public interface AsyncTaskExecutor
extends TaskExecutor {
    public static final long TIMEOUT_IMMEDIATE = 0L;
    public static final long TIMEOUT_INDEFINITE = Long.MAX_VALUE;

    public void execute(Runnable var1, long var2);

    public Future<?> submit(Runnable var1);

    public <T> Future<T> submit(Callable<T> var1);
}

