/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.aggregation.impl;

import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.aggregation.Supplier;
import com.hazelcast.mapreduce.aggregation.impl.AggregationsDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.Map;

public class KeyPredicateSupplier<KeyIn, ValueIn, ValueOut>
extends Supplier<KeyIn, ValueIn, ValueOut>
implements IdentifiedDataSerializable {
    private KeyPredicate<KeyIn> keyPredicate;
    private Supplier<KeyIn, ValueIn, ValueOut> chainedSupplier;

    KeyPredicateSupplier() {
    }

    public KeyPredicateSupplier(KeyPredicate<KeyIn> keyPredicate) {
        this(keyPredicate, null);
    }

    public KeyPredicateSupplier(KeyPredicate<KeyIn> keyPredicate, Supplier<KeyIn, ValueIn, ValueOut> chainedSupplier) {
        this.keyPredicate = keyPredicate;
        this.chainedSupplier = chainedSupplier;
    }

    @Override
    public ValueOut apply(Map.Entry<KeyIn, ValueIn> entry) {
        if (this.keyPredicate.evaluate(entry.getKey())) {
            ValueIn value = entry.getValue();
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
        return 52;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.keyPredicate);
        out.writeObject(this.chainedSupplier);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.keyPredicate = (KeyPredicate)in.readObject();
        this.chainedSupplier = (Supplier)in.readObject();
    }
}

