/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import java.io.IOException;

public final class TrueEventFilter
implements EventFilter,
IdentifiedDataSerializable {
    public static final TrueEventFilter INSTANCE = new TrueEventFilter();

    @Override
    public boolean eval(Object arg) {
        return true;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
    }

    public boolean equals(Object obj) {
        return obj instanceof TrueEventFilter;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "TrueEventFilter{}";
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 17;
    }
}

