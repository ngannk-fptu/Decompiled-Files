/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.aggregation.impl;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.aggregation.impl.AbstractAggregator;
import com.hazelcast.aggregation.impl.AggregatorDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.impl.Comparables;
import java.io.IOException;

public final class MaxByAggregator<I>
extends AbstractAggregator<I, Comparable, I>
implements IdentifiedDataSerializable {
    private Comparable maxValue;
    private I maxEntry;

    public MaxByAggregator() {
    }

    public MaxByAggregator(String attributePath) {
        super(attributePath);
    }

    @Override
    public void accumulateExtracted(I entry, Comparable value) {
        if (this.isCurrentlyLessThan(value)) {
            this.maxValue = value;
            this.maxEntry = entry;
        }
    }

    private boolean isCurrentlyLessThan(Comparable otherValue) {
        if (otherValue == null) {
            return false;
        }
        return this.maxValue == null || Comparables.compare(this.maxValue, otherValue) < 0;
    }

    @Override
    public void combine(Aggregator aggregator) {
        MaxByAggregator maxAggregator = (MaxByAggregator)aggregator;
        Comparable valueFromOtherAggregator = maxAggregator.maxValue;
        if (this.isCurrentlyLessThan(valueFromOtherAggregator)) {
            this.maxValue = valueFromOtherAggregator;
            this.maxEntry = maxAggregator.maxEntry;
        }
    }

    @Override
    public I aggregate() {
        return this.maxEntry;
    }

    @Override
    public int getFactoryId() {
        return AggregatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 17;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attributePath);
        out.writeObject(this.maxValue);
        out.writeObject(this.maxEntry);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attributePath = in.readUTF();
        this.maxValue = (Comparable)in.readObject();
        this.maxEntry = in.readObject();
    }
}

