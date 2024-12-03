/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.set;

import java.util.Comparator;
import java.util.SortedSet;
import org.apache.commons.collections.collection.SynchronizedCollection;

public class SynchronizedSortedSet
extends SynchronizedCollection
implements SortedSet {
    private static final long serialVersionUID = 2775582861954500111L;

    public static SortedSet decorate(SortedSet set) {
        return new SynchronizedSortedSet(set);
    }

    protected SynchronizedSortedSet(SortedSet set) {
        super(set);
    }

    protected SynchronizedSortedSet(SortedSet set, Object lock) {
        super(set, lock);
    }

    protected SortedSet getSortedSet() {
        return (SortedSet)this.collection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SortedSet subSet(Object fromElement, Object toElement) {
        Object object = this.lock;
        synchronized (object) {
            SortedSet<Object> set = this.getSortedSet().subSet(fromElement, toElement);
            return new SynchronizedSortedSet(set, this.lock);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SortedSet headSet(Object toElement) {
        Object object = this.lock;
        synchronized (object) {
            SortedSet<Object> set = this.getSortedSet().headSet(toElement);
            return new SynchronizedSortedSet(set, this.lock);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SortedSet tailSet(Object fromElement) {
        Object object = this.lock;
        synchronized (object) {
            SortedSet<Object> set = this.getSortedSet().tailSet(fromElement);
            return new SynchronizedSortedSet(set, this.lock);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object first() {
        Object object = this.lock;
        synchronized (object) {
            return this.getSortedSet().first();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object last() {
        Object object = this.lock;
        synchronized (object) {
            return this.getSortedSet().last();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Comparator comparator() {
        Object object = this.lock;
        synchronized (object) {
            return this.getSortedSet().comparator();
        }
    }
}

