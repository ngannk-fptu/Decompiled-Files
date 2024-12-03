/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.AbstractIndex;
import com.hazelcast.query.impl.BaseIndexStore;
import com.hazelcast.query.impl.BaseSingleValueIndexStore;
import com.hazelcast.query.impl.Comparables;
import com.hazelcast.query.impl.Comparison;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.query.impl.MultiResultSet;
import com.hazelcast.query.impl.QueryableEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class OrderedIndexStore
extends BaseSingleValueIndexStore {
    private final ConcurrentSkipListMap<Comparable, Map<Data, QueryableEntry>> recordMap = new ConcurrentSkipListMap(Comparables.COMPARATOR);
    private final BaseIndexStore.IndexFunctor<Comparable, QueryableEntry> addFunctor;
    private final BaseIndexStore.IndexFunctor<Comparable, Data> removeFunctor;
    private volatile Map<Data, QueryableEntry> recordsWithNullValue;

    public OrderedIndexStore(IndexCopyBehavior copyOn) {
        super(copyOn);
        assert (copyOn != null);
        if (copyOn == IndexCopyBehavior.COPY_ON_WRITE) {
            this.addFunctor = new CopyOnWriteAddFunctor();
            this.removeFunctor = new CopyOnWriteRemoveFunctor();
            this.recordsWithNullValue = Collections.emptyMap();
        } else {
            this.addFunctor = new AddFunctor();
            this.removeFunctor = new RemoveFunctor();
            this.recordsWithNullValue = new ConcurrentHashMap<Data, QueryableEntry>();
        }
    }

    @Override
    Object insertInternal(Comparable value, QueryableEntry record) {
        return this.addFunctor.invoke(value, record);
    }

    @Override
    Object removeInternal(Comparable value, Data recordKey) {
        return this.removeFunctor.invoke(value, recordKey);
    }

    @Override
    public Comparable canonicalizeQueryArgumentScalar(Comparable value) {
        return Comparables.canonicalizeForHashLookup(value);
    }

    @Override
    public Comparable canonicalizeScalarForStorage(Comparable value) {
        return value;
    }

    @Override
    public void clear() {
        this.takeWriteLock();
        try {
            this.recordsWithNullValue.clear();
            this.recordMap.clear();
        }
        finally {
            this.releaseWriteLock();
        }
    }

    @Override
    public boolean isEvaluateOnly() {
        return false;
    }

    @Override
    public boolean canEvaluate(Class<? extends Predicate> predicateClass) {
        return false;
    }

    @Override
    public Set<QueryableEntry> evaluate(Predicate predicate, TypeConverter converter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<QueryableEntry> getRecords(Comparable value) {
        this.takeReadLock();
        try {
            if (value == AbstractIndex.NULL) {
                Set<QueryableEntry> set = this.toSingleResultSet(this.recordsWithNullValue);
                return set;
            }
            Set<QueryableEntry> set = this.toSingleResultSet(this.recordMap.get(value));
            return set;
        }
        finally {
            this.releaseReadLock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<QueryableEntry> getRecords(Set<Comparable> values) {
        this.takeReadLock();
        try {
            MultiResultSet results = this.createMultiResultSet();
            for (Comparable value : values) {
                Map<Data, QueryableEntry> records = value == AbstractIndex.NULL ? this.recordsWithNullValue : this.recordMap.get(value);
                if (records == null) continue;
                this.copyToMultiResultSet(results, records);
            }
            MultiResultSet multiResultSet = results;
            return multiResultSet;
        }
        finally {
            this.releaseReadLock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<QueryableEntry> getRecords(Comparison comparison, Comparable searchedValue) {
        this.takeReadLock();
        try {
            NavigableMap subMap;
            MultiResultSet results = this.createMultiResultSet();
            switch (comparison) {
                case LESS: {
                    subMap = this.recordMap.headMap((Object)searchedValue, false);
                    break;
                }
                case LESS_OR_EQUAL: {
                    subMap = this.recordMap.headMap((Object)searchedValue, true);
                    break;
                }
                case GREATER: {
                    subMap = this.recordMap.tailMap((Object)searchedValue, false);
                    break;
                }
                case GREATER_OR_EQUAL: {
                    subMap = this.recordMap.tailMap((Object)searchedValue, true);
                    break;
                }
                case NOT_EQUAL: {
                    for (Map.Entry<Comparable, Map<Data, QueryableEntry>> entry : this.recordMap.entrySet()) {
                        if (Comparables.compare(searchedValue, entry.getKey()) == 0) continue;
                        this.copyToMultiResultSet(results, entry.getValue());
                    }
                    MultiResultSet multiResultSet = results;
                    return multiResultSet;
                }
                default: {
                    throw new IllegalArgumentException("Unrecognized comparison: " + (Object)((Object)comparison));
                }
            }
            for (Map value : subMap.values()) {
                this.copyToMultiResultSet(results, value);
            }
            MultiResultSet multiResultSet = results;
            return multiResultSet;
        }
        finally {
            this.releaseReadLock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<QueryableEntry> getRecords(Comparable from, boolean fromInclusive, Comparable to, boolean toInclusive) {
        this.takeReadLock();
        try {
            int order = Comparables.compare(from, to);
            if (order == 0) {
                if (!fromInclusive || !toInclusive) {
                    Set<QueryableEntry> set = Collections.emptySet();
                    return set;
                }
                Set<QueryableEntry> set = this.toSingleResultSet(this.recordMap.get(from));
                return set;
            }
            if (order > 0) {
                Set<QueryableEntry> set = Collections.emptySet();
                return set;
            }
            MultiResultSet results = this.createMultiResultSet();
            NavigableMap subMap = this.recordMap.subMap((Object)from, fromInclusive, (Object)to, toInclusive);
            for (Map value : subMap.values()) {
                this.copyToMultiResultSet(results, value);
            }
            MultiResultSet multiResultSet = results;
            return multiResultSet;
        }
        finally {
            this.releaseReadLock();
        }
    }

    private class CopyOnWriteRemoveFunctor
    implements BaseIndexStore.IndexFunctor<Comparable, Data> {
        private CopyOnWriteRemoveFunctor() {
        }

        @Override
        public Object invoke(Comparable value, Data indexKey) {
            Object oldValue;
            if (value == AbstractIndex.NULL) {
                HashMap copy = new HashMap(OrderedIndexStore.this.recordsWithNullValue);
                oldValue = copy.remove(indexKey);
                OrderedIndexStore.this.recordsWithNullValue = copy;
            } else {
                HashMap records = (HashMap)OrderedIndexStore.this.recordMap.get(value);
                if (records != null) {
                    records = new HashMap(records);
                    oldValue = records.remove(indexKey);
                    if (records.isEmpty()) {
                        OrderedIndexStore.this.recordMap.remove(value);
                    } else {
                        OrderedIndexStore.this.recordMap.put(value, records);
                    }
                } else {
                    oldValue = null;
                }
            }
            return oldValue;
        }
    }

    private class RemoveFunctor
    implements BaseIndexStore.IndexFunctor<Comparable, Data> {
        private RemoveFunctor() {
        }

        @Override
        public Object invoke(Comparable value, Data indexKey) {
            Object oldValue;
            if (value == AbstractIndex.NULL) {
                oldValue = OrderedIndexStore.this.recordsWithNullValue.remove(indexKey);
            } else {
                Map records = (Map)OrderedIndexStore.this.recordMap.get(value);
                if (records != null) {
                    oldValue = records.remove(indexKey);
                    if (records.size() == 0) {
                        OrderedIndexStore.this.recordMap.remove(value);
                    }
                } else {
                    oldValue = null;
                }
            }
            return oldValue;
        }
    }

    private class CopyOnWriteAddFunctor
    implements BaseIndexStore.IndexFunctor<Comparable, QueryableEntry> {
        private CopyOnWriteAddFunctor() {
        }

        @Override
        public Object invoke(Comparable value, QueryableEntry entry) {
            QueryableEntry oldValue;
            if (value == AbstractIndex.NULL) {
                HashMap<Data, QueryableEntry> copy = new HashMap<Data, QueryableEntry>(OrderedIndexStore.this.recordsWithNullValue);
                oldValue = copy.put(entry.getKeyData(), entry);
                OrderedIndexStore.this.recordsWithNullValue = copy;
            } else {
                Map<Data, QueryableEntry> records = (Map<Data, QueryableEntry>)OrderedIndexStore.this.recordMap.get(value);
                if (records == null) {
                    records = Collections.emptyMap();
                }
                records = new HashMap(records);
                oldValue = records.put(entry.getKeyData(), entry);
                OrderedIndexStore.this.recordMap.put(value, records);
            }
            return oldValue;
        }
    }

    private class AddFunctor
    implements BaseIndexStore.IndexFunctor<Comparable, QueryableEntry> {
        private AddFunctor() {
        }

        @Override
        public Object invoke(Comparable value, QueryableEntry entry) {
            if (value == AbstractIndex.NULL) {
                return OrderedIndexStore.this.recordsWithNullValue.put(entry.getKeyData(), entry);
            }
            ConcurrentHashMap<Data, QueryableEntry> records = (ConcurrentHashMap<Data, QueryableEntry>)OrderedIndexStore.this.recordMap.get(value);
            if (records == null) {
                records = new ConcurrentHashMap<Data, QueryableEntry>(1, 0.75f, 1);
                OrderedIndexStore.this.recordMap.put(value, records);
            }
            return records.put(entry.getKeyData(), entry);
        }
    }
}

