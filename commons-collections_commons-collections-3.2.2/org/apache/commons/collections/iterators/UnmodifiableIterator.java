/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.util.Iterator;
import org.apache.commons.collections.Unmodifiable;

public final class UnmodifiableIterator
implements Iterator,
Unmodifiable {
    private Iterator iterator;

    public static Iterator decorate(Iterator iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("Iterator must not be null");
        }
        if (iterator instanceof Unmodifiable) {
            return iterator;
        }
        return new UnmodifiableIterator(iterator);
    }

    private UnmodifiableIterator(Iterator iterator) {
        this.iterator = iterator;
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public Object next() {
        return this.iterator.next();
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }
}

