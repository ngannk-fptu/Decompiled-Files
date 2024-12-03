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

public final class CountAggregator<I>
extends AbstractAggregator<I, Object, Long>
implements IdentifiedDataSerializable {
    private long count;

    public CountAggregator() {
    }

    public CountAggregator(String attributePath) {
        super(attributePath);
    }

    @Override
    public void accumulateExtracted(I entry, Object value) {
        ++this.count;
    }

    @Override
    public void combine(Aggregator aggregator) {
        CountAggregator countAggregator = (CountAggregator)aggregator;
        this.count += countAggregator.count;
    }

    @Override
    public Long aggregate() {
        return this.count;
    }

    @Override
    public int getFactoryId() {
        return AggregatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attributePath);
        out.writeLong(this.count);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attributePath = in.readUTF();
        this.count = in.readLong();
    }
}

