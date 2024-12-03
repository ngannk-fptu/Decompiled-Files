/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.merge;

import com.hazelcast.spi.impl.merge.AbstractSplitBrainMergePolicy;
import com.hazelcast.spi.merge.MergingValue;

public class PassThroughMergePolicy<V, T extends MergingValue<V>>
extends AbstractSplitBrainMergePolicy<V, T> {
    @Override
    public V merge(T mergingValue, T existingValue) {
        if (mergingValue == null) {
            return existingValue.getValue();
        }
        return mergingValue.getValue();
    }

    @Override
    public int getId() {
        return 17;
    }
}

