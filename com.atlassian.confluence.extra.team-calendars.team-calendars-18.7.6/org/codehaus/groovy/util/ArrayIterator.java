/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import java.lang.reflect.Array;
import java.util.Iterator;

public class ArrayIterator<T>
implements Iterator<T> {
    private final T[] array;
    private int length;
    private int index;

    public ArrayIterator(T[] array) {
        this.array = array;
        this.length = Array.getLength(array);
    }

    @Override
    public boolean hasNext() {
        return this.index < this.length;
    }

    @Override
    public T next() {
        return (T)Array.get(this.array, this.index++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported for arrays");
    }
}

