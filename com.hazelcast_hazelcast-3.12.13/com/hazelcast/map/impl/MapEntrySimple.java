/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.nio.serialization.SerializableByConvention;
import java.util.AbstractMap;

@SerializableByConvention
public class MapEntrySimple<K, V>
extends AbstractMap.SimpleEntry<K, V> {
    private boolean modified;

    public MapEntrySimple(K key, V value) {
        super(key, value);
    }

    @Override
    public V setValue(V value) {
        this.modified = true;
        return super.setValue(value);
    }

    public boolean isModified() {
        return this.modified;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

