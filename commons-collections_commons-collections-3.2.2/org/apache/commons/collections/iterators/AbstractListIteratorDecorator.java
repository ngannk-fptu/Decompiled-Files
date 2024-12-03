/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.util.ListIterator;

public class AbstractListIteratorDecorator
implements ListIterator {
    protected final ListIterator iterator;

    public AbstractListIteratorDecorator(ListIterator iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("ListIterator must not be null");
        }
        this.iterator = iterator;
    }

    protected ListIterator getListIterator() {
        return this.iterator;
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public Object next() {
        return this.iterator.next();
    }

    public int nextIndex() {
        return this.iterator.nextIndex();
    }

    public boolean hasPrevious() {
        return this.iterator.hasPrevious();
    }

    public Object previous() {
        return this.iterator.previous();
    }

    public int previousIndex() {
        return this.iterator.previousIndex();
    }

    public void remove() {
        this.iterator.remove();
    }

    public void set(Object obj) {
        this.iterator.set(obj);
    }

    public void add(Object obj) {
        this.iterator.add(obj);
    }
}

