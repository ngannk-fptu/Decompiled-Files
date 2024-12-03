/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SingletonIterator
implements Iterator {
    private final Object mValue;
    private boolean mDone = false;

    public SingletonIterator(Object object) {
        this.mValue = object;
    }

    public boolean hasNext() {
        return !this.mDone;
    }

    public Object next() {
        if (this.mDone) {
            throw new NoSuchElementException();
        }
        this.mDone = true;
        return this.mValue;
    }

    public void remove() {
        throw new UnsupportedOperationException("Can not remove item from SingletonIterator.");
    }
}

