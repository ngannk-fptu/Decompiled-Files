/*
 * Decompiled with CFR 0.152.
 */
package com.mysema.commons.lang;

import com.mysema.commons.lang.CloseableIterator;
import java.util.NoSuchElementException;

public class EmptyCloseableIterator<T>
implements CloseableIterator<T> {
    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
    }
}

