/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections.ResettableListIterator;
import org.apache.commons.collections.iterators.ObjectArrayIterator;

public class ObjectArrayListIterator
extends ObjectArrayIterator
implements ListIterator,
ResettableListIterator {
    protected int lastItemIndex = -1;

    public ObjectArrayListIterator() {
    }

    public ObjectArrayListIterator(Object[] array) {
        super(array);
    }

    public ObjectArrayListIterator(Object[] array, int start) {
        super(array, start);
    }

    public ObjectArrayListIterator(Object[] array, int start, int end) {
        super(array, start, end);
    }

    public boolean hasPrevious() {
        return this.index > this.startIndex;
    }

    public Object previous() {
        if (!this.hasPrevious()) {
            throw new NoSuchElementException();
        }
        this.lastItemIndex = --this.index;
        return this.array[this.index];
    }

    public Object next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.lastItemIndex = this.index;
        return this.array[this.index++];
    }

    public int nextIndex() {
        return this.index - this.startIndex;
    }

    public int previousIndex() {
        return this.index - this.startIndex - 1;
    }

    public void add(Object obj) {
        throw new UnsupportedOperationException("add() method is not supported");
    }

    public void set(Object obj) {
        if (this.lastItemIndex == -1) {
            throw new IllegalStateException("must call next() or previous() before a call to set()");
        }
        this.array[this.lastItemIndex] = obj;
    }

    public void reset() {
        super.reset();
        this.lastItemIndex = -1;
    }
}

