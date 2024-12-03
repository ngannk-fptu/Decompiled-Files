/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.ExecutionCallback;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public interface ICompletableFuture<V>
extends Future<V> {
    public void andThen(ExecutionCallback<V> var1);

    public void andThen(ExecutionCallback<V> var1, Executor var2);
}

