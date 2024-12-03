/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.jcr.RangeIterator;

public class RangeIteratorAdapter
implements RangeIterator {
    public static final RangeIterator EMPTY = new RangeIteratorAdapter(Collections.EMPTY_LIST);
    private final Iterator iterator;
    private long size;
    private long position;

    public RangeIteratorAdapter(Iterator iterator, long size) {
        this.iterator = iterator;
        this.size = size;
        this.position = 0L;
    }

    public RangeIteratorAdapter(Iterator iterator) {
        this(iterator, -1L);
    }

    public RangeIteratorAdapter(Collection collection) {
        this(collection.iterator(), collection.size());
    }

    @Override
    public long getPosition() {
        return this.position;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public void skip(long n) throws IllegalArgumentException, NoSuchElementException {
        if (n < 0L) {
            throw new IllegalArgumentException("skip(" + n + ")");
        }
        for (long i = 0L; i < n; ++i) {
            this.next();
        }
    }

    @Override
    public boolean hasNext() {
        if (this.iterator.hasNext()) {
            return true;
        }
        if (this.size == -1L) {
            this.size = this.position;
        }
        return false;
    }

    public Object next() throws NoSuchElementException {
        try {
            Object next = this.iterator.next();
            ++this.position;
            return next;
        }
        catch (NoSuchElementException e) {
            if (this.size == -1L) {
                this.size = this.position;
            }
            throw e;
        }
    }

    @Override
    public void remove() throws UnsupportedOperationException, IllegalStateException {
        this.iterator.remove();
        --this.position;
        if (this.size != -1L) {
            --this.size;
        }
    }
}

