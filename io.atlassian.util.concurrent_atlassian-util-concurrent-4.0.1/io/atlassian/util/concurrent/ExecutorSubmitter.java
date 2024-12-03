/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.Promise;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public interface ExecutorSubmitter
extends Executor {
    public <T> Promise<T> submit(Callable<T> var1);

    public <T> Promise<T> submitSupplier(Supplier<T> var1);
}

