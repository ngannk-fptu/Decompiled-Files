/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.merge;

import com.hazelcast.spi.merge.MergingValue;

public interface SplitBrainMergeTypeProvider<T extends MergingValue> {
    public Class<T> getProvidedMergeTypes();
}

