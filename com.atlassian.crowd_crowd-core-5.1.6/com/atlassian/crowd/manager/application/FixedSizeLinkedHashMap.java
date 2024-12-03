/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.application;

import java.util.LinkedHashMap;
import java.util.Map;

class FixedSizeLinkedHashMap<K, V>
extends LinkedHashMap<K, V> {
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final boolean ACCESS_ORDERING_MODE = true;
    private final int maxSize;

    public FixedSizeLinkedHashMap(int maxSize) {
        super(FixedSizeLinkedHashMap.calculateInitialCapacity(maxSize), 0.75f, true);
        this.maxSize = maxSize;
    }

    private static int calculateInitialCapacity(int maxSize) {
        return (int)Math.ceil((double)Math.max(maxSize, 1) / 4.0);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > this.maxSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

