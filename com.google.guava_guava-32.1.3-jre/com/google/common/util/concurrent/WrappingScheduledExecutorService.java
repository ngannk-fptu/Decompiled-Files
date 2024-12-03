/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.util.concurrent.ElementTypesAreNonnullByDefault;
import com.google.common.util.concurrent.WrappingExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
abstract class WrappingScheduledExecutorService
extends WrappingExecutorService
implements ScheduledExecutorService {
    final ScheduledExecutorService delegate;

    protected WrappingScheduledExecutorService(ScheduledExecutorService delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public final ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return this.delegate.schedule(this.wrapTask(command), delay, unit);
    }

    @Override
    public final <V> ScheduledFuture<V> schedule(Callable<V> task, long delay, TimeUnit unit) {
        return this.delegate.schedule(this.wrapTask(task), delay, unit);
    }

    @Override
    public final ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.delegate.scheduleAtFixedRate(this.wrapTask(command), initialDelay, period, unit);
    }

    @Override
    public final ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return this.delegate.scheduleWithFixedDelay(this.wrapTask(command), initialDelay, delay, unit);
    }
}

