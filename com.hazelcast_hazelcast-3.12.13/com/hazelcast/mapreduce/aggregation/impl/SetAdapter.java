/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.aggregation.impl;

import com.hazelcast.mapreduce.aggregation.impl.AggregationsDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SetAdapter<E>
extends HashSet<E>
implements IdentifiedDataSerializable {
    @Override
    public int getFactoryId() {
        return AggregationsDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.size());
        for (Object element : this) {
            out.writeObject(element);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        Set set = SetUtil.createHashSet(size);
        for (int i = 0; i < size; ++i) {
            set.add(in.readObject());
        }
        this.addAll(set);
    }
}

