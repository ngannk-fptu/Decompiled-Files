/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections.ResettableListIterator;

public class LoopingListIterator
implements ResettableListIterator {
    private List list;
    private ListIterator iterator;

    public LoopingListIterator(List list) {
        if (list == null) {
            throw new NullPointerException("The list must not be null");
        }
        this.list = list;
        this.reset();
    }

    public boolean hasNext() {
        return !this.list.isEmpty();
    }

    public Object next() {
        if (this.list.isEmpty()) {
            throw new NoSuchElementException("There are no elements for this iterator to loop on");
        }
        if (!this.iterator.hasNext()) {
            this.reset();
        }
        return this.iterator.next();
    }

    public int nextIndex() {
        if (this.list.isEmpty()) {
            throw new NoSuchElementException("There are no elements for this iterator to loop on");
        }
        if (!this.iterator.hasNext()) {
            return 0;
        }
        return this.iterator.nextIndex();
    }

    public boolean hasPrevious() {
        return !this.list.isEmpty();
    }

    public Object previous() {
        if (this.list.isEmpty()) {
            throw new NoSuchElementException("There are no elements for this iterator to loop on");
        }
        if (!this.iterator.hasPrevious()) {
            Object result = null;
            while (this.iterator.hasNext()) {
                result = this.iterator.next();
            }
            this.iterator.previous();
            return result;
        }
        return this.iterator.previous();
    }

    public int previousIndex() {
        if (this.list.isEmpty()) {
            throw new NoSuchElementException("There are no elements for this iterator to loop on");
        }
        if (!this.iterator.hasPrevious()) {
            return this.list.size() - 1;
        }
        return this.iterator.previousIndex();
    }

    public void remove() {
        this.iterator.remove();
    }

    public void add(Object obj) {
        this.iterator.add(obj);
    }

    public void set(Object obj) {
        this.iterator.set(obj);
    }

    public void reset() {
        this.iterator = this.list.listIterator();
    }

    public int size() {
        return this.list.size();
    }
}

