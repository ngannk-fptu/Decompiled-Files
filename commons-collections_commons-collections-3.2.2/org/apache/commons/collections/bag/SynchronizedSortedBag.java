/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bag;

import java.util.Comparator;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.SortedBag;
import org.apache.commons.collections.bag.SynchronizedBag;

public class SynchronizedSortedBag
extends SynchronizedBag
implements SortedBag {
    private static final long serialVersionUID = 722374056718497858L;

    public static SortedBag decorate(SortedBag bag) {
        return new SynchronizedSortedBag(bag);
    }

    protected SynchronizedSortedBag(SortedBag bag) {
        super(bag);
    }

    protected SynchronizedSortedBag(Bag bag, Object lock) {
        super(bag, lock);
    }

    protected SortedBag getSortedBag() {
        return (SortedBag)this.collection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized Object first() {
        Object object = this.lock;
        synchronized (object) {
            return this.getSortedBag().first();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized Object last() {
        Object object = this.lock;
        synchronized (object) {
            return this.getSortedBag().last();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized Comparator comparator() {
        Object object = this.lock;
        synchronized (object) {
            return this.getSortedBag().comparator();
        }
    }
}

