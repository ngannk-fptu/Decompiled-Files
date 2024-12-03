/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.DynamicSet;
import org.springframework.util.Assert;

public class DynamicSortedSet<E>
extends DynamicSet<E>
implements SortedSet<E> {
    private final Comparator<? super E> comparator;

    public DynamicSortedSet() {
        this((Comparator)null);
    }

    public DynamicSortedSet(Collection<? extends E> c) {
        this.comparator = null;
        this.addAll(c);
    }

    public DynamicSortedSet(int size) {
        super(size);
        this.comparator = null;
    }

    public DynamicSortedSet(SortedSet<E> ss) {
        this.comparator = ss.comparator();
        this.addAll(ss);
    }

    public DynamicSortedSet(Comparator<? super E> c) {
        this.comparator = c;
    }

    @Override
    public Comparator<? super E> comparator() {
        return this.comparator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean add(E o) {
        Assert.notNull(o);
        if (this.comparator == null && !(o instanceof Comparable)) {
            throw new ClassCastException("given object does not implement " + Comparable.class.getName() + " and no Comparator is set on the collection");
        }
        int index = 0;
        List list = this.storage;
        synchronized (list) {
            index = Collections.binarySearch(this.storage, o, this.comparator);
            if (index >= 0) {
                return false;
            }
            index = -index - 1;
            super.add(index, o);
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Assert.notNull((Object)o);
        return super.remove(o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public E first() {
        List list = this.storage;
        synchronized (list) {
            if (this.storage.isEmpty()) {
                throw new NoSuchElementException();
            }
            return this.storage.get(0);
        }
    }

    @Override
    public SortedSet<E> headSet(Object toElement) {
        throw new UnsupportedOperationException();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public E last() {
        List list = this.storage;
        synchronized (list) {
            if (this.storage.isEmpty()) {
                throw new NoSuchElementException();
            }
            return this.storage.get(this.storage.size() - 1);
        }
    }

    @Override
    public SortedSet<E> subSet(Object fromElement, Object toElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedSet<E> tailSet(Object fromElement) {
        throw new UnsupportedOperationException();
    }
}

