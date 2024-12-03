/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import java.util.LinkedHashMap;
import java.util.Map;

class LRUMap<K, V>
extends LinkedHashMap<K, V> {
    private int maxSize;

    public LRUMap() {
        this(10);
    }

    public LRUMap(int size) {
        this(size, 16);
    }

    public LRUMap(int size, int initialCapacity) {
        this(size, initialCapacity, 0.75f);
    }

    public LRUMap(int size, int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor, true);
        this.maxSize = size;
    }

    public void setMaxSize(int size) {
        this.maxSize = size;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> entry) {
        return this.size() > this.maxSize;
    }
}

