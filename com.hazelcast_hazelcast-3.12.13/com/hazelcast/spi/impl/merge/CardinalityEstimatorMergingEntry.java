/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.cardinality.impl.hyperloglog.HyperLogLog;
import com.hazelcast.spi.impl.merge.AbstractMergingEntryImpl;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;

public class CardinalityEstimatorMergingEntry
extends AbstractMergingEntryImpl<String, HyperLogLog, CardinalityEstimatorMergingEntry>
implements SplitBrainMergeTypes.CardinalityEstimatorMergeTypes {
    public CardinalityEstimatorMergingEntry() {
    }

    public CardinalityEstimatorMergingEntry(SerializationService serializationService) {
        super(serializationService);
    }

    @Override
    public int getId() {
        return 9;
    }
}

