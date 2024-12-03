/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice;

public interface PartitionTaskFactory<T> {
    public T create(int var1);
}

