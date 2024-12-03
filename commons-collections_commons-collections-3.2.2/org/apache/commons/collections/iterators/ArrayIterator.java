/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;
import org.apache.commons.collections.ResettableIterator;

public class ArrayIterator
implements ResettableIterator {
    protected Object array;
    protected int startIndex = 0;
    protected int endIndex = 0;
    protected int index = 0;

    public ArrayIterator() {
    }

    public ArrayIterator(Object array) {
        this.setArray(array);
    }

    public ArrayIterator(Object array, int startIndex) {
        this.setArray(array);
        this.checkBound(startIndex, "start");
        this.startIndex = startIndex;
        this.index = startIndex;
    }

    public ArrayIterator(Object array, int startIndex, int endIndex) {
        this.setArray(array);
        this.checkBound(startIndex, "start");
        this.checkBound(endIndex, "end");
        if (endIndex < startIndex) {
            throw new IllegalArgumentException("End index must not be less than start index.");
        }
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.index = startIndex;
    }

    protected void checkBound(int bound, String type) {
        if (bound > this.endIndex) {
            throw new ArrayIndexOutOfBoundsException("Attempt to make an ArrayIterator that " + type + "s beyond the end of the array. ");
        }
        if (bound < 0) {
            throw new ArrayIndexOutOfBoundsException("Attempt to make an ArrayIterator that " + type + "s before the start of the array. ");
        }
    }

    public boolean hasNext() {
        return this.index < this.endIndex;
    }

    public Object next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        return Array.get(this.array, this.index++);
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() method is not supported");
    }

    public Object getArray() {
        return this.array;
    }

    public void setArray(Object array) {
        this.endIndex = Array.getLength(array);
        this.startIndex = 0;
        this.array = array;
        this.index = 0;
    }

    public void reset() {
        this.index = this.startIndex;
    }
}

