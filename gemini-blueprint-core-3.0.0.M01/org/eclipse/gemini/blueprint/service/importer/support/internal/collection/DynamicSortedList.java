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
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.DynamicList;
import org.springframework.util.Assert;

public class DynamicSortedList<E>
extends DynamicList<E> {
    private final Comparator<? super E> comparator;

    public DynamicSortedList() {
        this((Comparator)null);
    }

    public DynamicSortedList(Comparator<? super E> c) {
        this.comparator = c;
    }

    public DynamicSortedList(Collection<? extends E> c) {
        this.comparator = null;
        this.addAll(c);
    }

    public DynamicSortedList(int size) {
        super(size);
        this.comparator = null;
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
            boolean duplicate;
            index = Collections.binarySearch(this.storage, o, this.comparator);
            boolean bl = duplicate = index >= 0;
            if (duplicate) {
                boolean stillEqual = true;
                while (index + 1 < this.storage.size() && stillEqual) {
                    stillEqual = false;
                    Object next = this.storage.get(index + 1);
                    if (!(this.comparator != null ? this.comparator.compare(o, next) == 0 : ((Comparable)o).compareTo(next) == 0)) continue;
                    stillEqual = true;
                    ++index;
                }
            } else {
                index = -index - 1;
            }
            if (duplicate) {
                super.add(index + 1, o);
            } else {
                super.add(index, o);
            }
        }
        return true;
    }

    @Override
    public void add(int index, E o) {
        throw new UnsupportedOperationException("This is a sorted list; it is illegal to specify the element position");
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("This is a sorted list; it is illegal to specify the element position");
    }

    @Override
    public E set(int index, E o) {
        throw new UnsupportedOperationException("This is a sorted list; it is illegal to specify the element position");
    }
}

