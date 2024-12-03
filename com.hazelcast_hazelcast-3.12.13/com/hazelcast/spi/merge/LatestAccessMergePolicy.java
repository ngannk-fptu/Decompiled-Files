/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.merge;

import com.hazelcast.spi.impl.merge.AbstractSplitBrainMergePolicy;
import com.hazelcast.spi.merge.MergingLastAccessTime;

public class LatestAccessMergePolicy<V, T extends MergingLastAccessTime<V>>
extends AbstractSplitBrainMergePolicy<V, T> {
    @Override
    public V merge(T mergingValue, T existingValue) {
        if (mergingValue == null) {
            return existingValue.getValue();
        }
        if (existingValue == null) {
            return mergingValue.getValue();
        }
        if (mergingValue.getLastAccessTime() >= existingValue.getLastAccessTime()) {
            return mergingValue.getValue();
        }
        return existingValue.getValue();
    }

    @Override
    public int getId() {
        return 15;
    }
}

