/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 */
package com.atlassian.fugue.retry;

import com.atlassian.fugue.retry.ExceptionHandler;
import com.atlassian.fugue.retry.ExceptionHandlers;
import com.atlassian.fugue.retry.NoOp;
import com.atlassian.fugue.retry.RetrySupplier;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class RetryFunction<F, T>
implements Function<F, T> {
    private final Function<F, T> function;
    private final int tries;
    private final ExceptionHandler handler;
    private final Runnable beforeRetry;

    public RetryFunction(Function<F, T> function, int tries) {
        this(function, tries, ExceptionHandlers.ignoreExceptionHandler());
    }

    public RetryFunction(Function<F, T> function, int tries, ExceptionHandler handler) {
        this(function, tries, handler, new NoOp());
    }

    public RetryFunction(Function<F, T> function, int tries, ExceptionHandler handler, Runnable beforeRetry) {
        this.function = (Function)Preconditions.checkNotNull(function);
        this.handler = (ExceptionHandler)Preconditions.checkNotNull((Object)handler);
        Preconditions.checkArgument((tries >= 0 ? 1 : 0) != 0, (Object)"Tries must not be negative");
        this.tries = tries;
        this.beforeRetry = (Runnable)Preconditions.checkNotNull((Object)beforeRetry);
    }

    public T apply(F parameter) {
        return new RetrySupplier(Suppliers.compose(this.function, (Supplier)Suppliers.ofInstance(parameter)), this.tries, this.handler, this.beforeRetry).get();
    }
}

