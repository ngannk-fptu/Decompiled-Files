/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

public class MultiIterator<T>
implements Iterator<Object[]> {
    @Nullable
    private Boolean hasNext;
    private int index = 0;
    private final List<? extends Iterable<T>> iterables;
    private final List<Iterator<T>> iterators;
    private final boolean[] lastEntry;
    private final Object[] values;

    public MultiIterator(List<? extends Iterable<T>> iterables) {
        this.iterables = iterables;
        this.iterators = new ArrayList<Iterator<T>>(iterables.size());
        for (int i = 0; i < iterables.size(); ++i) {
            this.iterators.add(null);
        }
        this.lastEntry = new boolean[iterables.size()];
        this.values = new Object[iterables.size()];
    }

    @Override
    public boolean hasNext() {
        while (this.hasNext == null) {
            this.produceNext();
        }
        return this.hasNext;
    }

    @Override
    public T[] next() {
        while (this.hasNext == null) {
            this.produceNext();
        }
        if (this.hasNext.booleanValue()) {
            this.hasNext = null;
            return (Object[])this.values.clone();
        }
        throw new NoSuchElementException();
    }

    private void produceNext() {
        for (int i = this.index; i < this.iterables.size(); ++i) {
            if (this.iterators.get(i) == null || !this.iterators.get(i).hasNext() && i > 0) {
                this.iterators.set(i, this.iterables.get(i).iterator());
            }
            if (!this.iterators.get(i).hasNext()) {
                this.hasNext = i == 0 ? Boolean.FALSE : null;
                return;
            }
            this.values[i] = this.iterators.get(i).next();
            this.lastEntry[i] = !this.iterators.get(i).hasNext();
            this.hasNext = Boolean.TRUE;
        }
        this.index = this.iterables.size() - 1;
        while (this.lastEntry[this.index] && this.index > 0) {
            --this.index;
        }
    }

    @Override
    public void remove() {
    }
}

