/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue.retry;

import io.atlassian.fugue.retry.ExceptionHandler;
import io.atlassian.fugue.retry.ExceptionHandlers;
import io.atlassian.fugue.retry.NoOp;
import io.atlassian.fugue.retry.RetrySupplier;
import java.util.Objects;

public class RetryTask
implements Runnable {
    private RetrySupplier<?> retrySupplier;

    public RetryTask(Runnable task, int tries) {
        this(task, tries, ExceptionHandlers.ignoreExceptionHandler());
    }

    public RetryTask(Runnable task, int tries, ExceptionHandler handler) {
        this(task, tries, handler, new NoOp());
    }

    public RetryTask(Runnable task, int tries, ExceptionHandler handler, Runnable beforeRetry) {
        Objects.requireNonNull(task, "task");
        this.retrySupplier = new RetrySupplier<Object>(() -> {
            task.run();
            return null;
        }, tries, handler, beforeRetry);
    }

    @Override
    public void run() {
        this.retrySupplier.get();
    }
}

