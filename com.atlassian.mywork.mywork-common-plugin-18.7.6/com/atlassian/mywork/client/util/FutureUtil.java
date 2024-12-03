/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.mywork.client.util;

import com.google.common.base.Function;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureUtil {
    public static <A, B> Future<B> map(final Future<A> future, final Function<A, B> f) {
        return new Future<B>(){

            @Override
            public B get() throws InterruptedException, ExecutionException {
                return f.apply(future.get());
            }

            @Override
            public B get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return f.apply(future.get(timeout, unit));
            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return future.cancel(mayInterruptIfRunning);
            }

            @Override
            public boolean isCancelled() {
                return future.isCancelled();
            }

            @Override
            public boolean isDone() {
                return future.isDone();
            }
        };
    }
}

