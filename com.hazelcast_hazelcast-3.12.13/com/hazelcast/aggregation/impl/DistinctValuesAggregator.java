/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.aggregation.impl;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.aggregation.impl.AbstractAggregator;
import com.hazelcast.aggregation.impl.AggregatorDataSerializerHook;
import com.hazelcast.aggregation.impl.CanonicalizingHashSet;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.MapUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.Set;

@SuppressFBWarnings(value={"SE_BAD_FIELD"})
public final class DistinctValuesAggregator<I, R>
extends AbstractAggregator<I, R, Set<R>>
implements IdentifiedDataSerializable {
    private CanonicalizingHashSet<R> values = new CanonicalizingHashSet();

    public DistinctValuesAggregator() {
    }

    public DistinctValuesAggregator(String attributePath) {
        super(attributePath);
    }

    @Override
    public void accumulateExtracted(I entry, R value) {
        this.values.addInternal(value);
    }

    @Override
    public void combine(Aggregator aggregator) {
        DistinctValuesAggregator distinctValuesAggregator = (DistinctValuesAggregator)aggregator;
        this.values.addAllInternal(distinctValuesAggregator.values);
    }

    @Override
    public Set<R> aggregate() {
        return this.values;
    }

    @Override
    public int getFactoryId() {
        return AggregatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attributePath);
        out.writeInt(this.values.size());
        for (R value : this.values) {
            out.writeObject(value);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attributePath = in.readUTF();
        int count = in.readInt();
        this.values = new CanonicalizingHashSet(MapUtil.calculateInitialCapacity(count));
        for (int i = 0; i < count; ++i) {
            Object value = in.readObject();
            this.values.addInternal(value);
        }
    }
}

