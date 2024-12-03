/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.NotNull;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface ManagedLock {
    public <R> R withLock(@NotNull Callable<R> var1) throws Exception;

    public <R> R withLock(@NotNull Supplier<R> var1);

    public void withLock(@NotNull Runnable var1);

    public static interface ReadWrite {
        public ManagedLock read();

        public ManagedLock write();
    }
}

