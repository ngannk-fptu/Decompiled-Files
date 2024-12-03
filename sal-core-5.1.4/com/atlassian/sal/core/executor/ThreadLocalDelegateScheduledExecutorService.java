/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.sal.core.executor;

import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.core.executor.ThreadLocalDelegateExecutorService;
import com.google.common.base.Preconditions;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ThreadLocalDelegateScheduledExecutorService
extends ThreadLocalDelegateExecutorService
implements ScheduledExecutorService {
    private final ScheduledExecutorService delegate;
    private final ThreadLocalDelegateExecutorFactory deletegateExecutorFactory;

    public ThreadLocalDelegateScheduledExecutorService(ScheduledExecutorService delegate, ThreadLocalDelegateExecutorFactory deletegateExecutorFactory) {
        super(delegate, deletegateExecutorFactory);
        this.delegate = (ScheduledExecutorService)Preconditions.checkNotNull((Object)delegate);
        this.deletegateExecutorFactory = (ThreadLocalDelegateExecutorFactory)Preconditions.checkNotNull((Object)deletegateExecutorFactory);
    }

    @Override
    @Nonnull
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return this.delegate.schedule(this.deletegateExecutorFactory.createRunnable(command), delay, unit);
    }

    @Override
    @Nonnull
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return this.delegate.schedule(this.deletegateExecutorFactory.createCallable(callable), delay, unit);
    }

    @Override
    @Nonnull
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.delegate.scheduleAtFixedRate(this.deletegateExecutorFactory.createRunnable(command), initialDelay, period, unit);
    }

    @Override
    @Nonnull
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return this.delegate.scheduleWithFixedDelay(this.deletegateExecutorFactory.createRunnable(command), initialDelay, delay, unit);
    }
}

