/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.merge;

import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.spi.merge.MergingValue;

public interface SplitBrainMergePolicy<V, T extends MergingValue<V>>
extends DataSerializable {
    public V merge(T var1, T var2);
}

