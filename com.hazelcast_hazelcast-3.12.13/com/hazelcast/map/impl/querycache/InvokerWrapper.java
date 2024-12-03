/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache;

import com.hazelcast.nio.Address;
import com.hazelcast.spi.Operation;
import java.util.concurrent.Future;

public interface InvokerWrapper {
    public Future invokeOnPartitionOwner(Object var1, int var2);

    public Object invokeOnAllPartitions(Object var1) throws Exception;

    public Future invokeOnTarget(Object var1, Address var2);

    public Object invoke(Object var1);

    public void executeOperation(Operation var1);
}

