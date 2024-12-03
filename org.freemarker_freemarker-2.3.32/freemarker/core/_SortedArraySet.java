/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core._ArrayIterator;
import freemarker.core._UnmodifiableSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class _SortedArraySet<E>
extends _UnmodifiableSet<E> {
    private final E[] array;

    public _SortedArraySet(E[] array) {
        this.array = array;
    }

    @Override
    public int size() {
        return this.array.length;
    }

    @Override
    public boolean contains(Object o) {
        return Arrays.binarySearch(this.array, o) >= 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new _ArrayIterator(this.array);
    }

    @Override
    public boolean add(E o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}

