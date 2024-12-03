/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.MultiResultSet;
import com.hazelcast.query.impl.QueryableEntry;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FastMultiResultSet
extends AbstractSet<QueryableEntry>
implements MultiResultSet {
    private Set<Object> index;
    private final List<Map<Data, QueryableEntry>> resultSets = new ArrayList<Map<Data, QueryableEntry>>();

    @Override
    public void addResultSet(Map<Data, QueryableEntry> resultSet) {
        this.resultSets.add(resultSet);
    }

    @Override
    public boolean contains(Object o) {
        QueryableEntry entry = (QueryableEntry)o;
        if (this.index != null) {
            return this.checkFromIndex(entry);
        }
        if (this.resultSets.size() > 3) {
            this.index = new HashSet<Object>();
            for (Map<Data, QueryableEntry> result : this.resultSets) {
                for (QueryableEntry queryableEntry : result.values()) {
                    this.index.add(queryableEntry.getKeyData());
                }
            }
            return this.checkFromIndex(entry);
        }
        for (Map<Data, QueryableEntry> resultSet : this.resultSets) {
            if (!resultSet.containsKey(entry.getKeyData())) continue;
            return true;
        }
        return false;
    }

    private boolean checkFromIndex(QueryableEntry entry) {
        return this.index.contains(entry.getKeyData());
    }

    @Override
    public Iterator<QueryableEntry> iterator() {
        return new It();
    }

    @Override
    public boolean add(QueryableEntry obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        int size = 0;
        for (Map<Data, QueryableEntry> resultSet : this.resultSets) {
            size += resultSet.size();
        }
        return size;
    }

    class It
    implements Iterator<QueryableEntry> {
        int currentIndex;
        Iterator<QueryableEntry> currentIterator;

        It() {
        }

        @Override
        public boolean hasNext() {
            if (FastMultiResultSet.this.resultSets.size() == 0) {
                return false;
            }
            if (this.currentIterator != null && this.currentIterator.hasNext()) {
                return true;
            }
            while (this.currentIndex < FastMultiResultSet.this.resultSets.size()) {
                this.currentIterator = ((Map)FastMultiResultSet.this.resultSets.get(this.currentIndex++)).values().iterator();
                if (!this.currentIterator.hasNext()) continue;
                return true;
            }
            return false;
        }

        @Override
        public QueryableEntry next() {
            if (FastMultiResultSet.this.resultSets.size() == 0) {
                return null;
            }
            if (this.currentIterator != null && this.currentIterator.hasNext()) {
                return this.currentIterator.next();
            }
            while (this.currentIndex < FastMultiResultSet.this.resultSets.size()) {
                this.currentIterator = ((Map)FastMultiResultSet.this.resultSets.get(this.currentIndex++)).values().iterator();
                if (!this.currentIterator.hasNext()) continue;
                return this.currentIterator.next();
            }
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

