/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.EntryView;

class NullEntryView<K, V>
implements EntryView<K, V> {
    private K key;

    NullEntryView(K key) {
        this.key = key;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return null;
    }

    @Override
    public long getCost() {
        return 0L;
    }

    @Override
    public long getCreationTime() {
        return 0L;
    }

    @Override
    public long getExpirationTime() {
        return 0L;
    }

    @Override
    public long getHits() {
        return 0L;
    }

    @Override
    public long getLastAccessTime() {
        return 0L;
    }

    @Override
    public long getLastStoredTime() {
        return 0L;
    }

    @Override
    public long getLastUpdateTime() {
        return 0L;
    }

    @Override
    public long getVersion() {
        return 0L;
    }

    @Override
    public long getTtl() {
        return 0L;
    }

    @Override
    public Long getMaxIdle() {
        return 0L;
    }
}

