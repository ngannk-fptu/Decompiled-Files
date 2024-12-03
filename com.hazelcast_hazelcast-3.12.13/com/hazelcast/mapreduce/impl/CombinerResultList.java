/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl;

import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class CombinerResultList<E>
extends ArrayList<E>
implements IdentifiedDataSerializable {
    public CombinerResultList() {
    }

    public CombinerResultList(Collection<? extends E> c) {
        super(c);
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 23;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        int size = this.size();
        out.writeInt(size);
        for (int i = 0; i < size; ++i) {
            out.writeObject(this.get(i));
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            this.add(i, in.readObject());
        }
    }
}

