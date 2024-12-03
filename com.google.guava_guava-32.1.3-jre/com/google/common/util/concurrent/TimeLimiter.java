/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.DoNotMock
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.util.concurrent.ElementTypesAreNonnullByDefault;
import com.google.common.util.concurrent.Internal;
import com.google.common.util.concurrent.ParametricNullness;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotMock;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@DoNotMock(value="Use FakeTimeLimiter")
@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public interface TimeLimiter {
    public <T> T newProxy(T var1, Class<T> var2, long var3, TimeUnit var5);

    default public <T> T newProxy(T target, Class<T> interfaceType, Duration timeout) {
        return this.newProxy(target, interfaceType, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
    }

    @ParametricNullness
    @CanIgnoreReturnValue
    public <T> T callWithTimeout(Callable<T> var1, long var2, TimeUnit var4) throws TimeoutException, InterruptedException, ExecutionException;

    @ParametricNullness
    @CanIgnoreReturnValue
    default public <T> T callWithTimeout(Callable<T> callable, Duration timeout) throws TimeoutException, InterruptedException, ExecutionException {
        return this.callWithTimeout(callable, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
    }

    @ParametricNullness
    @CanIgnoreReturnValue
    public <T> T callUninterruptiblyWithTimeout(Callable<T> var1, long var2, TimeUnit var4) throws TimeoutException, ExecutionException;

    @ParametricNullness
    @CanIgnoreReturnValue
    default public <T> T callUninterruptiblyWithTimeout(Callable<T> callable, Duration timeout) throws TimeoutException, ExecutionException {
        return this.callUninterruptiblyWithTimeout(callable, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
    }

    public void runWithTimeout(Runnable var1, long var2, TimeUnit var4) throws TimeoutException, InterruptedException;

    default public void runWithTimeout(Runnable runnable, Duration timeout) throws TimeoutException, InterruptedException {
        this.runWithTimeout(runnable, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
    }

    public void runUninterruptiblyWithTimeout(Runnable var1, long var2, TimeUnit var4) throws TimeoutException;

    default public void runUninterruptiblyWithTimeout(Runnable runnable, Duration timeout) throws TimeoutException {
        this.runUninterruptiblyWithTimeout(runnable, Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
    }
}

