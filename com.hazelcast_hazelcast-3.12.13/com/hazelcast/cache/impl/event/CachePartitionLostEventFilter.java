/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.event;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.EventFilter;
import java.io.IOException;

public class CachePartitionLostEventFilter
implements EventFilter,
IdentifiedDataSerializable {
    @Override
    public boolean eval(Object arg) {
        return false;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
    }

    public boolean equals(Object obj) {
        return obj instanceof CachePartitionLostEventFilter;
    }

    public int hashCode() {
        return 0;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 43;
    }
}

