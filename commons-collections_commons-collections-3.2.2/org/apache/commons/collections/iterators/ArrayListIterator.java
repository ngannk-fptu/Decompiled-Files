/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.lang.reflect.Array;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections.ResettableListIterator;
import org.apache.commons.collections.iterators.ArrayIterator;

public class ArrayListIterator
extends ArrayIterator
implements ListIterator,
ResettableListIterator {
    protected int lastItemIndex = -1;

    public ArrayListIterator() {
    }

    public ArrayListIterator(Object array) {
        super(array);
    }

    public ArrayListIterator(Object array, int startIndex) {
        super(array, startIndex);
        this.startIndex = startIndex;
    }

    public ArrayListIterator(Object array, int startIndex, int endIndex) {
        super(array, startIndex, endIndex);
        this.startIndex = startIndex;
    }

    public boolean hasPrevious() {
        return this.index > this.startIndex;
    }

    public Object previous() {
        if (!this.hasPrevious()) {
            throw new NoSuchElementException();
        }
        this.lastItemIndex = --this.index;
        return Array.get(this.array, this.index);
    }

    public Object next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.lastItemIndex = this.index;
        return Array.get(this.array, this.index++);
    }

    public int nextIndex() {
        return this.index - this.startIndex;
    }

    public int previousIndex() {
        return this.index - this.startIndex - 1;
    }

    public void add(Object o) {
        throw new UnsupportedOperationException("add() method is not supported");
    }

    public void set(Object o) {
        if (this.lastItemIndex == -1) {
            throw new IllegalStateException("must call next() or previous() before a call to set()");
        }
        Array.set(this.array, this.lastItemIndex, o);
    }

    public void reset() {
        super.reset();
        this.lastItemIndex = -1;
    }
}

