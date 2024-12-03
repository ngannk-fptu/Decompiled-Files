/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator
implements Iterator {
    private Object array;
    private int pos;
    private int size;

    public ArrayIterator(Object array) {
        if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("Programmer error : internal ArrayIterator invoked w/o array");
        }
        this.array = array;
        this.pos = 0;
        this.size = Array.getLength(this.array);
    }

    public Object next() {
        if (this.pos < this.size) {
            return Array.get(this.array, this.pos++);
        }
        throw new NoSuchElementException("No more elements: " + this.pos + " / " + this.size);
    }

    @Override
    public boolean hasNext() {
        return this.pos < this.size;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

