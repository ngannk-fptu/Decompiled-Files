/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bag;

import java.util.Set;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.collection.SynchronizedCollection;
import org.apache.commons.collections.set.SynchronizedSet;

public class SynchronizedBag
extends SynchronizedCollection
implements Bag {
    private static final long serialVersionUID = 8084674570753837109L;

    public static Bag decorate(Bag bag) {
        return new SynchronizedBag(bag);
    }

    protected SynchronizedBag(Bag bag) {
        super(bag);
    }

    protected SynchronizedBag(Bag bag, Object lock) {
        super(bag, lock);
    }

    protected Bag getBag() {
        return (Bag)this.collection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean add(Object object, int count) {
        Object object2 = this.lock;
        synchronized (object2) {
            return this.getBag().add(object, count);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean remove(Object object, int count) {
        Object object2 = this.lock;
        synchronized (object2) {
            return this.getBag().remove(object, count);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set uniqueSet() {
        Object object = this.lock;
        synchronized (object) {
            Set set = this.getBag().uniqueSet();
            return new SynchronizedBagSet(set, this.lock);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getCount(Object object) {
        Object object2 = this.lock;
        synchronized (object2) {
            return this.getBag().getCount(object);
        }
    }

    class SynchronizedBagSet
    extends SynchronizedSet {
        SynchronizedBagSet(Set set, Object lock) {
            super(set, lock);
        }
    }
}

