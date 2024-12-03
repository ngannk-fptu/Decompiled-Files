/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cardinality;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.ICompletableFuture;

public interface CardinalityEstimator
extends DistributedObject {
    public void add(Object var1);

    public long estimate();

    public ICompletableFuture<Void> addAsync(Object var1);

    public ICompletableFuture<Long> estimateAsync();
}

