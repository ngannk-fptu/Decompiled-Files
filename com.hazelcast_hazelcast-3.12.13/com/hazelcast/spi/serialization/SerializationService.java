/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.serialization;

import com.hazelcast.core.ManagedContext;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.nio.serialization.Data;

public interface SerializationService {
    public <B extends Data> B toData(Object var1);

    public <B extends Data> B toData(Object var1, PartitioningStrategy var2);

    public <T> T toObject(Object var1);

    public <T> T toObject(Object var1, Class var2);

    public ManagedContext getManagedContext();
}

