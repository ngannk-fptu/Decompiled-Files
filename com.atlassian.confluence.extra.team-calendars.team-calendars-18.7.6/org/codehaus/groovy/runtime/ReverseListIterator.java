/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ReverseListIterator<T>
implements Iterator<T> {
    private ListIterator<T> delegate;

    public ReverseListIterator(List<T> list) {
        this.delegate = list.listIterator(list.size());
    }

    @Override
    public boolean hasNext() {
        return this.delegate.hasPrevious();
    }

    @Override
    public T next() {
        return this.delegate.previous();
    }

    @Override
    public void remove() {
        this.delegate.remove();
    }
}

