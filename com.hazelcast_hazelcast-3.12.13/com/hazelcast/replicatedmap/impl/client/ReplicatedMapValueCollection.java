/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.client;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapPortableHook;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ReplicatedMapValueCollection
implements Portable {
    private Collection<Data> values;

    ReplicatedMapValueCollection() {
    }

    public ReplicatedMapValueCollection(Collection<Data> values) {
        this.values = values;
    }

    public Collection<Data> getValues() {
        return this.values;
    }

    @Override
    public void writePortable(PortableWriter writer) throws IOException {
        writer.writeInt("size", this.values.size());
        ObjectDataOutput out = writer.getRawDataOutput();
        for (Data value : this.values) {
            out.writeData(value);
        }
    }

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        int size = reader.readInt("size");
        ObjectDataInput in = reader.getRawDataInput();
        this.values = new ArrayList<Data>(size);
        for (int i = 0; i < size; ++i) {
            this.values.add(in.readData());
        }
    }

    @Override
    public int getFactoryId() {
        return ReplicatedMapPortableHook.F_ID;
    }

    @Override
    public int getClassId() {
        return 14;
    }
}

