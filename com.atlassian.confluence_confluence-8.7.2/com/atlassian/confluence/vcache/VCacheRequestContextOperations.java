/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.vcache;

import java.util.concurrent.Callable;

public interface VCacheRequestContextOperations {
    public <T, X extends Throwable> T doInRequestContext(Action<T, X> var1) throws X;

    @Deprecated
    public <T, X extends Throwable> T doInRequestContext(String var1, Action<T, X> var2) throws X;

    default public void doInRequestContext(Runnable action) {
        this.doInRequestContext(() -> {
            action.run();
            return null;
        });
    }

    default public <T> Callable<T> withRequestContext(Callable<T> task) {
        return () -> this.doInRequestContext(task::call);
    }

    default public Runnable withRequestContext(Runnable task) {
        return () -> this.doInRequestContext(task);
    }

    @FunctionalInterface
    public static interface Action<T, X extends Throwable> {
        public T perform() throws X;
    }
}

