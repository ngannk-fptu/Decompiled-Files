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
import com.hazelcast.query.impl.CompositeValue;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.query.impl.MultiResultSet;
import com.hazelcast.query.impl.Numbers;
import com.hazelcast.query.impl.QueryableEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UnorderedIndexStore
extends BaseSingleValueIndexStore {
    private final ConcurrentMap<Comparable, Map<Data, QueryableEntry>> recordMap = new ConcurrentHashMap<Comparable, Map<Data, QueryableEntry>>(1000);
    private final BaseIndexStore.IndexFunctor<Comparable, QueryableEntry> addFunctor;
    private final BaseIndexStore.IndexFunctor<Comparable, Data> removeFunctor;
    private volatile Map<Data, QueryableEntry> recordsWithNullValue;

    public UnorderedIndexStore(IndexCopyBehavior copyOn) {
        super(copyOn);
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
        return this.canonicalizeScalarForStorage(value);
    }

    @Override
    public Comparable canonicalizeScalarForStorage(Comparable value) {
        if (!(value instanceof Number)) {
            return value;
        }
        Class<?> clazz = value.getClass();
        Number number = (Number)((Object)value);
        if (clazz == Double.class) {
            long longValue;
            double doubleValue = number.doubleValue();
            if (Numbers.equalDoubles(doubleValue, longValue = number.longValue())) {
                return UnorderedIndexStore.canonicalizeLongRepresentable(longValue);
            }
            float floatValue = number.floatValue();
            if (doubleValue == (double)floatValue) {
                return Float.valueOf(floatValue);
            }
        } else if (clazz == Float.class) {
            long longValue;
            float floatValue = number.floatValue();
            if (Numbers.equalFloats(floatValue, longValue = number.longValue())) {
                return UnorderedIndexStore.canonicalizeLongRepresentable(longValue);
            }
        } else if (Numbers.isLongRepresentable(clazz)) {
            return UnorderedIndexStore.canonicalizeLongRepresentable(number.longValue());
        }
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
            Set<QueryableEntry> set = this.toSingleResultSet((Map)this.recordMap.get(this.canonicalize(value)));
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
                Map records = value == AbstractIndex.NULL ? this.recordsWithNullValue : (Map)this.recordMap.get(value);
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
    public Set<QueryableEntry> getRecords(Comparison comparison, Comparable value) {
        this.takeReadLock();
        try {
            MultiResultSet results = this.createMultiResultSet();
            for (Map.Entry recordMapEntry : this.recordMap.entrySet()) {
                Map records;
                boolean valid;
                Comparable indexedValue = (Comparable)recordMapEntry.getKey();
                int result = Comparables.compare(value, indexedValue);
                switch (comparison) {
                    case LESS: {
                        valid = result > 0;
                        break;
                    }
                    case LESS_OR_EQUAL: {
                        valid = result >= 0;
                        break;
                    }
                    case GREATER: {
                        valid = result < 0;
                        break;
                    }
                    case GREATER_OR_EQUAL: {
                        valid = result <= 0;
                        break;
                    }
                    case NOT_EQUAL: {
                        valid = result != 0;
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unrecognized comparison: " + (Object)((Object)comparison));
                    }
                }
                if (!valid || (records = (Map)recordMapEntry.getValue()) == null) continue;
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
    public Set<QueryableEntry> getRecords(Comparable from, boolean fromInclusive, Comparable to, boolean toInclusive) {
        this.takeReadLock();
        try {
            MultiResultSet results = this.createMultiResultSet();
            if (Comparables.compare(from, to) == 0) {
                if (!fromInclusive || !toInclusive) {
                    MultiResultSet multiResultSet = results;
                    return multiResultSet;
                }
                Map records = (Map)this.recordMap.get(this.canonicalize(from));
                if (records != null) {
                    this.copyToMultiResultSet(results, records);
                }
                MultiResultSet multiResultSet = results;
                return multiResultSet;
            }
            int fromBound = fromInclusive ? 0 : 1;
            int toBound = toInclusive ? 0 : -1;
            for (Map.Entry recordMapEntry : this.recordMap.entrySet()) {
                Map records;
                Comparable value = (Comparable)recordMapEntry.getKey();
                if (Comparables.compare(value, from) < fromBound || Comparables.compare(value, to) > toBound || (records = (Map)recordMapEntry.getValue()) == null) continue;
                this.copyToMultiResultSet(results, records);
            }
            MultiResultSet multiResultSet = results;
            return multiResultSet;
        }
        finally {
            this.releaseReadLock();
        }
    }

    private Comparable canonicalize(Comparable value) {
        if (value instanceof CompositeValue) {
            Comparable[] components = ((CompositeValue)value).getComponents();
            for (int i = 0; i < components.length; ++i) {
                components[i] = this.canonicalizeScalarForStorage(components[i]);
            }
            return value;
        }
        return this.canonicalizeScalarForStorage(value);
    }

    private static Comparable canonicalizeLongRepresentable(long value) {
        if (value == (long)((int)value)) {
            return Integer.valueOf((int)value);
        }
        return Long.valueOf(value);
    }

    private class CopyOnWriteRemoveFunctor
    implements BaseIndexStore.IndexFunctor<Comparable, Data> {
        private CopyOnWriteRemoveFunctor() {
        }

        @Override
        public Object invoke(Comparable value, Data indexKey) {
            Object oldValue;
            if (value == AbstractIndex.NULL) {
                HashMap copy = new HashMap(UnorderedIndexStore.this.recordsWithNullValue);
                oldValue = copy.remove(indexKey);
                UnorderedIndexStore.this.recordsWithNullValue = copy;
            } else {
                HashMap records = (HashMap)UnorderedIndexStore.this.recordMap.get(value);
                if (records != null) {
                    records = new HashMap(records);
                    oldValue = records.remove(indexKey);
                    if (records.isEmpty()) {
                        UnorderedIndexStore.this.recordMap.remove(value);
                    } else {
                        UnorderedIndexStore.this.recordMap.put(value, records);
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
                oldValue = UnorderedIndexStore.this.recordsWithNullValue.remove(indexKey);
            } else {
                Map records = (Map)UnorderedIndexStore.this.recordMap.get(value);
                if (records != null) {
                    oldValue = records.remove(indexKey);
                    if (records.size() == 0) {
                        UnorderedIndexStore.this.recordMap.remove(value);
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
                HashMap<Data, QueryableEntry> copy = new HashMap<Data, QueryableEntry>(UnorderedIndexStore.this.recordsWithNullValue);
                oldValue = copy.put(entry.getKeyData(), entry);
                UnorderedIndexStore.this.recordsWithNullValue = copy;
            } else {
                HashMap<Data, QueryableEntry> records = (HashMap<Data, QueryableEntry>)UnorderedIndexStore.this.recordMap.get(value);
                if (records == null) {
                    records = new HashMap<Data, QueryableEntry>();
                }
                records = new HashMap(records);
                oldValue = records.put(entry.getKeyData(), entry);
                UnorderedIndexStore.this.recordMap.put(value, records);
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
                return UnorderedIndexStore.this.recordsWithNullValue.put(entry.getKeyData(), entry);
            }
            ConcurrentHashMap<Data, QueryableEntry> records = (ConcurrentHashMap<Data, QueryableEntry>)UnorderedIndexStore.this.recordMap.get(value);
            if (records == null) {
                records = new ConcurrentHashMap<Data, QueryableEntry>(1, 0.75f, 1);
                UnorderedIndexStore.this.recordMap.put(value, records);
            }
            return records.put(entry.getKeyData(), entry);
        }
    }
}

