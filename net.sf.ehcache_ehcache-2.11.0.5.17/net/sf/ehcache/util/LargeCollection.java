/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import net.sf.ehcache.util.AggregateIterator;

public abstract class LargeCollection<E>
extends AbstractCollection<E> {
    private final Collection<E> addSet = new HashSet();
    private final Collection<Object> removeSet = new HashSet<Object>();

    @Override
    public final boolean add(E obj) {
        return this.addSet.add(obj);
    }

    @Override
    public final boolean contains(Object obj) {
        return !this.removeSet.contains(obj) ? this.addSet.contains(obj) || super.contains(obj) : false;
    }

    @Override
    public final boolean remove(Object obj) {
        return this.removeSet.add(obj);
    }

    @Override
    public final boolean removeAll(Collection<?> removeCandidates) {
        boolean remove = true;
        Iterator<?> iter = removeCandidates.iterator();
        while (iter.hasNext()) {
            remove = this.remove(iter.next()) & remove;
        }
        return remove;
    }

    private Iterator<E> additionalIterator() {
        return this.addSet.iterator();
    }

    @Override
    public final Iterator<E> iterator() {
        ArrayList iterators = new ArrayList();
        iterators.add(this.sourceIterator());
        iterators.add(this.additionalIterator());
        return new AggregateIterator(this.removeSet, iterators);
    }

    @Override
    public final int size() {
        return Math.max(0, this.sourceSize() + this.addSet.size() - this.removeSet.size());
    }

    public abstract Iterator<E> sourceIterator();

    public abstract int sourceSize();
}

