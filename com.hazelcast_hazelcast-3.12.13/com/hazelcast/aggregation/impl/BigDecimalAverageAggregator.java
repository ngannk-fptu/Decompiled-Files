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

public final class BigDecimalAverageAggregator<I>
extends AbstractAggregator<I, BigDecimal, BigDecimal>
implements IdentifiedDataSerializable {
    private BigDecimal sum = BigDecimal.ZERO;
    private long count;

    public BigDecimalAverageAggregator() {
    }

    public BigDecimalAverageAggregator(String attributePath) {
        super(attributePath);
    }

    @Override
    protected void accumulateExtracted(I entry, BigDecimal value) {
        ++this.count;
        this.sum = this.sum.add(value);
    }

    @Override
    public void combine(Aggregator aggregator) {
        BigDecimalAverageAggregator doubleAverageAggregator = (BigDecimalAverageAggregator)aggregator;
        this.sum = this.sum.add(doubleAverageAggregator.sum);
        this.count += doubleAverageAggregator.count;
    }

    @Override
    public BigDecimal aggregate() {
        if (this.count == 0L) {
            return null;
        }
        return this.sum.divide(BigDecimal.valueOf(this.count));
    }

    @Override
    public int getFactoryId() {
        return AggregatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attributePath);
        out.writeObject(this.sum);
        out.writeLong(this.count);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attributePath = in.readUTF();
        this.sum = (BigDecimal)in.readObject();
        this.count = in.readLong();
    }
}

