/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class JoinedIterator
implements Iterator {
    Iterator[] its;
    Iterator removeIterator = null;
    boolean permit_removes;
    int cur = 0;

    public JoinedIterator(Iterator[] iteratorArray, boolean bl) {
        this.its = iteratorArray;
        this.permit_removes = bl;
    }

    @Override
    public boolean hasNext() {
        if (this.cur == this.its.length) {
            return false;
        }
        if (this.its[this.cur].hasNext()) {
            return true;
        }
        ++this.cur;
        return this.hasNext();
    }

    public Object next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.removeIterator = this.its[this.cur];
        return this.removeIterator.next();
    }

    @Override
    public void remove() {
        if (this.permit_removes) {
            if (this.removeIterator == null) {
                throw new IllegalStateException("next() not called, or element already removed.");
            }
        } else {
            throw new UnsupportedOperationException();
        }
        this.removeIterator.remove();
        this.removeIterator = null;
    }
}

