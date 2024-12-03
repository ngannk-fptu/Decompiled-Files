/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import com.twelvemonkeys.util.ExpiringMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class LRUHashMap<K, V>
extends LinkedHashMap<K, V>
implements ExpiringMap<K, V> {
    private int maxSize = 1000;
    private float trimFactor = 0.01f;

    public LRUHashMap() {
        super(16, 0.75f, true);
    }

    public LRUHashMap(int n) {
        super(16, 0.75f, true);
        this.setMaxSize(n);
    }

    public LRUHashMap(Map<? extends K, ? extends V> map) {
        super(16, 0.75f, true);
        this.putAll(map);
    }

    public LRUHashMap(Map<? extends K, ? extends V> map, int n) {
        super(16, 0.75f, true);
        this.setMaxSize(n);
        this.putAll(map);
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void setMaxSize(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("max size must be positive");
        }
        this.maxSize = n;
        while (this.size() > this.maxSize) {
            this.removeLRU();
        }
    }

    public float getTrimFactor() {
        return this.trimFactor;
    }

    public void setTrimFactor(float f) {
        if (f < 0.0f || f >= 1.0f) {
            throw new IllegalArgumentException("trim factor must be between 0 and 1");
        }
        this.trimFactor = f;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> entry) {
        if (this.size() >= this.maxSize) {
            this.removeLRU();
        }
        return false;
    }

    @Override
    public void processRemoved(Map.Entry<K, V> entry) {
    }

    public void removeLRU() {
        int n = (int)Math.max((float)this.size() * this.trimFactor, 1.0f);
        Iterator iterator = this.entrySet().iterator();
        while (n-- > 0 && iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }
}

