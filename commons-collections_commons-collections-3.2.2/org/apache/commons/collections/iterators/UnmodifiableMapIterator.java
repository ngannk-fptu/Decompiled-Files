/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.Unmodifiable;

public final class UnmodifiableMapIterator
implements MapIterator,
Unmodifiable {
    private MapIterator iterator;

    public static MapIterator decorate(MapIterator iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("MapIterator must not be null");
        }
        if (iterator instanceof Unmodifiable) {
            return iterator;
        }
        return new UnmodifiableMapIterator(iterator);
    }

    private UnmodifiableMapIterator(MapIterator iterator) {
        this.iterator = iterator;
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public Object next() {
        return this.iterator.next();
    }

    public Object getKey() {
        return this.iterator.getKey();
    }

    public Object getValue() {
        return this.iterator.getValue();
    }

    public Object setValue(Object value) {
        throw new UnsupportedOperationException("setValue() is not supported");
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }
}

