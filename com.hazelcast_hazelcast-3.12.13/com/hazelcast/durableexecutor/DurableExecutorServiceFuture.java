/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor;

import com.hazelcast.core.ICompletableFuture;

public interface DurableExecutorServiceFuture<V>
extends ICompletableFuture<V> {
    public long getTaskId();
}

