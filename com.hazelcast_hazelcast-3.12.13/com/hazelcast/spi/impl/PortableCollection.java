/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.spi.impl.SpiPortableHook;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class PortableCollection
implements Portable {
    private Collection<Data> collection;

    public PortableCollection() {
    }

    public PortableCollection(Collection<Data> collection) {
        this.collection = collection;
    }

    public Collection<Data> getCollection() {
        return this.collection;
    }

    @Override
    public int getFactoryId() {
        return SpiPortableHook.ID;
    }

    @Override
    public int getClassId() {
        return 2;
    }

    @Override
    public void writePortable(PortableWriter writer) throws IOException {
        writer.writeBoolean("l", this.collection instanceof List);
        if (this.collection == null) {
            writer.writeInt("s", -1);
            return;
        }
        writer.writeInt("s", this.collection.size());
        ObjectDataOutput out = writer.getRawDataOutput();
        for (Data data : this.collection) {
            out.writeData(data);
        }
    }

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        boolean list = reader.readBoolean("l");
        int size = reader.readInt("s");
        if (size == -1) {
            return;
        }
        this.collection = list ? new ArrayList<Data>(size) : SetUtil.createHashSet(size);
        ObjectDataInput in = reader.getRawDataInput();
        for (int i = 0; i < size; ++i) {
            Data data = in.readData();
            this.collection.add(data);
        }
    }
}

