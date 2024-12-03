/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.conversion;

import java.util.HashMap;
import java.util.Map;

class GenerationalCache {
    private static final int DEFAULT_CACHE_SIZE = 1000;
    private static final int DEFAULT_SIZE_AGE_RATIO = 10;
    private final int maxSize;
    private final int maxAge;
    private Map cache = new HashMap();
    private Map old = new HashMap();
    private Map young = new HashMap();
    private int age = 0;

    public GenerationalCache(int maxSize, int maxAge) {
        this.maxSize = maxSize;
        this.maxAge = maxAge;
    }

    public GenerationalCache(int maxSize) {
        this(maxSize, maxSize / 10);
    }

    public GenerationalCache() {
        this(1000);
    }

    public Object get(Object key) {
        Object value = this.cache.get(key);
        if (value == null && (value = this.old.get(key)) != null) {
            this.put(key, value);
        }
        return value;
    }

    public synchronized void put(Object key, Object value) {
        this.young.put(key, value);
        if (++this.age == this.maxAge) {
            HashMap union = new HashMap();
            for (Map.Entry entry : this.old.entrySet()) {
                if (!this.young.containsKey(entry.getKey())) continue;
                union.put(entry.getKey(), entry.getValue());
            }
            if (!union.isEmpty()) {
                if (this.cache.size() + union.size() <= this.maxSize) {
                    union.putAll(this.cache);
                }
                this.cache = union;
            }
            this.old = this.young;
            this.young = new HashMap();
            this.age = 0;
        }
    }
}

