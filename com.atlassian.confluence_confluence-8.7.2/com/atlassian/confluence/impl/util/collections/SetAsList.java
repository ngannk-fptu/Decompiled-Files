/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.NotImplementedException
 */
package com.atlassian.confluence.impl.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.NotImplementedException;

public class SetAsList<E>
implements List<E> {
    private final Set<E> set;
    private int currentGetIndex;
    private Iterator<E> currentGetIterator;

    public SetAsList(Set<E> set) {
        Objects.requireNonNull(set, "set");
        this.set = set;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof SetAsList && this.set.equals(((SetAsList)other).set);
    }

    @Override
    public int hashCode() {
        return this.set.hashCode();
    }

    @Override
    public E get(int index) {
        if (index >= this.set.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.set.size());
        }
        if (index == 0) {
            this.currentGetIndex = 0;
            this.currentGetIterator = this.set.iterator();
            return this.currentGetIterator.next();
        }
        if (this.currentGetIterator != null && this.currentGetIndex == index - 1 && this.currentGetIterator.hasNext()) {
            this.currentGetIndex = index;
            E item = this.currentGetIterator.next();
            if (index == this.set.size() - 1) {
                this.currentGetIterator = null;
            }
            return item;
        }
        this.currentGetIterator = this.set.iterator();
        for (int i = 0; i < index; ++i) {
            this.currentGetIterator.next();
        }
        this.currentGetIndex = index;
        return this.currentGetIterator.next();
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
    public boolean add(E o) {
        this.currentGetIterator = null;
        return this.set.add(o);
    }

    @Override
    public boolean remove(Object o) {
        this.currentGetIterator = null;
        return this.set.remove(o);
    }

    @Override
    public boolean addAll(Collection c) {
        this.currentGetIterator = null;
        return this.set.addAll(c);
    }

    @Override
    public void clear() {
        this.currentGetIterator = null;
        this.set.clear();
    }

    @Override
    public int indexOf(Object o) {
        int i = 0;
        for (E item : this.set) {
            if (Objects.equals(item, o)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.set.containsAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        this.currentGetIterator = null;
        return this.set.removeAll(c);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.set.toArray(a);
    }

    @Override
    public boolean addAll(int index, Collection c) {
        throw new NotImplementedException("addAll(int, Collection) not implemented");
    }

    @Override
    public E set(int index, E element) {
        throw new NotImplementedException("set not implemented");
    }

    @Override
    public void add(int index, E element) {
        throw new NotImplementedException("add(int, Object) not implemented");
    }

    @Override
    public E remove(int index) {
        throw new NotImplementedException("remove not implemented");
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new NotImplementedException("lastIndexOf");
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new NotImplementedException("listIterator");
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new NotImplementedException("listIterator");
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new NotImplementedException("listIterator");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new NotImplementedException("listIterator");
    }
}

