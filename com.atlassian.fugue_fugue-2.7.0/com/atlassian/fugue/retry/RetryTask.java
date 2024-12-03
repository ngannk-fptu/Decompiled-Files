/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 */
package com.atlassian.fugue.retry;

import com.atlassian.fugue.retry.ExceptionHandler;
import com.atlassian.fugue.retry.ExceptionHandlers;
import com.atlassian.fugue.retry.NoOp;
import com.atlassian.fugue.retry.RetrySupplier;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

public class RetryTask
implements Runnable {
    private RetrySupplier<?> retrySupplier;

    public RetryTask(Runnable task, int tries) {
        this(task, tries, ExceptionHandlers.ignoreExceptionHandler());
    }

    public RetryTask(Runnable task, int tries, ExceptionHandler handler) {
        this(task, tries, handler, new NoOp());
    }

    public RetryTask(final Runnable task, int tries, ExceptionHandler handler, Runnable beforeRetry) {
        Preconditions.checkNotNull((Object)task, (Object)"task");
        this.retrySupplier = new RetrySupplier<Object>(new Supplier<Object>(){

            public Object get() {
                task.run();
                return null;
            }
        }, tries, handler, beforeRetry);
    }

    @Override
    public void run() {
        this.retrySupplier.get();
    }
}

