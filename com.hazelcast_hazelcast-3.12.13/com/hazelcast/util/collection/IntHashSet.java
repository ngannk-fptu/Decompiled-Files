/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.collection;

import com.hazelcast.util.Preconditions;
import com.hazelcast.util.QuickMath;
import com.hazelcast.util.collection.Hashing;
import com.hazelcast.util.collection.IntIterator;
import com.hazelcast.util.function.Predicate;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public final class IntHashSet
implements Set<Integer> {
    public static final int MAX_CAPACITY = 0x20000000;
    private final int[] values;
    private final IntIterator iterator;
    private final int capacity;
    private final int mask;
    private final int missingValue;
    private int size;

    public IntHashSet(int capacity, int missingValue) {
        Preconditions.checkTrue(capacity <= 0x20000000, "Maximum capacity is 2^29");
        this.capacity = capacity;
        this.size = 0;
        this.missingValue = missingValue;
        int arraySize = QuickMath.nextPowerOfTwo(2 * capacity);
        this.mask = arraySize - 1;
        this.values = new int[arraySize];
        Arrays.fill(this.values, missingValue);
        this.iterator = new IntIterator(missingValue, this.values);
    }

    @Override
    public boolean add(Integer value) {
        return this.add((int)value);
    }

    @Override
    public boolean add(int value) {
        if (this.size == this.capacity) {
            throw new IllegalStateException("This IntHashSet of capacity " + this.capacity + " is full");
        }
        int index = Hashing.intHash(value, this.mask);
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
        return value instanceof Integer && this.remove((Integer)value);
    }

    public boolean remove(int value) {
        int index = Hashing.intHash(value, this.mask);
        while (this.values[index] != this.missingValue) {
            if (this.values[index] == value) {
                this.values[index] = this.missingValue;
                this.compactChain(index);
                return true;
            }
            index = this.next(index);
        }
        return false;
    }

    private int next(int index) {
        ++index;
        return index &= this.mask;
    }

    private void compactChain(int deleteIndex) {
        int[] values = this.values;
        int index = deleteIndex;
        while (values[index = this.next(index)] != this.missingValue) {
            int hash = Hashing.intHash(values[index], this.mask);
            if ((index >= hash || hash > deleteIndex && deleteIndex > index) && (hash > deleteIndex || deleteIndex > index)) continue;
            values[deleteIndex] = values[index];
            values[index] = this.missingValue;
            deleteIndex = index;
        }
        return;
    }

    @Override
    public boolean contains(Object value) {
        return value instanceof Integer && this.contains((Integer)value);
    }

    public boolean contains(int value) {
        int index = Hashing.intHash(value, this.mask);
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
        int[] values = this.values;
        int length = values.length;
        for (int i = 0; i < length; ++i) {
            values[i] = this.missingValue;
        }
        this.size = 0;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> coll) {
        return this.addAllCapture(coll);
    }

    private <E extends Integer> boolean addAllCapture(Collection<E> coll) {
        Predicate p = new Predicate<E>(){

            @Override
            public boolean test(E x) {
                return IntHashSet.this.add((Integer)x);
            }
        };
        return IntHashSet.conjunction(coll, p);
    }

    @Override
    public boolean containsAll(Collection<?> coll) {
        return this.containsAllCapture(coll);
    }

    private <E> boolean containsAllCapture(Collection<E> coll) {
        return IntHashSet.conjunction(coll, new Predicate<E>(){

            @Override
            public boolean test(E value) {
                return IntHashSet.this.contains(value);
            }
        });
    }

    public boolean containsAll(IntHashSet other) {
        IntIterator iterator = other.iterator();
        while (iterator.hasNext()) {
            if (this.contains(iterator.nextValue())) continue;
            return false;
        }
        return true;
    }

    public IntHashSet difference(IntHashSet collection) {
        Preconditions.checkNotNull(collection, "Collection must not be null");
        IntHashSet difference = null;
        IntIterator it = this.iterator();
        while (it.hasNext()) {
            int value = it.nextValue();
            if (collection.contains(value)) continue;
            if (difference == null) {
                difference = new IntHashSet(this.size, this.missingValue);
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
        return IntHashSet.conjunction(coll, new Predicate<E>(){

            @Override
            public boolean test(E value) {
                return IntHashSet.this.remove(value);
            }
        });
    }

    private static <E> boolean conjunction(Collection<E> collection, Predicate<E> predicate) {
        Preconditions.checkNotNull(collection);
        boolean acc = false;
        for (E e : collection) {
            acc |= predicate.test(e);
        }
        return acc;
    }

    public IntIterator iterator() {
        this.iterator.reset();
        return this.iterator;
    }

    public void copy(IntHashSet obj) {
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
        for (int i : this.values) {
            b.append(i).append(separator);
            separator = ",";
        }
        return b.append('}').toString();
    }

    @Override
    public Object[] toArray() {
        int[] values = this.values;
        Object[] array = new Object[this.size];
        int i = 0;
        for (int value : values) {
            if (value == this.missingValue) continue;
            array[i++] = value;
        }
        return array;
    }

    @Override
    public <T> T[] toArray(T[] into) {
        Preconditions.checkNotNull(into);
        Class<Integer> aryType = into.getClass().getComponentType();
        if (!aryType.isAssignableFrom(Integer.class)) {
            throw new ArrayStoreException("Cannot store Integers in array of type " + aryType);
        }
        int[] values = this.values;
        T[] ret = into.length >= this.size ? into : (Object[])Array.newInstance(aryType, this.size);
        int i = 0;
        for (int value : values) {
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
        if (other instanceof IntHashSet) {
            IntHashSet otherSet = (IntHashSet)other;
            return otherSet.missingValue == this.missingValue && otherSet.size() == this.size() && this.containsAll(otherSet);
        }
        return false;
    }

    @Override
    public int hashCode() {
        IntIterator iterator = this.iterator();
        int total = 0;
        while (iterator.hasNext()) {
            total += iterator.nextValue();
        }
        return total;
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

