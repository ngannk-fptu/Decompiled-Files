/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.aggregation.impl;

import com.hazelcast.mapreduce.aggregation.impl.AggregationsDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class AvgTuple<F, S>
implements IdentifiedDataSerializable {
    private F first;
    private S second;

    AvgTuple() {
    }

    public AvgTuple(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return this.first;
    }

    public S getSecond() {
        return this.second;
    }

    @Override
    public int getFactoryId() {
        return AggregationsDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 54;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.first);
        out.writeObject(this.second);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.first = in.readObject();
        this.second = in.readObject();
    }
}

