/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.merge;

import com.hazelcast.spi.merge.MergingValue;

public interface MergingEntry<K, V>
extends MergingValue<V> {
    public K getKey();

    public <DK> DK getDeserializedKey();
}

