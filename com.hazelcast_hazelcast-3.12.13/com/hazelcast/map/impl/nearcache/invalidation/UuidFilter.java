/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.nearcache.invalidation;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.EventFilter;
import java.io.IOException;

public class UuidFilter
implements EventFilter,
IdentifiedDataSerializable {
    private String uuid;

    public UuidFilter() {
    }

    public UuidFilter(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean eval(Object suppliedUuid) {
        assert (suppliedUuid instanceof String);
        return this.uuid.equals(suppliedUuid);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.uuid);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.uuid = in.readUTF();
    }

    public String toString() {
        return "UuidFilter{uuid='" + this.uuid + '\'' + '}';
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 109;
    }
}

