/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner.callables;

import com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner.callables.BatchCallable;
import com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner.callables.RetryCallable;
import com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner.callables.TransactionalCallable;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;
import javax.annotation.Nullable;

public class CallableBuilder<T> {
    private Callable<T> innerCallable;
    private BatchCallable<T> batchCallable;

    private CallableBuilder() {
    }

    public static <T> CallableBuilder<T> builder() {
        return new CallableBuilder<T>();
    }

    public CallableBuilder<T> withAction(Callable<T> action) {
        Objects.nonNull(action);
        this.innerCallable = action;
        return this;
    }

    public CallableBuilder<T> withTransaction(TransactionalHostContextAccessor hostContextAccessor) {
        Objects.nonNull(this.innerCallable);
        Objects.nonNull(hostContextAccessor);
        TransactionalCallable<T> transactionalCallable = new TransactionalCallable<T>(hostContextAccessor, this.innerCallable);
        this.innerCallable = transactionalCallable;
        return this;
    }

    public CallableBuilder<T> withRetry(int numberOfRetry, @Nullable Runnable failedRetryCallback) {
        Objects.nonNull(this.innerCallable);
        RetryCallable<T> retryCallable = new RetryCallable<T>(numberOfRetry, failedRetryCallback, this.innerCallable);
        this.innerCallable = retryCallable;
        return this;
    }

    public CallableBuilder<T> withBatching(int batchSize, long totalElement) {
        Preconditions.checkArgument((batchSize > 0 ? 1 : 0) != 0);
        Preconditions.checkArgument((totalElement > 0L ? 1 : 0) != 0);
        Objects.nonNull(this.innerCallable);
        BatchCallable<T> batchCallable = new BatchCallable<T>(batchSize, totalElement, this.innerCallable);
        this.batchCallable = batchCallable;
        return this;
    }

    public Function<Void, Collection<T>> getBatchCallable() {
        Objects.nonNull(this.batchCallable);
        return this.batchCallable;
    }

    public Callable<T> getCallable() {
        Objects.nonNull(this.innerCallable);
        return this.innerCallable;
    }
}

