/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public interface AsynchronousTaskExecutor {
    public <T> Future<T> submit(Callable<T> var1);

    public ExecutorService getExecutorService();
}

