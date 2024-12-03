/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.merge;

import com.hazelcast.spi.impl.merge.AbstractSplitBrainMergePolicy;
import com.hazelcast.spi.merge.MergingExpirationTime;

public class ExpirationTimeMergePolicy<V, T extends MergingExpirationTime<V>>
extends AbstractSplitBrainMergePolicy<V, T> {
    @Override
    public V merge(T mergingValue, T existingValue) {
        if (mergingValue == null) {
            return existingValue.getValue();
        }
        if (existingValue == null) {
            return mergingValue.getValue();
        }
        if (mergingValue.getExpirationTime() >= existingValue.getExpirationTime()) {
            return mergingValue.getValue();
        }
        return existingValue.getValue();
    }

    @Override
    public int getId() {
        return 12;
    }
}

