/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.collections;

import java.util.Iterator;

public final class SingletonIterator<T>
implements Iterator<T> {
    private T value;
    private boolean hasNext = true;

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public T next() {
        if (this.hasNext) {
            this.hasNext = false;
            return this.value;
        }
        throw new IllegalStateException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public SingletonIterator(T value) {
        this.value = value;
    }
}

