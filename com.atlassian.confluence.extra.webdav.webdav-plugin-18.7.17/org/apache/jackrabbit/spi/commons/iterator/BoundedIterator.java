/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class BoundedIterator<T>
implements Iterator<T> {
    private final Iterator<T> iterator;
    private final long offset;
    private final long max;
    private int pos;
    private T next;

    public BoundedIterator(long offset, long max, Iterator<T> iterator) {
        if (offset < 0L) {
            throw new IllegalArgumentException("Offset must not be negative");
        }
        this.iterator = iterator;
        this.offset = offset;
        this.max = max;
    }

    public static <T> Iterator<T> create(long offset, long max, Iterator<T> iterator) {
        if (offset == 0L && max == -1L) {
            return iterator;
        }
        return new BoundedIterator<T>(offset, max, iterator);
    }

    @Override
    public boolean hasNext() {
        if (this.next == null) {
            this.fetchNext();
        }
        return this.next != null;
    }

    @Override
    public T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        return this.consumeNext();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void fetchNext() {
        while ((long)this.pos < this.offset && this.iterator.hasNext()) {
            this.next = this.iterator.next();
            ++this.pos;
        }
        if ((long)this.pos < this.offset || !this.iterator.hasNext() || this.max >= 0L && (long)this.pos - this.offset + 1L > this.max) {
            this.next = null;
        } else {
            this.next = this.iterator.next();
            ++this.pos;
        }
    }

    private T consumeNext() {
        T element = this.next;
        this.next = null;
        return element;
    }
}

