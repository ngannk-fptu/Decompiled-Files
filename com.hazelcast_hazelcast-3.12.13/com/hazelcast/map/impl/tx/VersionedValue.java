/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.tx;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class VersionedValue
implements IdentifiedDataSerializable {
    long version;
    Data value;

    public VersionedValue(Data value, long version) {
        this.value = value;
        this.version = version;
    }

    public VersionedValue() {
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.version);
        boolean isNull = this.value == null;
        out.writeBoolean(isNull);
        if (!isNull) {
            out.writeData(this.value);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.version = in.readLong();
        boolean isNull = in.readBoolean();
        if (!isNull) {
            this.value = in.readData();
        }
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 112;
    }
}

