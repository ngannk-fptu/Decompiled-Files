/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Supplier;
import java.util.concurrent.Callable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public interface LockManager<T> {
    public <R> R withLock(T var1, Supplier<R> var2);

    public <R> R withLock(T var1, Callable<R> var2) throws Exception;

    public void withLock(T var1, Runnable var2);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface ReadWrite<T> {
        public LockManager<T> read();

        public LockManager<T> write();
    }
}

