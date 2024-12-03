/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import com.twelvemonkeys.util.ExpiringMap;
import com.twelvemonkeys.util.LinkedMap;
import java.util.Map;

public class LRUMap<K, V>
extends LinkedMap<K, V>
implements ExpiringMap<K, V> {
    private int maxSize = 1000;
    private float trimFactor = 0.01f;

    public LRUMap() {
        super(null, true);
    }

    public LRUMap(int n) {
        super(null, true);
        this.setMaxSize(n);
    }

    public LRUMap(Map<? extends K, ? extends V> map) {
        super(map, true);
    }

    public LRUMap(Map<? extends K, ? extends V> map, int n) {
        super(map, true);
        this.setMaxSize(n);
    }

    public LRUMap(Map<K, Map.Entry<K, V>> map, Map<? extends K, ? extends V> map2, int n) {
        super(map, map2, true);
        this.setMaxSize(n);
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
    protected boolean removeEldestEntry(Map.Entry entry) {
        if (this.size() >= this.maxSize) {
            this.removeLRU();
        }
        return false;
    }

    @Override
    protected Map.Entry<K, V> removeEntry(Map.Entry<K, V> entry) {
        Map.Entry<K, V> entry2 = super.removeEntry(entry);
        this.processRemoved(entry);
        return entry2;
    }

    @Override
    public void processRemoved(Map.Entry<K, V> entry) {
    }

    public void removeLRU() {
        int n = (int)Math.max((float)this.size() * this.trimFactor, 1.0f);
        while (n-- > 0) {
            this.removeEntry(this.head.next);
        }
    }
}

