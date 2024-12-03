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
import java.math.BigDecimal;

public final class BigDecimalSumAggregator<I>
extends AbstractAggregator<I, BigDecimal, BigDecimal>
implements IdentifiedDataSerializable {
    private BigDecimal sum = BigDecimal.ZERO;

    public BigDecimalSumAggregator() {
    }

    public BigDecimalSumAggregator(String attributePath) {
        super(attributePath);
    }

    @Override
    public void accumulateExtracted(I entry, BigDecimal value) {
        this.sum = this.sum.add(value);
    }

    @Override
    public void combine(Aggregator aggregator) {
        BigDecimalSumAggregator longSumAggregator = (BigDecimalSumAggregator)aggregator;
        this.sum = this.sum.add(longSumAggregator.sum);
    }

    @Override
    public BigDecimal aggregate() {
        return this.sum;
    }

    @Override
    public int getFactoryId() {
        return AggregatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attributePath);
        out.writeObject(this.sum);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attributePath = in.readUTF();
        this.sum = (BigDecimal)in.readObject();
    }
}

