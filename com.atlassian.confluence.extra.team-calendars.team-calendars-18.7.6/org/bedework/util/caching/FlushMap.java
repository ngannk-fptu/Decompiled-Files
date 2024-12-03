/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.caching;

import java.util.HashMap;

public class FlushMap<K, V>
extends HashMap<K, V> {
    private long lastFlush;
    private long flushTime;
    private int maxSize;
    private static final int defaultMaxSize = 1000;
    private static final long defaultFlushTime = 600000L;

    public FlushMap() {
        this.flushTime = 600000L;
        this.maxSize = 1000;
    }

    public FlushMap(int size) {
        super(size);
        this.flushTime = 600000L;
        this.maxSize = 1000;
    }

    public FlushMap(long flushTime, int maxSize) {
        this.flushTime = flushTime;
        this.maxSize = maxSize;
    }

    public FlushMap(int size, long flushTime, int maxSize) {
        super(size);
        this.flushTime = flushTime;
        this.maxSize = maxSize;
        this.lastFlush = System.currentTimeMillis();
    }

    protected boolean testFlush() {
        boolean flushed = false;
        if (this.flushTime > 0L && System.currentTimeMillis() - this.lastFlush > this.flushTime) {
            this.clear();
            flushed = true;
            this.lastFlush = System.currentTimeMillis();
        }
        if (this.maxSize <= 0) {
            return flushed;
        }
        if (this.size() >= this.maxSize) {
            this.clear();
            flushed = true;
            this.lastFlush = System.currentTimeMillis();
        }
        return flushed;
    }

    @Override
    public boolean containsKey(Object key) {
        this.testFlush();
        return super.containsKey(key);
    }

    @Override
    public synchronized V put(K key, V val) {
        this.testFlush();
        return super.put(key, val);
    }

    @Override
    public V get(Object key) {
        Object val = super.get(key);
        if (val == null) {
            return null;
        }
        if (this.testFlush()) {
            super.put(key, val);
        }
        return val;
    }
}

