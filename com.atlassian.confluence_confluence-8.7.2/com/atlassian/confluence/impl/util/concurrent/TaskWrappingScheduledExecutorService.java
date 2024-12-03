/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.util.concurrent;

import com.atlassian.confluence.impl.util.concurrent.TaskWrapper;
import com.atlassian.confluence.impl.util.concurrent.TaskWrappingExecutorService;
import com.google.common.base.Preconditions;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;

class TaskWrappingScheduledExecutorService
extends TaskWrappingExecutorService
implements ScheduledExecutorService {
    private final ScheduledExecutorService delegate;

    public TaskWrappingScheduledExecutorService(ScheduledExecutorService delegate, TaskWrapper taskWrapper) {
        super(delegate, taskWrapper);
        this.delegate = (ScheduledExecutorService)Preconditions.checkNotNull((Object)delegate);
    }

    @Override
    public @NonNull ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return this.delegate.schedule(this.taskWrapper.wrap(command), delay, unit);
    }

    @Override
    public <V> @NonNull ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return this.delegate.schedule(this.taskWrapper.wrap(callable), delay, unit);
    }

    @Override
    public @NonNull ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.delegate.scheduleAtFixedRate(this.taskWrapper.wrap(command), initialDelay, period, unit);
    }

    @Override
    public @NonNull ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return this.delegate.scheduleWithFixedDelay(this.taskWrapper.wrap(command), initialDelay, delay, unit);
    }
}

