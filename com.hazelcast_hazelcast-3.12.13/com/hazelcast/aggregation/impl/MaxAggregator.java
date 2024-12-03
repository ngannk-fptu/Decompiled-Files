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

public final class MaxAggregator<I, R extends Comparable>
extends AbstractAggregator<I, R, R>
implements IdentifiedDataSerializable {
    private R max;

    public MaxAggregator() {
    }

    public MaxAggregator(String attributePath) {
        super(attributePath);
    }

    @Override
    public void accumulateExtracted(I entry, R value) {
        if (this.isCurrentlyLessThan(value)) {
            this.max = value;
        }
    }

    private boolean isCurrentlyLessThan(R otherValue) {
        if (otherValue == null) {
            return false;
        }
        return this.max == null || Comparables.compare(this.max, otherValue) < 0;
    }

    @Override
    public void combine(Aggregator aggregator) {
        MaxAggregator maxAggregator = (MaxAggregator)aggregator;
        R valueFromOtherAggregator = maxAggregator.max;
        if (this.isCurrentlyLessThan(valueFromOtherAggregator)) {
            this.max = valueFromOtherAggregator;
        }
    }

    @Override
    public R aggregate() {
        return this.max;
    }

    @Override
    public int getFactoryId() {
        return AggregatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 14;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attributePath);
        out.writeObject(this.max);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attributePath = in.readUTF();
        this.max = (Comparable)in.readObject();
    }
}

