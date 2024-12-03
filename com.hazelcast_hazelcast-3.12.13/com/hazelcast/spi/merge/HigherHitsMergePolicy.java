/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.merge;

import com.hazelcast.spi.impl.merge.AbstractSplitBrainMergePolicy;
import com.hazelcast.spi.merge.MergingHits;

public class HigherHitsMergePolicy<V, T extends MergingHits<V>>
extends AbstractSplitBrainMergePolicy<V, T> {
    @Override
    public V merge(T mergingValue, T existingValue) {
        if (mergingValue == null) {
            return existingValue.getValue();
        }
        if (existingValue == null) {
            return mergingValue.getValue();
        }
        if (mergingValue.getHits() >= existingValue.getHits()) {
            return mergingValue.getValue();
        }
        return existingValue.getValue();
    }

    @Override
    public int getId() {
        return 13;
    }
}

