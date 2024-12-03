/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.collection;

import com.hazelcast.util.JVMUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.QuickMath;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class OAHashSet<E>
extends AbstractSet<E> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.6f;
    private final float loadFactor;
    private int[] hashes;
    private Object[] table;
    private int resizeThreshold;
    private int capacity;
    private int mask;
    private int size;
    private int version;

    public OAHashSet() {
        this(16, 0.6f);
    }

    public OAHashSet(int initialCapacity) {
        this(initialCapacity, 0.6f);
    }

    public OAHashSet(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (loadFactor <= 0.0f || loadFactor >= 1.0f || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }
        this.capacity = QuickMath.nextPowerOfTwo(initialCapacity);
        this.loadFactor = loadFactor;
        this.resizeThreshold = (int)((float)this.capacity * loadFactor);
        this.mask = this.capacity - 1;
        this.hashes = new int[this.capacity];
        this.table = new Object[this.capacity];
    }

    @Override
    public boolean add(E element) {
        return this.add(element, element.hashCode());
    }

    public boolean add(E elementToAdd, int hash) {
        Preconditions.checkNotNull(elementToAdd);
        int index = hash & this.mask;
        while (this.hashes[index] != 0 || this.table[index] != null) {
            if (hash == this.hashes[index] && elementToAdd.equals(this.table[index])) {
                return false;
            }
            ++index;
            index &= this.mask;
        }
        ++this.size;
        ++this.version;
        this.table[index] = elementToAdd;
        this.hashes[index] = hash;
        if (this.size > this.resizeThreshold) {
            this.increaseCapacity();
        }
        return true;
    }

    @Override
    public boolean contains(Object objectToCheck) {
        return this.contains(objectToCheck, objectToCheck.hashCode());
    }

    public boolean contains(Object objectToCheck, int hash) {
        Preconditions.checkNotNull(objectToCheck);
        int index = hash & this.mask;
        while (this.hashes[index] != 0 || this.table[index] != null) {
            if (hash == this.hashes[index] && objectToCheck.equals(this.table[index])) {
                return true;
            }
            ++index;
            index &= this.mask;
        }
        return false;
    }

    @Override
    public boolean remove(Object objectToRemove) {
        return this.remove(objectToRemove, objectToRemove.hashCode());
    }

    public boolean remove(Object objectToRemove, int hash) {
        Preconditions.checkNotNull(objectToRemove);
        int index = hash & this.mask;
        while (this.hashes[index] != 0 || this.table[index] != null) {
            if (hash == this.hashes[index] && objectToRemove.equals(this.table[index])) {
                this.removeFromIndex(index);
                return true;
            }
            ++index;
            index &= this.mask;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> elementsToRemove) {
        boolean setChanged = false;
        for (Object objectToRemove : elementsToRemove) {
            setChanged |= this.remove(objectToRemove.hashCode());
        }
        return setChanged;
    }

    @Override
    public boolean retainAll(Collection<?> elementsToRetain) {
        boolean setChanged = false;
        int sizeBeforeRemovals = this.size;
        int visited = 0;
        for (int index = 0; index < this.table.length && visited < sizeBeforeRemovals; ++index) {
            Object storedElement = this.table[index];
            if (storedElement == null) continue;
            ++visited;
            if (elementsToRetain.contains(storedElement)) continue;
            this.removeFromIndex(index);
            setChanged = true;
        }
        return setChanged;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<E> iterator() {
        return new ElementIterator();
    }

    @Override
    public Object[] toArray() {
        return this.toArray(new Object[this.size]);
    }

    @Override
    public <T> T[] toArray(T[] array) {
        if (array.length < this.size) {
            array = new Object[this.size];
        }
        int arrIdx = 0;
        for (int i = 0; i < this.table.length && arrIdx < this.size; ++i) {
            if (this.table[i] == null) continue;
            array[arrIdx++] = this.table[i];
        }
        return array;
    }

    @Override
    public void clear() {
        this.size = 0;
        Arrays.fill(this.hashes, 0);
        Arrays.fill(this.table, null);
        ++this.version;
    }

    public int capacity() {
        return this.capacity;
    }

    public long footprint() {
        return 4 * this.hashes.length + JVMUtil.REFERENCE_COST_IN_BYTES * this.table.length + JVMUtil.REFERENCE_COST_IN_BYTES + JVMUtil.REFERENCE_COST_IN_BYTES + 4 + 4 + 4 + 4 + 4 + 4;
    }

    public float loadFactor() {
        return this.loadFactor;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (int hash : this.hashes) {
            hashCode += hash;
        }
        return hashCode;
    }

    private void increaseCapacity() {
        int newCapacity = this.capacity << 1;
        if (newCapacity < 0) {
            throw new IllegalStateException("Max capacity reached at size=" + this.size);
        }
        this.rehash(newCapacity);
    }

    private void rehash(int newCapacity) {
        if (1 != Integer.bitCount(newCapacity)) {
            throw new IllegalStateException("New capacity must be a power of two");
        }
        this.capacity = newCapacity;
        this.mask = newCapacity - 1;
        this.resizeThreshold = (int)((float)newCapacity * this.loadFactor);
        Object[] newTable = new Object[this.capacity];
        int[] newHashes = new int[this.capacity];
        for (int i = 0; i < this.table.length; ++i) {
            Object element = this.table[i];
            if (element == null) continue;
            int index = this.hashes[i] & this.mask;
            while (null != newTable[index]) {
                ++index;
                index &= this.mask;
            }
            newTable[index] = element;
            newHashes[index] = this.hashes[i];
        }
        this.table = newTable;
        this.hashes = newHashes;
    }

    private void removeFromIndex(int index) {
        this.hashes[index] = 0;
        this.table[index] = null;
        --this.size;
        ++this.version;
        this.compactChain(index);
    }

    private void compactChain(int indexOfRemoved) {
        int deleteIndex;
        int index = deleteIndex = indexOfRemoved;
        while (true) {
            ++index;
            if (null == this.table[index &= this.mask]) {
                return;
            }
            int hashedIndex = this.hashes[index] & this.mask;
            if ((index >= hashedIndex || hashedIndex > deleteIndex && deleteIndex > index) && (hashedIndex > deleteIndex || deleteIndex > index)) continue;
            this.hashes[deleteIndex] = this.hashes[index];
            this.table[deleteIndex] = this.table[index];
            this.hashes[index] = 0;
            this.table[index] = null;
            deleteIndex = index;
        }
    }

    private final class ElementIterator
    implements Iterator<E> {
        private final int expectedVersion;
        private int position;
        private int index;

        private ElementIterator() {
            this.expectedVersion = OAHashSet.this.version;
        }

        @Override
        public boolean hasNext() {
            return this.position < OAHashSet.this.size;
        }

        @Override
        public E next() {
            if (OAHashSet.this.version != this.expectedVersion) {
                throw new ConcurrentModificationException();
            }
            while (this.index < OAHashSet.this.table.length && this.position < OAHashSet.this.size) {
                if (OAHashSet.this.table[this.index] != null) {
                    ++this.position;
                    return OAHashSet.this.table[this.index++];
                }
                ++this.index;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}

