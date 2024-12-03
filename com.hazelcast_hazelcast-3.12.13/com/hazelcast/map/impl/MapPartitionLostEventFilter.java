/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.EventFilter;
import java.io.IOException;

public class MapPartitionLostEventFilter
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
        return obj instanceof MapPartitionLostEventFilter;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "MapPartitionLostEventFilter{}";
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 93;
    }
}

