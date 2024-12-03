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
import com.hazelcast.query.impl.Numbers;
import java.io.IOException;

public final class LongAverageAggregator<I>
extends AbstractAggregator<I, Number, Double>
implements IdentifiedDataSerializable {
    private long sum;
    private long count;

    public LongAverageAggregator() {
    }

    public LongAverageAggregator(String attributePath) {
        super(attributePath);
    }

    @Override
    public void accumulateExtracted(I entry, Number value) {
        ++this.count;
        this.sum += Numbers.asLongExactly(value);
    }

    @Override
    public void combine(Aggregator aggregator) {
        LongAverageAggregator longAverageAggregator = (LongAverageAggregator)aggregator;
        this.sum += longAverageAggregator.sum;
        this.count += longAverageAggregator.count;
    }

    @Override
    public Double aggregate() {
        if (this.count == 0L) {
            return null;
        }
        return (double)this.sum / (double)this.count;
    }

    @Override
    public int getFactoryId() {
        return AggregatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 12;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attributePath);
        out.writeLong(this.sum);
        out.writeLong(this.count);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attributePath = in.readUTF();
        this.sum = in.readLong();
        this.count = in.readLong();
    }
}

