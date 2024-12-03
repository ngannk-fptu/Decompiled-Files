/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.aggregation.impl;

import com.hazelcast.mapreduce.aggregation.Supplier;
import com.hazelcast.mapreduce.aggregation.impl.AggregationsDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.Predicate;
import java.io.IOException;
import java.util.Map;

public class PredicateSupplier<KeyIn, ValueIn, ValueOut>
extends Supplier<KeyIn, ValueIn, ValueOut>
implements IdentifiedDataSerializable {
    private Predicate<KeyIn, ValueIn> predicate;
    private Supplier<KeyIn, ValueIn, ValueOut> chainedSupplier;

    PredicateSupplier() {
    }

    public PredicateSupplier(Predicate<KeyIn, ValueIn> predicate) {
        this(predicate, null);
    }

    public PredicateSupplier(Predicate<KeyIn, ValueIn> predicate, Supplier<KeyIn, ValueIn, ValueOut> chainedSupplier) {
        this.predicate = predicate;
        this.chainedSupplier = chainedSupplier;
    }

    @Override
    public ValueOut apply(Map.Entry<KeyIn, ValueIn> entry) {
        ValueIn value;
        if (this.predicate.apply(entry) && (value = entry.getValue()) != null) {
            return (ValueOut)(this.chainedSupplier != null ? this.chainedSupplier.apply(entry) : value);
        }
        return null;
    }

    @Override
    public int getFactoryId() {
        return AggregationsDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 53;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.predicate);
        out.writeObject(this.chainedSupplier);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.predicate = (Predicate)in.readObject();
        this.chainedSupplier = (Supplier)in.readObject();
    }
}

