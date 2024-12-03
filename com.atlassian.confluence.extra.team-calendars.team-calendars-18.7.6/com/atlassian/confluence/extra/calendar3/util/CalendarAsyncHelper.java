/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.extra.calendar3.util.AsynchronousTaskExecutor;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class CalendarAsyncHelper {
    private final AsynchronousTaskExecutor asynchronousTaskExecutor;
    private final TransactionTemplate transactionTemplate;

    public CalendarAsyncHelper(AsynchronousTaskExecutor asynchronousTaskExecutor, TransactionTemplate transactionTemplate) {
        this.asynchronousTaskExecutor = asynchronousTaskExecutor;
        this.transactionTemplate = transactionTemplate;
    }

    public <T> Future<T> doAsync(Callable<T> callable) {
        return this.asynchronousTaskExecutor.submit(() -> callable.call());
    }

    public <T> Future<T> doAsyncWithTransaction(Callable<T> runnable) {
        ArrayList innerExceptions = new ArrayList();
        return this.asynchronousTaskExecutor.submit(() -> {
            Object returnValue = this.transactionTemplate.execute(() -> {
                try {
                    return runnable.call();
                }
                catch (Exception exception) {
                    innerExceptions.add(exception);
                    return null;
                }
            });
            if (innerExceptions.size() > 0) {
                throw (Exception)innerExceptions.get(0);
            }
            return returnValue;
        });
    }
}

