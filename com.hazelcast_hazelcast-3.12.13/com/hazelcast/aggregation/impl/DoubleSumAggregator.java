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

public final class DoubleSumAggregator<I>
extends AbstractAggregator<I, Number, Double>
implements IdentifiedDataSerializable {
    private double sum;

    public DoubleSumAggregator() {
    }

    public DoubleSumAggregator(String attributePath) {
        super(attributePath);
    }

    @Override
    public void accumulateExtracted(I entry, Number value) {
        this.sum += Numbers.asDoubleExactly(value);
    }

    @Override
    public void combine(Aggregator aggregator) {
        DoubleSumAggregator longSumAggregator = (DoubleSumAggregator)aggregator;
        this.sum += longSumAggregator.sum;
    }

    @Override
    public Double aggregate() {
        return this.sum;
    }

    @Override
    public int getFactoryId() {
        return AggregatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attributePath);
        out.writeDouble(this.sum);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attributePath = in.readUTF();
        this.sum = in.readDouble();
    }
}

