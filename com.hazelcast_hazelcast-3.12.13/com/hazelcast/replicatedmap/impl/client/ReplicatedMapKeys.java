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
import java.util.List;

public class ReplicatedMapKeys
implements Portable {
    private List<Data> keys;

    public ReplicatedMapKeys() {
        this.keys = new ArrayList<Data>();
    }

    public ReplicatedMapKeys(List<Data> keys) {
        this.keys = keys;
    }

    public List<Data> getKeys() {
        return this.keys;
    }

    @Override
    public void writePortable(PortableWriter writer) throws IOException {
        writer.writeInt("size", this.keys.size());
        ObjectDataOutput out = writer.getRawDataOutput();
        for (Data key : this.keys) {
            out.writeData(key);
        }
    }

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        int size = reader.readInt("size");
        ObjectDataInput in = reader.getRawDataInput();
        this.keys = new ArrayList<Data>(size);
        for (int i = 0; i < size; ++i) {
            this.keys.add(in.readData());
        }
    }

    @Override
    public int getFactoryId() {
        return ReplicatedMapPortableHook.F_ID;
    }

    @Override
    public int getClassId() {
        return 13;
    }
}

