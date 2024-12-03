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

public final class IntegerSumAggregator<I>
extends AbstractAggregator<I, Number, Long>
implements IdentifiedDataSerializable {
    private long sum;

    public IntegerSumAggregator() {
    }

    public IntegerSumAggregator(String attributePath) {
        super(attributePath);
    }

    @Override
    public void accumulateExtracted(I entry, Number value) {
        this.sum += (long)Numbers.asIntExactly(value);
    }

    @Override
    public void combine(Aggregator aggregator) {
        IntegerSumAggregator integerSumAggregator = (IntegerSumAggregator)aggregator;
        this.sum += integerSumAggregator.sum;
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
        return 11;
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

