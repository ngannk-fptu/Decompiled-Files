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
import java.io.IOException;

public final class NumberAverageAggregator<I>
extends AbstractAggregator<I, Number, Double>
implements IdentifiedDataSerializable {
    private double sum;
    private long count;

    public NumberAverageAggregator() {
    }

    public NumberAverageAggregator(String attributePath) {
        super(attributePath);
    }

    @Override
    public void accumulateExtracted(I entry, Number value) {
        ++this.count;
        this.sum += value.doubleValue();
    }

    @Override
    public void combine(Aggregator aggregator) {
        NumberAverageAggregator doubleAverageAggregator = (NumberAverageAggregator)aggregator;
        this.sum += doubleAverageAggregator.sum;
        this.count += doubleAverageAggregator.count;
    }

    @Override
    public Double aggregate() {
        if (this.count == 0L) {
            return null;
        }
        return this.sum / (double)this.count;
    }

    @Override
    public int getFactoryId() {
        return AggregatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 16;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attributePath);
        out.writeDouble(this.sum);
        out.writeLong(this.count);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attributePath = in.readUTF();
        this.sum = in.readDouble();
        this.count = in.readLong();
    }
}

