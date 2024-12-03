/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Fold
 *  com.atlassian.streams.api.common.Function2
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.streams.spi;

import com.atlassian.streams.api.common.Fold;
import com.atlassian.streams.api.common.Function2;
import com.google.common.collect.ImmutableList;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class BoundedTreeSet<T>
extends AbstractSet<T> {
    private final SortedSet<T> backingSet;
    private final int maxSize;

    public BoundedTreeSet(int maxSize, Comparator<T> comparator) {
        this.maxSize = maxSize;
        this.backingSet = new TreeSet<T>(comparator);
    }

    @Override
    public boolean add(T o) {
        boolean added = this.backingSet.add(o);
        if (!added) {
            return false;
        }
        if (this.backingSet.size() > this.maxSize) {
            T removed = this.backingSet.last();
            this.remove(removed);
            if (removed == o) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> os) {
        return (Boolean)Fold.foldl((Iterable)ImmutableList.copyOf(os), (Object)true, (Function2)new Function2<T, Boolean, Boolean>(){

            public Boolean apply(T t, Boolean allSuccess) {
                return BoundedTreeSet.this.add(t) && allSuccess != false;
            }
        });
    }

    @Override
    public Iterator<T> iterator() {
        return this.backingSet.iterator();
    }

    @Override
    public int size() {
        return this.backingSet.size();
    }
}

