/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.jcr.RangeIterator;
import org.apache.jackrabbit.commons.predicate.Predicate;

public class FilteredRangeIterator
implements RangeIterator {
    private final Iterator<?> iterator;
    private final Predicate predicate;
    private final Object[] buffer;
    private int bufferPosition = 0;
    private int bufferSize = 0;
    private long position = 0L;
    private long size = 0L;

    public FilteredRangeIterator(Iterator<?> iterator, Predicate predicate, int bufferSize) {
        this.iterator = iterator;
        this.predicate = predicate;
        this.buffer = new Object[bufferSize];
    }

    public FilteredRangeIterator(Iterator<?> iterator, Predicate predicate) {
        this(iterator, predicate, 1000);
    }

    public FilteredRangeIterator(Iterator<?> iterator) {
        this(iterator, Predicate.TRUE, 1000);
    }

    private void fetch() {
        if (this.bufferPosition == this.bufferSize) {
            this.position += (long)this.bufferSize;
            this.bufferPosition = 0;
            this.bufferSize = 0;
            while (this.bufferSize < this.buffer.length && this.iterator.hasNext()) {
                Object object = this.iterator.next();
                if (!this.predicate.evaluate(object)) continue;
                this.buffer[this.bufferSize++] = object;
            }
            this.size += (long)this.bufferSize;
        }
    }

    @Override
    public long getPosition() {
        return this.position + (long)this.bufferPosition;
    }

    @Override
    public long getSize() {
        this.fetch();
        if (this.iterator.hasNext()) {
            return -1L;
        }
        return this.size;
    }

    @Override
    public void skip(long n) throws IllegalArgumentException, NoSuchElementException {
        if (n < 0L) {
            throw new IllegalArgumentException();
        }
        while (n > 0L) {
            this.fetch();
            if (this.bufferPosition < this.bufferSize) {
                long m = Math.min(n, (long)(this.bufferSize - this.bufferPosition));
                this.bufferPosition = (int)((long)this.bufferPosition + m);
                n -= m;
                continue;
            }
            throw new NoSuchElementException();
        }
    }

    @Override
    public boolean hasNext() {
        this.fetch();
        return this.bufferPosition < this.bufferSize;
    }

    public Object next() throws NoSuchElementException {
        this.fetch();
        if (this.bufferPosition < this.bufferSize) {
            return this.buffer[this.bufferPosition++];
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}

