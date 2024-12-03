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
import java.math.BigInteger;

public final class BigIntegerAverageAggregator<I>
extends AbstractAggregator<I, BigInteger, BigDecimal>
implements IdentifiedDataSerializable {
    private BigInteger sum = BigInteger.ZERO;
    private long count;

    public BigIntegerAverageAggregator() {
    }

    public BigIntegerAverageAggregator(String attributePath) {
        super(attributePath);
    }

    @Override
    public void accumulateExtracted(I entry, BigInteger value) {
        ++this.count;
        this.sum = this.sum.add(value);
    }

    @Override
    public void combine(Aggregator aggregator) {
        BigIntegerAverageAggregator typedAggregator = (BigIntegerAverageAggregator)aggregator;
        this.sum = this.sum.add(typedAggregator.sum);
        this.count += typedAggregator.count;
    }

    @Override
    public BigDecimal aggregate() {
        if (this.count == 0L) {
            return null;
        }
        return new BigDecimal(this.sum).divide(BigDecimal.valueOf(this.count));
    }

    @Override
    public int getFactoryId() {
        return AggregatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
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
        this.sum = (BigInteger)in.readObject();
        this.count = in.readLong();
    }
}

