/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache$Entry
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.CacheEntry;
import com.hazelcast.cache.impl.ICacheInternal;
import com.hazelcast.nio.serialization.Data;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.cache.Cache;

public abstract class AbstractClusterWideIterator<K, V>
implements Iterator<Cache.Entry<K, V>> {
    protected static final int DEFAULT_FETCH_SIZE = 100;
    protected ICacheInternal<K, V> cache;
    protected List result;
    protected final int partitionCount;
    protected int partitionIndex = -1;
    protected int lastTableIndex = Integer.MAX_VALUE;
    protected final int fetchSize;
    protected boolean prefetchValues;
    protected int index;
    protected int currentIndex = -1;

    public AbstractClusterWideIterator(ICacheInternal<K, V> cache, int partitionCount, int fetchSize, boolean prefetchValues) {
        this.cache = cache;
        this.partitionCount = partitionCount;
        this.fetchSize = fetchSize;
        this.prefetchValues = prefetchValues;
    }

    @Override
    public boolean hasNext() {
        this.ensureOpen();
        if (this.result != null && this.index < this.result.size()) {
            return true;
        }
        return this.advance();
    }

    @Override
    public Cache.Entry<K, V> next() {
        while (this.hasNext()) {
            this.currentIndex = this.index++;
            Data keyData = this.getKey(this.currentIndex);
            Object key = this.toObject(keyData);
            V value = this.getValue(this.currentIndex, key);
            if (value == null) continue;
            return new CacheEntry(key, value);
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        this.ensureOpen();
        if (this.result == null || this.currentIndex < 0) {
            throw new IllegalStateException("Iterator.next() must be called before remove()!");
        }
        Data keyData = this.getKey(this.currentIndex);
        Object key = this.toObject(keyData);
        this.cache.remove(key);
        this.currentIndex = -1;
    }

    protected boolean advance() {
        while (this.partitionIndex < this.getPartitionCount()) {
            if (this.result == null || this.result.size() < this.fetchSize || this.lastTableIndex < 0) {
                ++this.partitionIndex;
                this.lastTableIndex = Integer.MAX_VALUE;
                this.result = null;
                if (this.partitionIndex == this.getPartitionCount()) {
                    return false;
                }
            }
            this.result = this.fetch();
            if (this.result == null || this.result.size() <= 0) continue;
            this.index = 0;
            return true;
        }
        return false;
    }

    private Data getKey(int index) {
        if (this.result != null) {
            if (this.prefetchValues) {
                Map.Entry entry = (Map.Entry)this.result.get(index);
                return (Data)entry.getKey();
            }
            return (Data)this.result.get(index);
        }
        return null;
    }

    private V getValue(int index, K key) {
        if (this.result != null) {
            if (this.prefetchValues) {
                Map.Entry entry = (Map.Entry)this.result.get(index);
                return (V)this.toObject(entry.getValue());
            }
            return (V)this.cache.get(key);
        }
        return null;
    }

    protected void ensureOpen() {
        if (this.cache.isClosed()) {
            throw new IllegalStateException("Cache operations can not be performed. The cache closed");
        }
    }

    protected void setLastTableIndex(List response, int lastTableIndex) {
        if (response != null && response.size() > 0) {
            this.lastTableIndex = lastTableIndex;
        }
    }

    protected int getPartitionCount() {
        return this.partitionCount;
    }

    protected abstract List fetch();

    protected abstract Data toData(Object var1);

    protected abstract <T> T toObject(Object var1);
}

