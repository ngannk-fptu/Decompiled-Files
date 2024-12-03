/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.core.ICompletableFuture;

@Deprecated
public interface JobCompletableFuture<V>
extends ICompletableFuture<V> {
    public String getJobId();
}

