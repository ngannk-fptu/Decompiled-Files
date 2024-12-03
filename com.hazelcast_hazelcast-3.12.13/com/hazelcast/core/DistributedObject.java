/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

public interface DistributedObject {
    public String getPartitionKey();

    public String getName();

    public String getServiceName();

    public void destroy();
}

