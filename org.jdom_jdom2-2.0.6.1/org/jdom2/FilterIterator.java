/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jdom2.Content;
import org.jdom2.DescendantIterator;
import org.jdom2.filter.Filter;
import org.jdom2.util.IteratorIterable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class FilterIterator<T>
implements IteratorIterable<T> {
    private final DescendantIterator iterator;
    private final Filter<T> filter;
    private T nextObject;
    private boolean canremove = false;

    public FilterIterator(DescendantIterator iterator, Filter<T> filter) {
        if (filter == null) {
            throw new NullPointerException("Cannot specify a null Filter for a FilterIterator");
        }
        this.iterator = iterator;
        this.filter = filter;
    }

    @Override
    public Iterator<T> iterator() {
        return new FilterIterator<T>(this.iterator.iterator(), this.filter);
    }

    @Override
    public boolean hasNext() {
        this.canremove = false;
        if (this.nextObject != null) {
            return true;
        }
        while (this.iterator.hasNext()) {
            Content obj = this.iterator.next();
            T f = this.filter.filter(obj);
            if (f == null) continue;
            this.nextObject = f;
            return true;
        }
        return false;
    }

    @Override
    public T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        T obj = this.nextObject;
        this.nextObject = null;
        this.canremove = true;
        return obj;
    }

    @Override
    public void remove() {
        if (!this.canremove) {
            throw new IllegalStateException("remove() can only be called on the FilterIterator immediately after a successful call to next(). A call to remove() immediately after a call to hasNext() or remove() will also fail.");
        }
        this.canremove = false;
        this.iterator.remove();
    }
}

