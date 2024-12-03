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

public final class MinByAggregator<I>
extends AbstractAggregator<I, Comparable, I>
implements IdentifiedDataSerializable {
    private Comparable minValue;
    private I minEntry;

    public MinByAggregator() {
    }

    public MinByAggregator(String attributePath) {
        super(attributePath);
    }

    @Override
    public void accumulateExtracted(I entry, Comparable value) {
        if (this.isCurrentlyGreaterThan(value)) {
            this.minValue = value;
            this.minEntry = entry;
        }
    }

    private boolean isCurrentlyGreaterThan(Comparable otherValue) {
        if (otherValue == null) {
            return false;
        }
        return this.minValue == null || Comparables.compare(this.minValue, otherValue) > 0;
    }

    @Override
    public void combine(Aggregator aggregator) {
        MinByAggregator minAggregator = (MinByAggregator)aggregator;
        Comparable valueFromOtherAggregator = minAggregator.minValue;
        if (this.isCurrentlyGreaterThan(valueFromOtherAggregator)) {
            this.minValue = valueFromOtherAggregator;
            this.minEntry = minAggregator.minEntry;
        }
    }

    @Override
    public I aggregate() {
        return this.minEntry;
    }

    @Override
    public int getFactoryId() {
        return AggregatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 18;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attributePath);
        out.writeObject(this.minValue);
        out.writeObject(this.minEntry);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attributePath = in.readUTF();
        this.minValue = (Comparable)in.readObject();
        this.minEntry = in.readObject();
    }
}

