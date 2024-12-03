/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class MapValueCollection
implements IdentifiedDataSerializable {
    private Collection<Data> values;

    public MapValueCollection(Collection<Data> values) {
        this.values = values;
    }

    public MapValueCollection() {
    }

    public Collection<Data> getValues() {
        return this.values;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        int size = this.values.size();
        out.writeInt(size);
        for (Data o : this.values) {
            out.writeData(o);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.values = new ArrayList<Data>(size);
        for (int i = 0; i < size; ++i) {
            Data data = in.readData();
            this.values.add(data);
        }
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 6;
    }
}

