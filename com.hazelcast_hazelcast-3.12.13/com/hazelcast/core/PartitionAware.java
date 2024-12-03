/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

public interface PartitionAware<T> {
    public T getPartitionKey();
}

