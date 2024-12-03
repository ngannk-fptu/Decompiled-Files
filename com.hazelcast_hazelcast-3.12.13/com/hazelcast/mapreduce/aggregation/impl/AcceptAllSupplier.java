/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.aggregation.impl;

import com.hazelcast.mapreduce.aggregation.PropertyExtractor;
import com.hazelcast.mapreduce.aggregation.Supplier;
import com.hazelcast.mapreduce.aggregation.impl.AggregationsDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.Map;

public class AcceptAllSupplier<KeyIn, ValueIn, ValueOut>
extends Supplier<KeyIn, ValueIn, ValueOut>
implements IdentifiedDataSerializable {
    private PropertyExtractor<ValueIn, ValueOut> propertyExtractor;

    AcceptAllSupplier() {
    }

    public AcceptAllSupplier(PropertyExtractor<ValueIn, ValueOut> propertyExtractor) {
        this.propertyExtractor = propertyExtractor;
    }

    @Override
    public ValueOut apply(Map.Entry<KeyIn, ValueIn> entry) {
        ValueIn value = entry.getValue();
        return (ValueOut)(this.propertyExtractor != null ? this.propertyExtractor.extract(value) : value);
    }

    @Override
    public int getFactoryId() {
        return AggregationsDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.propertyExtractor);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.propertyExtractor = (PropertyExtractor)in.readObject();
    }
}

