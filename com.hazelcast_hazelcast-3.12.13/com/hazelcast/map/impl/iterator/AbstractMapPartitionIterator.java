/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.iterator;

import com.hazelcast.core.IMap;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.LazyMapEntry;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class AbstractMapPartitionIterator<K, V>
implements Iterator<Map.Entry<K, V>> {
    protected IMap<K, V> map;
    protected final int fetchSize;
    protected final int partitionId;
    protected boolean prefetchValues;
    protected int lastTableIndex = Integer.MAX_VALUE;
    protected int index;
    protected int currentIndex = -1;
    protected List result;

    public AbstractMapPartitionIterator(IMap<K, V> map, int fetchSize, int partitionId, boolean prefetchValues) {
        this.map = map;
        this.fetchSize = fetchSize;
        this.partitionId = partitionId;
        this.prefetchValues = prefetchValues;
    }

    @Override
    public boolean hasNext() {
        return this.result != null && this.index < this.result.size() || this.advance();
    }

    @Override
    public Map.Entry<K, V> next() {
        while (this.hasNext()) {
            this.currentIndex = this.index++;
            Data keyData = this.getKey(this.currentIndex);
            Object value = this.getValue(this.currentIndex, keyData);
            if (value == null) continue;
            return new LazyMapEntry(keyData, value, (InternalSerializationService)this.getSerializationService());
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        if (this.result == null || this.currentIndex < 0) {
            throw new IllegalStateException("Iterator.next() must be called before remove()!");
        }
        Data keyData = this.getKey(this.currentIndex);
        this.map.remove(keyData);
        this.currentIndex = -1;
    }

    protected boolean advance() {
        if (this.lastTableIndex < 0) {
            this.lastTableIndex = Integer.MAX_VALUE;
            return false;
        }
        this.result = this.fetch();
        if (this.result != null && this.result.size() > 0) {
            this.index = 0;
            return true;
        }
        return false;
    }

    protected void setLastTableIndex(List response, int lastTableIndex) {
        if (response != null && response.size() > 0) {
            this.lastTableIndex = lastTableIndex;
        }
    }

    protected abstract List fetch();

    protected abstract SerializationService getSerializationService();

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

    private Object getValue(int index, Data keyData) {
        if (this.result != null) {
            if (this.prefetchValues) {
                Map.Entry entry = (Map.Entry)this.result.get(index);
                return entry.getValue();
            }
            return this.map.get(keyData);
        }
        return null;
    }
}

