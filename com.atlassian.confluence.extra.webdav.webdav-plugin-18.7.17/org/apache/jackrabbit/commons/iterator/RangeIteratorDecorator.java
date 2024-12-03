/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.NoSuchElementException;
import javax.jcr.RangeIterator;

public class RangeIteratorDecorator
implements RangeIterator {
    private final RangeIterator iterator;

    protected RangeIteratorDecorator(RangeIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public long getPosition() {
        return this.iterator.getPosition();
    }

    @Override
    public long getSize() {
        return this.iterator.getSize();
    }

    @Override
    public void skip(long n) throws NoSuchElementException {
        this.iterator.skip(n);
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public Object next() throws NoSuchElementException {
        return this.iterator.next();
    }

    @Override
    public void remove() throws UnsupportedOperationException, IllegalStateException {
        this.iterator.remove();
    }
}

