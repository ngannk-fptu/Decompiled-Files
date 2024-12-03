/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.executionservice.impl;

import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.spi.impl.executionservice.impl.DelegateAndSkipOnConcurrentExecutionDecorator;
import com.hazelcast.spi.impl.executionservice.impl.DelegatingCallableTaskDecorator;
import com.hazelcast.spi.impl.executionservice.impl.DelegatingTaskDecorator;
import com.hazelcast.util.Preconditions;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class DelegatingTaskScheduler
implements TaskScheduler {
    private final ScheduledExecutorService scheduledExecutorService;
    private final ExecutorService executor;

    public DelegatingTaskScheduler(ScheduledExecutorService scheduledExecutorService, ExecutorService executor) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.executor = executor;
    }

    @Override
    public void execute(Runnable command) {
        this.executor.execute(command);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        Preconditions.checkNotNull(command);
        DelegatingTaskDecorator decoratedTask = new DelegatingTaskDecorator(command, this.executor);
        return this.scheduledExecutorService.schedule(decoratedTask, delay, unit);
    }

    @Override
    public <V> ScheduledFuture<Future<V>> schedule(Callable<V> command, long delay, TimeUnit unit) {
        Preconditions.checkNotNull(command);
        DelegatingCallableTaskDecorator<V> decoratedTask = new DelegatingCallableTaskDecorator<V>(command, this.executor);
        return this.scheduledExecutorService.schedule(decoratedTask, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithRepetition(Runnable command, long initialDelay, long period, TimeUnit unit) {
        Preconditions.checkNotNull(command);
        DelegateAndSkipOnConcurrentExecutionDecorator decoratedTask = new DelegateAndSkipOnConcurrentExecutionDecorator(command, this.executor);
        return this.scheduledExecutorService.scheduleAtFixedRate(decoratedTask, initialDelay, period, unit);
    }
}

