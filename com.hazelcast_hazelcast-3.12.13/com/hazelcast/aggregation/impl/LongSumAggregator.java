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

public final class LongSumAggregator<I>
extends AbstractAggregator<I, Number, Long>
implements IdentifiedDataSerializable {
    private long sum;

    public LongSumAggregator() {
    }

    public LongSumAggregator(String attributePath) {
        super(attributePath);
    }

    @Override
    public void accumulateExtracted(I entry, Number value) {
        this.sum += Numbers.asLongExactly(value);
    }

    @Override
    public void combine(Aggregator aggregator) {
        LongSumAggregator longSumAggregator = (LongSumAggregator)aggregator;
        this.sum += longSumAggregator.sum;
    }

    @Override
    public Long aggregate() {
        return this.sum;
    }

    @Override
    public int getFactoryId() {
        return AggregatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 13;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attributePath);
        out.writeLong(this.sum);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attributePath = in.readUTF();
        this.sum = in.readLong();
    }
}

