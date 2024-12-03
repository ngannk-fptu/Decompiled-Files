/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Supplier
 */
package com.atlassian.fugue.retry;

import com.atlassian.fugue.retry.BeforeRetryExponentialBackoffTask;
import com.atlassian.fugue.retry.ExceptionHandler;
import com.atlassian.fugue.retry.ExceptionHandlers;
import com.atlassian.fugue.retry.RetryFunction;
import com.atlassian.fugue.retry.RetrySupplier;
import com.atlassian.fugue.retry.RetryTask;
import com.google.common.base.Function;
import com.google.common.base.Supplier;

public class RetryFactory {
    private RetryFactory() {
        throw new AssertionError((Object)"This class is non-instantiable.");
    }

    public static Runnable create(Runnable task, int tries) {
        return RetryFactory.create(task, tries, ExceptionHandlers.ignoreExceptionHandler());
    }

    public static Runnable create(Runnable task, int tries, ExceptionHandler handler) {
        return new RetryTask(task, tries, handler);
    }

    public static Runnable create(Runnable task, int tries, ExceptionHandler handler, long backoff) {
        return new RetryTask(task, tries, handler, new BeforeRetryExponentialBackoffTask(backoff));
    }

    public static <A> Supplier<A> create(Supplier<A> supplier, int tries) {
        return RetryFactory.create(supplier, tries, ExceptionHandlers.ignoreExceptionHandler());
    }

    public static <A> Supplier<A> create(Supplier<A> supplier, int tries, ExceptionHandler handler) {
        return new RetrySupplier<A>(supplier, tries, handler);
    }

    public static <A> Supplier<A> create(Supplier<A> supplier, int tries, ExceptionHandler handler, long backoff) {
        return new RetrySupplier<A>(supplier, tries, handler, new BeforeRetryExponentialBackoffTask(backoff));
    }

    public static <A, B> Function<A, B> create(Function<A, B> function, int tries) {
        return RetryFactory.create(function, tries, ExceptionHandlers.ignoreExceptionHandler());
    }

    public static <A, B> Function<A, B> create(Function<A, B> function, int tries, ExceptionHandler handler) {
        return RetryFactory.create(function, tries, handler, 0L);
    }

    public static <A, B> Function<A, B> create(Function<A, B> function, int tries, ExceptionHandler handler, long backoff) {
        return new RetryFunction<A, B>(function, tries, handler, new BeforeRetryExponentialBackoffTask(backoff));
    }
}

