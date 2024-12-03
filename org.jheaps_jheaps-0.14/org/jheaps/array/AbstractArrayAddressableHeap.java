/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.array;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.NoSuchElementException;
import org.jheaps.AddressableHeap;
import org.jheaps.annotations.ConstantTime;
import org.jheaps.annotations.LogarithmicTime;

abstract class AbstractArrayAddressableHeap<K, V>
implements AddressableHeap<K, V>,
Serializable {
    private static final long serialVersionUID = 1L;
    protected static final int NO_INDEX = -1;
    protected static final int MAX_HEAP_CAPACITY = 0x7FFFFFF6;
    protected static final int MIN_HEAP_CAPACITY = 0;
    protected static final int DOWNSIZING_MIN_HEAP_CAPACITY = 16;
    protected Comparator<? super K> comparator;
    protected ArrayHandle[] array;
    protected int size;
    protected final int minCapacity;

    public AbstractArrayAddressableHeap(Comparator<? super K> comparator, int capacity) {
        this.checkCapacity(capacity);
        this.size = 0;
        this.comparator = comparator;
        this.minCapacity = Math.max(capacity, 16);
        this.array = (ArrayHandle[])Array.newInstance(ArrayHandle.class, this.minCapacity + 1);
    }

    @Override
    @ConstantTime
    public AddressableHeap.Handle<K, V> findMin() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.array[1];
    }

    @Override
    @ConstantTime
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    @ConstantTime
    public long size() {
        return this.size;
    }

    @Override
    public Comparator<? super K> comparator() {
        return this.comparator;
    }

    @Override
    @ConstantTime
    public void clear() {
        this.size = 0;
    }

    @Override
    @LogarithmicTime(amortized=true)
    public AddressableHeap.Handle<K, V> insert(K key) {
        return this.insert(key, null);
    }

    @Override
    @LogarithmicTime(amortized=true)
    public AddressableHeap.Handle<K, V> insert(K key, V value) {
        if (key == null) {
            throw new NullPointerException("Null keys not permitted");
        }
        if (this.size == this.array.length - 1) {
            if (this.array.length == 1) {
                this.ensureCapacity(1);
            } else {
                this.ensureCapacity(2 * (this.array.length - 1));
            }
        }
        ArrayHandle p = new ArrayHandle(key, value);
        ++this.size;
        this.array[this.size] = p;
        p.index = this.size;
        if (this.comparator == null) {
            this.fixup(this.size);
        } else {
            this.fixupWithComparator(this.size);
        }
        return p;
    }

    @Override
    @LogarithmicTime(amortized=true)
    public AddressableHeap.Handle<K, V> deleteMin() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        ArrayHandle result = this.array[1];
        result.index = -1;
        if (this.size == 1) {
            this.array[1] = null;
            this.size = 0;
        } else {
            this.array[1] = this.array[this.size--];
            if (this.comparator == null) {
                this.fixdown(1);
            } else {
                this.fixdownWithComparator(1);
            }
        }
        if (2 * this.minCapacity < this.array.length - 1 && 4 * this.size < this.array.length - 1) {
            this.ensureCapacity((this.array.length - 1) / 2);
        }
        return result;
    }

    protected final void checkCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Heap capacity must be >= 0");
        }
        if (capacity > 0x7FFFFFF6) {
            throw new IllegalArgumentException("Heap capacity too large");
        }
    }

    protected abstract void ensureCapacity(int var1);

    protected abstract void forceFixup(int var1);

    protected abstract void fixup(int var1);

    protected abstract void fixupWithComparator(int var1);

    protected abstract void fixdown(int var1);

    protected abstract void fixdownWithComparator(int var1);

    protected class ArrayHandle
    implements AddressableHeap.Handle<K, V>,
    Serializable {
        private static final long serialVersionUID = 1L;
        K key;
        V value;
        int index;

        ArrayHandle(K key, V value) {
            this.key = key;
            this.value = value;
            this.index = -1;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public void setValue(V value) {
            this.value = value;
        }

        @Override
        @LogarithmicTime
        public void decreaseKey(K newKey) {
            if (this.index == -1) {
                throw new IllegalArgumentException("Invalid handle!");
            }
            int c = AbstractArrayAddressableHeap.this.comparator == null ? ((Comparable)newKey).compareTo(this.key) : AbstractArrayAddressableHeap.this.comparator.compare(newKey, this.key);
            if (c > 0) {
                throw new IllegalArgumentException("Keys can only be decreased!");
            }
            this.key = newKey;
            if (c == 0 || this.index == 1) {
                return;
            }
            if (AbstractArrayAddressableHeap.this.comparator == null) {
                AbstractArrayAddressableHeap.this.fixup(this.index);
            } else {
                AbstractArrayAddressableHeap.this.fixupWithComparator(this.index);
            }
        }

        @Override
        public void delete() {
            if (this.index == -1) {
                throw new IllegalArgumentException("Invalid handle!");
            }
            if (this.index == 1) {
                AbstractArrayAddressableHeap.this.deleteMin();
                return;
            }
            AbstractArrayAddressableHeap.this.forceFixup(this.index);
            AbstractArrayAddressableHeap.this.deleteMin();
        }
    }
}

