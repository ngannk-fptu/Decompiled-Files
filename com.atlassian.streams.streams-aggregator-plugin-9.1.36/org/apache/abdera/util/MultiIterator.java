/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import java.util.Arrays;
import java.util.Iterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class MultiIterator<T>
implements Iterator<T> {
    private Iterator<Iterator<T>> iterators;
    private Iterator<T> current;

    public MultiIterator(Iterable<Iterator<T>> i) {
        this(i.iterator());
    }

    public MultiIterator(Iterator<T> ... iterators) {
        this(Arrays.asList(iterators).iterator());
    }

    public MultiIterator(Iterator<Iterator<T>> iterators) {
        this.iterators = iterators;
        this.current = this.selectCurrent();
    }

    private Iterator<T> selectCurrent() {
        if (this.current == null) {
            if (this.iterators.hasNext()) {
                this.current = this.iterators.next();
            }
        } else if (!this.current.hasNext() && this.iterators.hasNext()) {
            this.current = this.iterators.next();
        }
        return this.current;
    }

    @Override
    public boolean hasNext() {
        Iterator<T> c = this.selectCurrent();
        return c != null ? c.hasNext() : false;
    }

    @Override
    public T next() {
        if (this.hasNext()) {
            return this.selectCurrent().next();
        }
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

