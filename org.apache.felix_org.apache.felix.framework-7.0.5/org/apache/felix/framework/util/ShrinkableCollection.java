/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util;

import java.util.Collection;
import java.util.Iterator;

public class ShrinkableCollection<T>
implements Collection<T> {
    private final Collection<T> m_delegate;

    public ShrinkableCollection(Collection<T> delegate) {
        this.m_delegate = delegate;
    }

    @Override
    public boolean add(T o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        this.m_delegate.clear();
    }

    @Override
    public boolean contains(Object o) {
        return this.m_delegate.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.m_delegate.containsAll(c);
    }

    @Override
    public boolean equals(Object o) {
        return this.m_delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return this.m_delegate.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return this.m_delegate.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return this.m_delegate.iterator();
    }

    @Override
    public boolean remove(Object o) {
        return this.m_delegate.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.m_delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.m_delegate.retainAll(c);
    }

    @Override
    public int size() {
        return this.m_delegate.size();
    }

    @Override
    public Object[] toArray() {
        return this.m_delegate.toArray();
    }

    @Override
    public <A> A[] toArray(A[] a) {
        return this.m_delegate.toArray(a);
    }
}

