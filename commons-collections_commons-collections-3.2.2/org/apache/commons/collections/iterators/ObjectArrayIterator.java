/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections.ResettableIterator;

public class ObjectArrayIterator
implements Iterator,
ResettableIterator {
    protected Object[] array = null;
    protected int startIndex = 0;
    protected int endIndex = 0;
    protected int index = 0;

    public ObjectArrayIterator() {
    }

    public ObjectArrayIterator(Object[] array) {
        this(array, 0, array.length);
    }

    public ObjectArrayIterator(Object[] array, int start) {
        this(array, start, array.length);
    }

    public ObjectArrayIterator(Object[] array, int start, int end) {
        if (start < 0) {
            throw new ArrayIndexOutOfBoundsException("Start index must not be less than zero");
        }
        if (end > array.length) {
            throw new ArrayIndexOutOfBoundsException("End index must not be greater than the array length");
        }
        if (start > array.length) {
            throw new ArrayIndexOutOfBoundsException("Start index must not be greater than the array length");
        }
        if (end < start) {
            throw new IllegalArgumentException("End index must not be less than start index");
        }
        this.array = array;
        this.startIndex = start;
        this.endIndex = end;
        this.index = start;
    }

    public boolean hasNext() {
        return this.index < this.endIndex;
    }

    public Object next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        return this.array[this.index++];
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() method is not supported for an ObjectArrayIterator");
    }

    public Object[] getArray() {
        return this.array;
    }

    public void setArray(Object[] array) {
        if (this.array != null) {
            throw new IllegalStateException("The array to iterate over has already been set");
        }
        this.array = array;
        this.startIndex = 0;
        this.endIndex = array.length;
        this.index = 0;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public int getEndIndex() {
        return this.endIndex;
    }

    public void reset() {
        this.index = this.startIndex;
    }
}

