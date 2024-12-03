/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.merge;

public interface MergingValue<V> {
    public V getValue();

    public <DV> DV getDeserializedValue();
}

