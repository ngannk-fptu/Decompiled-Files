/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue.retry;

import io.atlassian.fugue.retry.ExceptionHandler;
import io.atlassian.fugue.retry.ExceptionHandlers;
import io.atlassian.fugue.retry.NoOp;
import java.util.Objects;
import java.util.function.Supplier;

public class RetrySupplier<T>
implements Supplier<T> {
    private final Supplier<T> supplier;
    private final int tries;
    private final ExceptionHandler handler;
    private final Runnable beforeRetry;

    public RetrySupplier(Supplier<T> supplier, int tries) {
        this(supplier, tries, ExceptionHandlers.ignoreExceptionHandler());
    }

    public RetrySupplier(Supplier<T> supplier, int tries, ExceptionHandler handler) {
        this(supplier, tries, handler, new NoOp());
    }

    public RetrySupplier(Supplier<T> supplier, int tries, ExceptionHandler handler, Runnable beforeRetry) {
        Objects.requireNonNull(supplier);
        if (tries <= 0) {
            throw new IllegalArgumentException("Tries must be strictly positive");
        }
        Objects.requireNonNull(handler);
        this.beforeRetry = beforeRetry;
        this.supplier = supplier;
        this.tries = tries;
        this.handler = handler;
    }

    @Override
    public T get() {
        RuntimeException ex = null;
        for (int i = 0; i < this.tries; ++i) {
            try {
                return this.supplier.get();
            }
            catch (RuntimeException e) {
                this.handler.handle(e);
                ex = e;
                if (i + 1 >= this.tries) continue;
                this.beforeRetry.run();
                continue;
            }
        }
        throw ex;
    }
}

