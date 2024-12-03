/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.NotNull;
import com.atlassian.util.concurrent.Supplier;
import java.util.concurrent.Callable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ManagedLock {
    public <R> R withLock(@NotNull Callable<R> var1) throws Exception;

    public <R> R withLock(@NotNull Supplier<R> var1);

    public void withLock(@NotNull Runnable var1);

    public static interface ReadWrite {
        public ManagedLock read();

        public ManagedLock write();
    }
}

