/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.coalesce;

import java.util.Iterator;

class CoalescerIterator
implements Iterator {
    Iterator inner;

    CoalescerIterator(Iterator iterator) {
        this.inner = iterator;
    }

    @Override
    public boolean hasNext() {
        return this.inner.hasNext();
    }

    public Object next() {
        return this.inner.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Objects cannot be removed from a coalescer!");
    }
}

