/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class _ArrayIterator
implements Iterator {
    private final Object[] array;
    private int nextIndex;

    public _ArrayIterator(Object[] array) {
        this.array = array;
        this.nextIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return this.nextIndex < this.array.length;
    }

    public Object next() {
        if (this.nextIndex >= this.array.length) {
            throw new NoSuchElementException();
        }
        return this.array[this.nextIndex++];
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

