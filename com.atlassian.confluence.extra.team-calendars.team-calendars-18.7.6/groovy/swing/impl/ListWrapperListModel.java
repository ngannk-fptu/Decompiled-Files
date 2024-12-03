/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.AbstractListModel;

public class ListWrapperListModel<E>
extends AbstractListModel {
    private final List<E> delegate;

    public ListWrapperListModel(List<E> delegateList) {
        this.delegate = delegateList;
    }

    public List<E> getDelegateList() {
        return this.delegate;
    }

    @Override
    public int getSize() {
        return this.delegate.size();
    }

    @Override
    public Object getElementAt(int i) {
        return this.delegate.get(i);
    }

    public E set(int i, E e) {
        E element = this.delegate.set(i, e);
        this.fireContentsChanged(this, i, i);
        return element;
    }

    public void clear() {
        int i = this.delegate.size() - 1;
        this.delegate.clear();
        if (i >= 0) {
            this.fireIntervalRemoved(this, 0, i);
        }
    }

    public int lastIndexOf(Object o) {
        return this.delegate.lastIndexOf(o);
    }

    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }

    public ListIterator<E> listIterator() {
        return this.delegate.listIterator();
    }

    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    public int indexOf(Object o) {
        return this.delegate.indexOf(o);
    }

    public void add(int i, E e) {
        int index = this.delegate.size();
        this.delegate.add(i, e);
        this.fireIntervalAdded(this, index, index);
    }

    public Iterator<E> iterator() {
        return this.delegate.iterator();
    }

    public boolean addAll(Collection<? extends E> es) {
        int i = this.delegate.size();
        boolean added = this.delegate.addAll(es);
        if (added) {
            this.fireIntervalAdded(this, i, i + es.size());
        }
        return added;
    }

    public E remove(int i) {
        E element = this.delegate.remove(i);
        this.fireIntervalRemoved(this, i, i);
        return element;
    }

    public boolean addAll(int i, Collection<? extends E> es) {
        boolean added = this.delegate.addAll(i, es);
        if (added) {
            this.fireIntervalAdded(this, i, i + es.size());
        }
        return added;
    }

    public ListIterator<E> listIterator(int i) {
        return this.delegate.listIterator(i);
    }

    public boolean containsAll(Collection<?> objects) {
        return this.delegate.containsAll(objects);
    }

    public boolean remove(Object o) {
        int i = this.indexOf(o);
        boolean rv = this.delegate.remove(o);
        if (i >= 0) {
            this.fireIntervalRemoved(this, i, i);
        }
        return rv;
    }

    public boolean add(E e) {
        int i = this.delegate.size();
        boolean added = this.delegate.add(e);
        if (added) {
            this.fireIntervalAdded(this, i, i);
        }
        return added;
    }

    public E get(int i) {
        return this.delegate.get(i);
    }

    public <T> T[] toArray(T[] ts) {
        return this.delegate.toArray(ts);
    }

    public Object[] toArray() {
        return this.delegate.toArray();
    }

    public void removeRange(int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex must be <= toIndex");
        }
        for (int i = toIndex; i >= fromIndex; --i) {
            this.delegate.remove(i);
        }
        this.fireIntervalRemoved(this, fromIndex, toIndex);
    }
}

