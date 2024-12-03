/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class WrapperIterator
implements Iterator {
    protected static final Object SKIP_TOKEN = new Object();
    static final boolean DEBUG = true;
    Iterator inner;
    boolean supports_remove;
    Object lastOut = null;
    Object nextOut = SKIP_TOKEN;

    public WrapperIterator(Iterator iterator, boolean bl) {
        this.inner = iterator;
        this.supports_remove = bl;
    }

    public WrapperIterator(Iterator iterator) {
        this(iterator, false);
    }

    @Override
    public boolean hasNext() {
        this.findNext();
        return this.nextOut != SKIP_TOKEN;
    }

    private void findNext() {
        if (this.nextOut == SKIP_TOKEN) {
            while (this.inner.hasNext() && this.nextOut == SKIP_TOKEN) {
                this.nextOut = this.transformObject(this.inner.next());
            }
        }
    }

    public Object next() {
        this.findNext();
        if (this.nextOut == SKIP_TOKEN) {
            throw new NoSuchElementException();
        }
        this.lastOut = this.nextOut;
        this.nextOut = SKIP_TOKEN;
        if (this.nextOut != SKIP_TOKEN || this.lastOut == SKIP_TOKEN) {
            throw new AssertionError((Object)"Better check out this weird WrapperIterator logic!");
        }
        return this.lastOut;
    }

    @Override
    public void remove() {
        if (this.supports_remove) {
            if (this.nextOut != SKIP_TOKEN) {
                throw new UnsupportedOperationException(this.getClass().getName() + " cannot support remove after hasNext() has been called!");
            }
            if (this.lastOut == SKIP_TOKEN) {
                throw new NoSuchElementException();
            }
        } else {
            throw new UnsupportedOperationException();
        }
        this.inner.remove();
    }

    protected abstract Object transformObject(Object var1);
}

