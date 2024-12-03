/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class SetAsList<E>
implements List<E> {
    private final Set<E> set;
    private transient Object[] array;

    public SetAsList(Set<E> set) {
        this.set = set;
    }

    @Override
    public int size() {
        return this.set.size();
    }

    @Override
    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.set.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return this.set.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.set.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return this.set.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return this.set.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.set.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return this.set.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("Delegates to set, operation not supported");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.set.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.set.retainAll(c);
    }

    @Override
    public void clear() {
        this.set.clear();
    }

    @Override
    public E get(int index) {
        if (this.array == null) {
            this.array = this.toArray();
        }
        if (this.array.length <= index) {
            throw new IndexOutOfBoundsException();
        }
        return (E)this.array[index];
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException("Delegates to set, operation not supported");
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException("Delegates to set, operation not supported");
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException("Delegates to set, operation not supported");
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("Delegates to set, operation not supported");
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("Delegates to set, operation not supported");
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException("Delegates to set, operation not supported");
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException("Delegates to set, operation not supported");
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Delegates to set, operation not supported");
    }
}

