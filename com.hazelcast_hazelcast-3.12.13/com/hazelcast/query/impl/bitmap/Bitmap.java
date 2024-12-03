/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.bitmap;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.bitmap.AscendingLongIterator;
import com.hazelcast.query.impl.bitmap.BitmapAlgorithms;
import com.hazelcast.query.impl.bitmap.SparseArray;
import com.hazelcast.query.impl.bitmap.SparseBitSet;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import com.hazelcast.query.impl.predicates.InPredicate;
import com.hazelcast.query.impl.predicates.NotEqualPredicate;
import com.hazelcast.query.impl.predicates.NotPredicate;
import com.hazelcast.query.impl.predicates.OrPredicate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Bitmap<E> {
    private final Map<Object, SparseBitSet> bitSets = new HashMap<Object, SparseBitSet>();
    private final SparseArray<E> entries = new SparseArray();

    public void insert(Iterator values, long key, E entry) {
        while (values.hasNext()) {
            Object value = values.next();
            assert (value != null);
            SparseBitSet bitSet = this.bitSets.get(value);
            if (bitSet == null) {
                bitSet = new SparseBitSet();
                this.bitSets.put(value, bitSet);
            }
            bitSet.add(key);
        }
        this.entries.set(key, entry);
    }

    public void update(Iterator oldValues, Iterator newValues, long key, E entry) {
        SparseBitSet bitSet;
        Object value;
        while (oldValues.hasNext()) {
            value = oldValues.next();
            assert (value != null);
            bitSet = this.bitSets.get(value);
            if (bitSet == null) continue;
            bitSet.remove(key);
        }
        while (newValues.hasNext()) {
            value = newValues.next();
            assert (value != null);
            bitSet = this.bitSets.get(value);
            if (bitSet == null) {
                bitSet = new SparseBitSet();
                this.bitSets.put(value, bitSet);
            }
            bitSet.add(key);
        }
        this.entries.set(key, entry);
    }

    public void remove(Iterator values, long key) {
        while (values.hasNext()) {
            Object value = values.next();
            assert (value != null);
            SparseBitSet bitSet = this.bitSets.get(value);
            if (bitSet == null || !bitSet.remove(key)) continue;
            this.bitSets.remove(value);
        }
        this.entries.clear(key);
    }

    public void clear() {
        this.bitSets.clear();
        this.entries.clear();
    }

    public Iterator<E> evaluate(Predicate predicate, TypeConverter converter) {
        return new EntryIterator<E>(this.predicateIterator(predicate, converter), this.entries.iterator());
    }

    private AscendingLongIterator predicateIterator(Predicate predicate, TypeConverter converter) {
        if (predicate instanceof AndPredicate) {
            Predicate[] predicates = ((AndPredicate)predicate).getPredicates();
            assert (predicates.length > 0);
            if (predicates.length == 1) {
                return this.predicateIterator(predicates[0], converter);
            }
            return BitmapAlgorithms.and(this.predicateIterators(predicates, converter));
        }
        if (predicate instanceof OrPredicate) {
            Predicate[] predicates = ((OrPredicate)predicate).getPredicates();
            assert (predicates.length > 0);
            if (predicates.length == 1) {
                return this.predicateIterator(predicates[0], converter);
            }
            return BitmapAlgorithms.or(this.predicateIterators(predicates, converter));
        }
        if (predicate instanceof NotPredicate) {
            Predicate subPredicate = ((NotPredicate)predicate).getPredicate();
            return BitmapAlgorithms.not(this.predicateIterator(subPredicate, converter), this.entries);
        }
        if (predicate instanceof NotEqualPredicate) {
            Comparable value = ((NotEqualPredicate)predicate).getFrom();
            return BitmapAlgorithms.not(this.valueIterator(value, converter), this.entries);
        }
        if (predicate instanceof EqualPredicate) {
            Comparable value = ((EqualPredicate)predicate).getFrom();
            return this.valueIterator(value, converter);
        }
        if (predicate instanceof InPredicate) {
            Comparable[] values = ((InPredicate)predicate).getValues();
            return BitmapAlgorithms.or(this.valueIterators(values, converter));
        }
        throw new IllegalArgumentException("unexpected predicate: " + predicate);
    }

    private AscendingLongIterator[] predicateIterators(Predicate[] predicates, TypeConverter converter) {
        AscendingLongIterator[] iterators = new AscendingLongIterator[predicates.length];
        for (int i = 0; i < predicates.length; ++i) {
            iterators[i] = this.predicateIterator(predicates[i], converter);
        }
        return iterators;
    }

    private AscendingLongIterator valueIterator(Comparable value, TypeConverter converter) {
        SparseBitSet bitSet = this.bitSets.get(converter.convert(value));
        return bitSet == null ? AscendingLongIterator.EMPTY : bitSet.iterator();
    }

    private AscendingLongIterator[] valueIterators(Comparable[] values, TypeConverter converter) {
        AscendingLongIterator[] iterators = new AscendingLongIterator[values.length];
        for (int i = 0; i < values.length; ++i) {
            iterators[i] = this.valueIterator(values[i], converter);
        }
        return iterators;
    }

    private static final class EntryIterator<E>
    implements Iterator<E> {
        private final AscendingLongIterator iterator;
        private final SparseArray.Iterator<E> universe;

        EntryIterator(AscendingLongIterator iterator, SparseArray.Iterator<E> universe) {
            this.iterator = iterator;
            this.universe = universe;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.getIndex() != -1L;
        }

        @Override
        public E next() {
            long member = this.iterator.advance();
            long advancedTo = this.universe.advanceAtLeastTo(member);
            assert (advancedTo == member);
            return this.universe.getValue();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("bitmap iterators are read-only");
        }
    }
}

