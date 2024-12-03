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

public final class MinAggregator<I, R extends Comparable>
extends AbstractAggregator<I, R, R>
implements IdentifiedDataSerializable {
    private R min;

    public MinAggregator() {
    }

    public MinAggregator(String attributePath) {
        super(attributePath);
    }

    @Override
    public void accumulateExtracted(I entry, R value) {
        if (this.isCurrentlyGreaterThan(value)) {
            this.min = value;
        }
    }

    private boolean isCurrentlyGreaterThan(R otherValue) {
        if (otherValue == null) {
            return false;
        }
        return this.min == null || Comparables.compare(this.min, otherValue) > 0;
    }

    @Override
    public void combine(Aggregator aggregator) {
        MinAggregator minAggregator = (MinAggregator)aggregator;
        R valueFromOtherAggregator = minAggregator.min;
        if (this.isCurrentlyGreaterThan(valueFromOtherAggregator)) {
            this.min = valueFromOtherAggregator;
        }
    }

    @Override
    public R aggregate() {
        return this.min;
    }

    @Override
    public int getFactoryId() {
        return AggregatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 15;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attributePath);
        out.writeObject(this.min);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attributePath = in.readUTF();
        this.min = (Comparable)in.readObject();
    }
}

