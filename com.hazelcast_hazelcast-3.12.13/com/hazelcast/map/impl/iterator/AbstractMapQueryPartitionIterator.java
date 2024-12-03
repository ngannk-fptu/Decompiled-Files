/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.iterator;

import com.hazelcast.core.IMap;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.projection.Projection;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.IterationType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class AbstractMapQueryPartitionIterator<K, V, R>
implements Iterator<R> {
    protected final IMap<K, V> map;
    protected final int fetchSize;
    protected final int partitionId;
    protected final Query query;
    protected int lastTableIndex = Integer.MAX_VALUE;
    protected int index;
    protected int currentIndex = -1;
    protected List<Data> segment;

    public AbstractMapQueryPartitionIterator(IMap<K, V> map, int fetchSize, int partitionId, Predicate<K, V> predicate, Projection<Map.Entry<K, V>, R> projection) {
        this.map = map;
        this.fetchSize = fetchSize;
        this.partitionId = partitionId;
        this.query = Query.of().mapName(map.getName()).iterationType(IterationType.VALUE).predicate(predicate).projection(projection).build();
    }

    @Override
    public boolean hasNext() {
        return this.segment != null && this.index < this.segment.size() || this.advance();
    }

    @Override
    public R next() {
        if (this.hasNext()) {
            this.currentIndex = this.index++;
            return (R)this.getSerializationService().toObject(this.getQueryResult(this.currentIndex));
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Removing when iterating map with query is not supported");
    }

    protected boolean advance() {
        if (this.lastTableIndex < 0) {
            this.lastTableIndex = Integer.MAX_VALUE;
            return false;
        }
        this.segment = this.fetch();
        if (CollectionUtil.isNotEmpty(this.segment)) {
            this.index = 0;
            return true;
        }
        return false;
    }

    protected void setLastTableIndex(List<Data> segment, int lastTableIndex) {
        if (CollectionUtil.isNotEmpty(segment)) {
            this.lastTableIndex = lastTableIndex;
        }
    }

    protected abstract List<Data> fetch();

    protected abstract SerializationService getSerializationService();

    private Data getQueryResult(int index) {
        if (this.segment != null) {
            return this.segment.get(index);
        }
        return null;
    }
}

