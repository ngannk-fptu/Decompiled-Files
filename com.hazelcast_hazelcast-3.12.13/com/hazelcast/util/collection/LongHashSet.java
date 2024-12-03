/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.collection;

import com.hazelcast.util.Preconditions;
import com.hazelcast.util.QuickMath;
import com.hazelcast.util.collection.Hashing;
import com.hazelcast.util.collection.LongIterator;
import com.hazelcast.util.function.Predicate;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public final class LongHashSet
implements Set<Long> {
    public static final int MAX_CAPACITY = 0x20000000;
    private final long[] values;
    private final LongIterator iterator;
    private final int capacity;
    private final int mask;
    private final long missingValue;
    private int size;

    public LongHashSet(int capacity, long missingValue) {
        Preconditions.checkTrue(capacity <= 0x20000000, "Maximum capacity is 2^29");
        this.capacity = capacity;
        this.size = 0;
        this.missingValue = missingValue;
        int arraySize = QuickMath.nextPowerOfTwo(2 * capacity);
        this.mask = arraySize - 1;
        this.values = new long[arraySize];
        Arrays.fill(this.values, missingValue);
        this.iterator = new LongIterator(missingValue, this.values);
    }

    public LongHashSet(long[] items, long missingValue) {
        this(items.length, missingValue);
        for (long item : items) {
            this.add(item);
        }
    }

    @Override
    public boolean add(Long value) {
        return this.add((long)value);
    }

    @Override
    public boolean add(long value) {
        if (this.size == this.capacity) {
            throw new IllegalStateException("This LongHashSet of capacity " + this.capacity + " is full");
        }
        int index = Hashing.longHash(value, this.mask);
        while (this.values[index] != this.missingValue) {
            if (this.values[index] == value) {
                return false;
            }
            index = this.next(index);
        }
        this.values[index] = value;
        ++this.size;
        return true;
    }

    @Override
    public boolean remove(Object value) {
        return value instanceof Long && this.remove((Long)value);
    }

    public boolean remove(long value) {
        int index = Hashing.longHash(value, this.mask);
        while (this.values[index] != this.missingValue) {
            if (this.values[index] == value) {
                this.values[index] = this.missingValue;
                this.compactChain(index);
                --this.size;
                return true;
            }
            index = this.next(index);
        }
        return false;
    }

    private int next(int index) {
        return index + 1 & this.mask;
    }

    private void compactChain(int deleteIndex) {
        long[] values = this.values;
        int index = deleteIndex;
        while (values[index = this.next(index)] != this.missingValue) {
            int hash = Hashing.longHash(values[index], this.mask);
            if ((index >= hash || hash > deleteIndex && deleteIndex > index) && (hash > deleteIndex || deleteIndex > index)) continue;
            values[deleteIndex] = values[index];
            values[index] = this.missingValue;
            deleteIndex = index;
        }
        return;
    }

    @Override
    public boolean contains(Object value) {
        return value instanceof Long && this.contains((Long)value);
    }

    public boolean contains(long value) {
        int index = Hashing.longHash(value, this.mask);
        while (this.values[index] != this.missingValue) {
            if (this.values[index] == value) {
                return true;
            }
            index = this.next(index);
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public void clear() {
        long[] values = this.values;
        int length = values.length;
        for (int i = 0; i < length; ++i) {
            values[i] = this.missingValue;
        }
        this.size = 0;
    }

    @Override
    public boolean addAll(Collection<? extends Long> coll) {
        return this.addAllCapture(coll);
    }

    private <E extends Long> boolean addAllCapture(Collection<E> coll) {
        Predicate p = new Predicate<E>(){

            @Override
            public boolean test(E x) {
                return LongHashSet.this.add((Long)x);
            }
        };
        return LongHashSet.conjunction(coll, p);
    }

    @Override
    public boolean containsAll(Collection<?> coll) {
        return this.containsAllCapture(coll);
    }

    private <E> boolean containsAllCapture(Collection<E> coll) {
        return LongHashSet.conjunction(coll, new Predicate<E>(){

            @Override
            public boolean test(E value) {
                return LongHashSet.this.contains(value);
            }
        });
    }

    public boolean containsAll(LongHashSet other) {
        LongIterator iterator = other.iterator();
        while (iterator.hasNext()) {
            if (this.contains(iterator.nextValue())) continue;
            return false;
        }
        return true;
    }

    public LongHashSet difference(LongHashSet collection) {
        Preconditions.checkNotNull(collection);
        LongHashSet difference = null;
        LongIterator it = this.iterator();
        while (it.hasNext()) {
            long value = it.nextValue();
            if (collection.contains(value)) continue;
            if (difference == null) {
                difference = new LongHashSet(this.size, this.missingValue);
            }
            difference.add(value);
        }
        return difference;
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        return this.removeAllCapture(coll);
    }

    private <E> boolean removeAllCapture(Collection<E> coll) {
        return LongHashSet.conjunction(coll, new Predicate<E>(){

            @Override
            public boolean test(E value) {
                return LongHashSet.this.remove(value);
            }
        });
    }

    private static <T> boolean conjunction(Collection<T> collection, Predicate<T> predicate) {
        Preconditions.checkNotNull(collection);
        boolean acc = false;
        for (T t : collection) {
            acc |= predicate.test(t);
        }
        return acc;
    }

    public LongIterator iterator() {
        this.iterator.reset();
        return this.iterator;
    }

    public void copy(LongHashSet obj) {
        if (this.mask != obj.mask) {
            throw new IllegalArgumentException("Cannot copy object: masks not equal");
        }
        if (this.missingValue != obj.missingValue) {
            throw new IllegalArgumentException("Cannot copy object: missingValues not equal");
        }
        System.arraycopy(obj.values, 0, this.values, 0, this.values.length);
        this.size = obj.size;
    }

    public String toString() {
        StringBuilder b = new StringBuilder(this.size() * 3 + 2);
        b.append('{');
        String separator = "";
        for (long i : this.values) {
            if (i == this.missingValue) continue;
            b.append(separator).append(i);
            separator = ",";
        }
        return b.append('}').toString();
    }

    @Override
    public Object[] toArray() {
        long[] values = this.values;
        Object[] array = new Object[this.size];
        int i = 0;
        for (long value : values) {
            if (value == this.missingValue) continue;
            array[i++] = value;
        }
        return array;
    }

    @Override
    public <T> T[] toArray(T[] into) {
        Preconditions.checkNotNull(into);
        Class<Long> aryType = into.getClass().getComponentType();
        if (!aryType.isAssignableFrom(Long.class)) {
            throw new ArrayStoreException("Cannot store Longs in array of type " + aryType);
        }
        long[] values = this.values;
        T[] ret = into.length >= this.size ? into : (Object[])Array.newInstance(aryType, this.size);
        int i = 0;
        for (long value : values) {
            if (value == this.missingValue) continue;
            ret[i++] = value;
        }
        if (ret.length > this.size) {
            ret[values.length] = null;
        }
        return ret;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof LongHashSet) {
            LongHashSet otherSet = (LongHashSet)other;
            return otherSet.missingValue == this.missingValue && otherSet.size() == this.size() && this.containsAll(otherSet);
        }
        return false;
    }

    @Override
    public int hashCode() {
        LongIterator iterator = this.iterator();
        int total = 0;
        while (iterator.hasNext()) {
            total = (int)((long)total + iterator.nextValue());
        }
        return total;
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

