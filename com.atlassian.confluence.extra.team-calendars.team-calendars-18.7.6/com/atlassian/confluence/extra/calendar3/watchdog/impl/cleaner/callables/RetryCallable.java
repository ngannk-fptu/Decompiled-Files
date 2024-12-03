/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner.callables;

import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RetryCallable<T>
implements Callable<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetryCallable.class);
    private final Runnable failedRetryCallback;
    private final Callable<T> innerCallable;
    private int numOfRetry;

    public RetryCallable(int numOfRetry, Runnable failedRetryCallback, Callable<T> innerCallable) {
        this.numOfRetry = numOfRetry;
        this.failedRetryCallback = failedRetryCallback;
        this.innerCallable = innerCallable;
    }

    @Override
    public T call() throws Exception {
        boolean isSuccess = false;
        Exception lastException = new Exception();
        for (int innerNumOfRetry = this.numOfRetry; !isSuccess && innerNumOfRetry > 0; --innerNumOfRetry) {
            try {
                T result = this.innerCallable.call();
                return result;
            }
            catch (Exception e) {
                LOGGER.error("Exception while executing action", (Throwable)e);
                isSuccess = false;
                lastException = e;
                continue;
            }
        }
        this.failedRetryCallback.run();
        throw lastException;
    }
}

