/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.internal.json.NonTerminalJsonValue;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.monitor.impl.IndexOperationStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.BaseIndexStore;
import com.hazelcast.query.impl.Comparison;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.query.impl.IndexDefinition;
import com.hazelcast.query.impl.Numbers;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.bitmap.Bitmap;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.query.impl.getters.MultiResult;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import com.hazelcast.query.impl.predicates.InPredicate;
import com.hazelcast.query.impl.predicates.NotEqualPredicate;
import com.hazelcast.query.impl.predicates.NotPredicate;
import com.hazelcast.query.impl.predicates.OrPredicate;
import com.hazelcast.util.collection.Long2LongHashMap;
import com.hazelcast.util.collection.Object2LongHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class BitmapIndexStore
extends BaseIndexStore {
    private static final long NO_KEY = -1L;
    private static final int INITIAL_CAPACITY = 8;
    private static final float LOAD_FACTOR = 0.75f;
    private static final Object CONSUMED = new Object();
    private static final Set<Class<? extends Predicate>> EVALUABLE_PREDICATES = new HashSet<Class<? extends Predicate>>();
    private final String keyAttribute;
    private final InternalSerializationService serializationService;
    private final Extractors extractors;
    private final Bitmap<QueryableEntry> bitmap = new Bitmap();
    private final Long2LongHashMap internalKeys;
    private final Object2LongHashMap internalObjectKeys;
    private long internalKeyCounter;

    public BitmapIndexStore(IndexDefinition definition, InternalSerializationService serializationService, Extractors extractors) {
        super(IndexCopyBehavior.NEVER);
        if (definition.isOrdered()) {
            throw new IllegalArgumentException("Ordered bitmap indexes are not supported");
        }
        this.keyAttribute = definition.getUniqueKey();
        this.serializationService = serializationService;
        this.extractors = extractors;
        switch (definition.getUniqueKeyTransform()) {
            case OBJECT: {
                this.internalObjectKeys = new Object2LongHashMap(8, 0.75f, -1L);
                this.internalKeys = null;
                break;
            }
            case LONG: {
                this.internalKeys = new Long2LongHashMap(8, 0.75, -1L);
                this.internalObjectKeys = null;
                break;
            }
            case RAW: {
                this.internalKeys = null;
                this.internalObjectKeys = null;
                break;
            }
            default: {
                throw new IllegalArgumentException("unexpected unique key transform: " + (Object)((Object)definition.getUniqueKeyTransform()));
            }
        }
    }

    @Override
    public Comparable canonicalizeQueryArgumentScalar(Comparable value) {
        return this.canonicalizeScalarForStorage(value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insert(Object value, QueryableEntry entry, IndexOperationStats operationStats) {
        if (value == NonTerminalJsonValue.INSTANCE) {
            return;
        }
        if (this.internalObjectKeys == null) {
            long key = this.extractLongKey(entry);
            Iterator values = this.makeIterator(value);
            this.takeWriteLock();
            try {
                if (this.internalKeys != null) {
                    long internalKey = this.internalKeyCounter++;
                    long replaced = this.internalKeys.put(key, internalKey);
                    assert (replaced == -1L);
                    key = internalKey;
                } else if (key < 0L) {
                    throw this.makeNegativeKeyException(key);
                }
                this.bitmap.insert(values, key, entry);
            }
            finally {
                this.releaseWriteLock();
            }
        }
        Object key = this.extractObjectKey(entry);
        Iterator values = this.makeIterator(value);
        this.takeWriteLock();
        try {
            long internalKey = this.internalKeyCounter++;
            long replaced = this.internalObjectKeys.put(key, internalKey);
            assert (replaced == -1L);
            this.bitmap.insert(values, internalKey, entry);
        }
        finally {
            this.releaseWriteLock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void update(Object oldValue, Object newValue, QueryableEntry entry, IndexOperationStats operationStats) {
        if (oldValue == NonTerminalJsonValue.INSTANCE) {
            this.insert(newValue, entry, operationStats);
            return;
        }
        if (this.internalObjectKeys == null) {
            long key = this.extractLongKey(entry);
            Iterator oldValues = this.makeIterator(oldValue);
            Iterator newValues = this.makeIterator(newValue);
            this.takeWriteLock();
            try {
                if (this.internalKeys != null) {
                    key = this.internalKeys.get(key);
                    assert (key != -1L);
                } else if (key < 0L) {
                    throw this.makeNegativeKeyException(key);
                }
                this.bitmap.update(oldValues, newValues, key, entry);
            }
            finally {
                this.releaseWriteLock();
            }
        }
        Object key = this.extractObjectKey(entry);
        Iterator oldValues = this.makeIterator(oldValue);
        Iterator newValues = this.makeIterator(newValue);
        this.takeWriteLock();
        try {
            long internalKey = this.internalObjectKeys.getValue(key);
            assert (internalKey != -1L);
            this.bitmap.update(oldValues, newValues, internalKey, entry);
        }
        finally {
            this.releaseWriteLock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void remove(Object value, Data entryKey, Object entryValue, IndexOperationStats operationStats) {
        if (value == NonTerminalJsonValue.INSTANCE) {
            return;
        }
        if (this.internalObjectKeys == null) {
            long key = this.extractLongKey(entryKey, entryValue);
            Iterator values = this.makeIterator(value);
            this.takeWriteLock();
            try {
                if (this.internalKeys != null) {
                    if ((key = this.internalKeys.remove(key)) == -1L) return;
                    this.bitmap.remove(values, key);
                    return;
                }
                if (key < 0L) {
                    throw this.makeNegativeKeyException(key);
                }
                this.bitmap.remove(values, key);
                return;
            }
            finally {
                this.releaseWriteLock();
            }
        }
        Object key = this.extractObjectKey(entryKey, entryValue);
        Iterator values = this.makeIterator(value);
        this.takeWriteLock();
        try {
            long internalKey = this.internalObjectKeys.removeKey(key);
            if (internalKey == -1L) return;
            this.bitmap.remove(values, internalKey);
            return;
        }
        finally {
            this.releaseWriteLock();
        }
    }

    @Override
    public void clear() {
        this.takeWriteLock();
        try {
            this.bitmap.clear();
            if (this.internalKeys != null) {
                this.internalKeys.clear();
            }
            if (this.internalObjectKeys != null) {
                this.internalObjectKeys.clear();
            }
            this.internalKeyCounter = 0L;
        }
        finally {
            this.releaseWriteLock();
        }
    }

    @Override
    public boolean isEvaluateOnly() {
        return true;
    }

    @Override
    public boolean canEvaluate(Class<? extends Predicate> predicateClass) {
        return EVALUABLE_PREDICATES.contains(predicateClass);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<QueryableEntry> evaluate(Predicate predicate, TypeConverter converter) {
        this.takeReadLock();
        try {
            Set<QueryableEntry> set = this.toSingleResultSet(this.toMap(this.bitmap.evaluate(predicate, new CanonicalizingConverter(converter))));
            return set;
        }
        finally {
            this.releaseReadLock();
        }
    }

    @Override
    public Set<QueryableEntry> getRecords(Comparable value) {
        throw BitmapIndexStore.makeUnsupportedOperationException();
    }

    @Override
    public Set<QueryableEntry> getRecords(Set<Comparable> values) {
        throw BitmapIndexStore.makeUnsupportedOperationException();
    }

    @Override
    public Set<QueryableEntry> getRecords(Comparison comparison, Comparable value) {
        throw BitmapIndexStore.makeUnsupportedOperationException();
    }

    @Override
    public Set<QueryableEntry> getRecords(Comparable from, boolean fromInclusive, Comparable to, boolean toInclusive) {
        throw BitmapIndexStore.makeUnsupportedOperationException();
    }

    @Override
    Comparable canonicalizeScalarForStorage(Comparable value) {
        if (!(value instanceof Number)) {
            return value;
        }
        Class<?> clazz = value.getClass();
        Number number = (Number)((Object)value);
        if (clazz == Double.class) {
            long longValue;
            double doubleValue = number.doubleValue();
            if (Numbers.equalDoubles(doubleValue, longValue = number.longValue())) {
                return BitmapIndexStore.canonicalizeLongRepresentable(longValue);
            }
            float floatValue = number.floatValue();
            if (doubleValue == (double)floatValue) {
                return Float.valueOf(floatValue);
            }
        } else if (clazz == Float.class) {
            long longValue;
            float floatValue = number.floatValue();
            if (Numbers.equalFloats(floatValue, longValue = number.longValue())) {
                return BitmapIndexStore.canonicalizeLongRepresentable(longValue);
            }
        } else if (Numbers.isLongRepresentable(clazz)) {
            return BitmapIndexStore.canonicalizeLongRepresentable(number.longValue());
        }
        return value;
    }

    private Map<Data, QueryableEntry> toMap(Iterator<QueryableEntry> iterator) {
        HashMap<Data, QueryableEntry> map = new HashMap<Data, QueryableEntry>();
        while (iterator.hasNext()) {
            QueryableEntry entry = iterator.next();
            map.put(entry.getKeyData(), entry);
        }
        return map;
    }

    private long extractLongKey(Data entryKey, Object entryValue) {
        Object key = QueryableEntry.extractAttributeValue(this.extractors, this.serializationService, this.keyAttribute, entryKey, entryValue, null);
        return BitmapIndexStore.extractLongKey(key);
    }

    private long extractLongKey(QueryableEntry entry) {
        Object key = entry.getAttributeValue(this.keyAttribute);
        return BitmapIndexStore.extractLongKey(key);
    }

    private Object extractObjectKey(Data entryKey, Object entryValue) {
        Object key = QueryableEntry.extractAttributeValue(this.extractors, this.serializationService, this.keyAttribute, entryKey, entryValue, null);
        return BitmapIndexStore.extractObjectKey(key);
    }

    private Object extractObjectKey(QueryableEntry entry) {
        Object key = entry.getAttributeValue(this.keyAttribute);
        return BitmapIndexStore.extractObjectKey(key);
    }

    private Iterator makeIterator(Object value) {
        return value instanceof MultiResult ? new MultiValueIterator((MultiResult)value) : new SingleValueIterator(value);
    }

    private static Comparable canonicalizeLongRepresentable(long value) {
        if (value == (long)((int)value)) {
            return Integer.valueOf((int)value);
        }
        return Long.valueOf(value);
    }

    private Comparable canonicalize(Comparable value) {
        return this.canonicalizeScalarForStorage(value);
    }

    private static UnsupportedOperationException makeUnsupportedOperationException() {
        return new UnsupportedOperationException("bitmap indexes support only direct predicate evaluation");
    }

    private static long extractLongKey(Object key) {
        if (key == null) {
            throw new NullPointerException("non-null unique key value is required");
        }
        if (!Numbers.isLongRepresentable(key.getClass())) {
            throw new IllegalArgumentException("integer-valued unique key value is required");
        }
        return ((Number)key).longValue();
    }

    private static Object extractObjectKey(Object key) {
        if (key == null) {
            throw new NullPointerException("non-null unique key value is required");
        }
        return key;
    }

    private IllegalArgumentException makeNegativeKeyException(long key) {
        return new IllegalArgumentException("negative keys are not supported: " + this.keyAttribute + " = " + key);
    }

    static {
        EVALUABLE_PREDICATES.add(AndPredicate.class);
        EVALUABLE_PREDICATES.add(OrPredicate.class);
        EVALUABLE_PREDICATES.add(NotPredicate.class);
        EVALUABLE_PREDICATES.add(EqualPredicate.class);
        EVALUABLE_PREDICATES.add(NotEqualPredicate.class);
        EVALUABLE_PREDICATES.add(InPredicate.class);
    }

    private final class CanonicalizingConverter
    implements TypeConverter {
        private final TypeConverter converter;

        CanonicalizingConverter(TypeConverter converter) {
            this.converter = converter;
        }

        @Override
        public Comparable convert(Comparable value) {
            return BitmapIndexStore.this.canonicalize(this.converter.convert(value));
        }
    }

    private final class SingleValueIterator
    implements Iterator {
        private Object value;

        SingleValueIterator(Object value) {
            this.value = value;
        }

        @Override
        public boolean hasNext() {
            return this.value != CONSUMED;
        }

        public Object next() {
            Comparable value = BitmapIndexStore.this.sanitizeValue(this.value);
            this.value = CONSUMED;
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private final class MultiValueIterator
    implements Iterator {
        private final Iterator iterator;

        MultiValueIterator(MultiResult multiResult) {
            this.iterator = multiResult.getResults().iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public Object next() {
            return BitmapIndexStore.this.sanitizeValue(this.iterator.next());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

