/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Suppliers
 */
package io.atlassian.fugue.retry;

import io.atlassian.fugue.Suppliers;
import io.atlassian.fugue.retry.ExceptionHandler;
import io.atlassian.fugue.retry.ExceptionHandlers;
import io.atlassian.fugue.retry.NoOp;
import io.atlassian.fugue.retry.RetrySupplier;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

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
        this.function = Objects.requireNonNull(function);
        this.handler = Objects.requireNonNull(handler);
        if (tries < 0) {
            throw new IllegalArgumentException("Tries must not be negative");
        }
        this.tries = tries;
        this.beforeRetry = Objects.requireNonNull(beforeRetry);
    }

    @Override
    public T apply(F parameter) {
        return new RetrySupplier(Suppliers.compose(this.function, (Supplier)Suppliers.ofInstance(parameter)), this.tries, this.handler, this.beforeRetry).get();
    }
}

