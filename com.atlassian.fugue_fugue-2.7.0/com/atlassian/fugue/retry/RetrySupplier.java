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
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

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
        Preconditions.checkNotNull(supplier);
        Preconditions.checkArgument((tries > 0 ? 1 : 0) != 0, (Object)"Tries must be strictly positive");
        Preconditions.checkNotNull((Object)handler);
        this.beforeRetry = beforeRetry;
        this.supplier = supplier;
        this.tries = tries;
        this.handler = handler;
    }

    public T get() {
        RuntimeException ex = null;
        for (int i = 0; i < this.tries; ++i) {
            try {
                return (T)this.supplier.get();
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

