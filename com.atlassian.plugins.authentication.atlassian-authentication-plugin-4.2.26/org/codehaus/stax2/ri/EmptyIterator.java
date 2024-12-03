/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class EmptyIterator
implements Iterator {
    static final EmptyIterator sInstance = new EmptyIterator();

    private EmptyIterator() {
    }

    public static EmptyIterator getInstance() {
        return sInstance;
    }

    public boolean hasNext() {
        return false;
    }

    public Object next() {
        throw new NoSuchElementException();
    }

    public void remove() {
        throw new IllegalStateException();
    }
}

