/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class EmptyIterator
implements Iterator<Object> {
    static final Iterator<?> sInstance = new EmptyIterator();

    private EmptyIterator() {
    }

    public static <T> Iterator<T> getInstance() {
        return sInstance;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Object next() {
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new IllegalStateException();
    }
}

