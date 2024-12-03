/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections.ResettableIterator;

public class SingletonIterator
implements Iterator,
ResettableIterator {
    private final boolean removeAllowed;
    private boolean beforeFirst = true;
    private boolean removed = false;
    private Object object;

    public SingletonIterator(Object object) {
        this(object, true);
    }

    public SingletonIterator(Object object, boolean removeAllowed) {
        this.object = object;
        this.removeAllowed = removeAllowed;
    }

    public boolean hasNext() {
        return this.beforeFirst && !this.removed;
    }

    public Object next() {
        if (!this.beforeFirst || this.removed) {
            throw new NoSuchElementException();
        }
        this.beforeFirst = false;
        return this.object;
    }

    public void remove() {
        if (this.removeAllowed) {
            if (this.removed || this.beforeFirst) {
                throw new IllegalStateException();
            }
        } else {
            throw new UnsupportedOperationException();
        }
        this.object = null;
        this.removed = true;
    }

    public void reset() {
        this.beforeFirst = true;
    }
}

