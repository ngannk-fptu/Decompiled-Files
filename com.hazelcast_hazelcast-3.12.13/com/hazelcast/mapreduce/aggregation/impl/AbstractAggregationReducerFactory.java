/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.aggregation.impl;

import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.aggregation.impl.AggregationsDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

@BinaryInterface
abstract class AbstractAggregationReducerFactory<KeyIn, ValueIn, ValueOut>
implements ReducerFactory<KeyIn, ValueIn, ValueOut>,
IdentifiedDataSerializable {
    AbstractAggregationReducerFactory() {
    }

    @Override
    public int getFactoryId() {
        return AggregationsDataSerializerHook.F_ID;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
    }
}

