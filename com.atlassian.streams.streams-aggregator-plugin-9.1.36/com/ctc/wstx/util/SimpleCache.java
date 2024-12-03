/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SimpleCache {
    protected final LimitMap mItems;
    protected final int mMaxSize;

    public SimpleCache(int maxSize) {
        this.mItems = new LimitMap(maxSize);
        this.mMaxSize = maxSize;
    }

    public Object find(Object key) {
        return this.mItems.get(key);
    }

    public void add(Object key, Object value) {
        this.mItems.put(key, value);
    }

    static final class LimitMap
    extends LinkedHashMap {
        private static final long serialVersionUID = 1L;
        protected final int mMaxSize;

        public LimitMap(int size) {
            super(size, 0.8f, true);
            this.mMaxSize = size;
        }

        public boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() >= this.mMaxSize;
        }
    }
}

