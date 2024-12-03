/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.merge;

import com.hazelcast.cardinality.impl.hyperloglog.HyperLogLog;
import com.hazelcast.spi.impl.merge.AbstractSplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;

public class HyperLogLogMergePolicy
extends AbstractSplitBrainMergePolicy<HyperLogLog, SplitBrainMergeTypes.CardinalityEstimatorMergeTypes> {
    @Override
    public HyperLogLog merge(SplitBrainMergeTypes.CardinalityEstimatorMergeTypes mergingValue, SplitBrainMergeTypes.CardinalityEstimatorMergeTypes existingValue) {
        if (existingValue == null) {
            return (HyperLogLog)mergingValue.getValue();
        }
        ((HyperLogLog)mergingValue.getValue()).merge((HyperLogLog)existingValue.getValue());
        return (HyperLogLog)mergingValue.getValue();
    }

    @Override
    public int getId() {
        return 14;
    }
}

