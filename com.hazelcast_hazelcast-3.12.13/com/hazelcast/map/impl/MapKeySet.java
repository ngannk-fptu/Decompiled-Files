/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Set;

public class MapKeySet
implements IdentifiedDataSerializable {
    private Set<Data> keySet;

    public MapKeySet(Set<Data> keySet) {
        this.keySet = keySet;
    }

    public MapKeySet() {
    }

    public Set<Data> getKeySet() {
        return this.keySet;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        int size = this.keySet.size();
        out.writeInt(size);
        for (Data o : this.keySet) {
            out.writeData(o);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.keySet = SetUtil.createHashSet(size);
        for (int i = 0; i < size; ++i) {
            Data data = in.readData();
            this.keySet.add(data);
        }
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 5;
    }
}

