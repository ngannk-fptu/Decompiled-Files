/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SingletonIterator<T>
implements Iterator<T> {
    private final T _value;
    private boolean _done = false;

    @Deprecated
    public SingletonIterator(T value) {
        this._value = value;
    }

    public static <T> SingletonIterator<T> create(T value) {
        return new SingletonIterator<T>(value);
    }

    @Override
    public boolean hasNext() {
        return !this._done;
    }

    @Override
    public T next() {
        if (this._done) {
            throw new NoSuchElementException();
        }
        this._done = true;
        return this._value;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Can not remove item from SingletonIterator.");
    }
}

