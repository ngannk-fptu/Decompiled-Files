/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

public class SynchronizedCollection
implements Collection,
Serializable {
    private static final long serialVersionUID = 2412805092710877986L;
    protected final Collection collection;
    protected final Object lock;

    public static Collection decorate(Collection coll) {
        return new SynchronizedCollection(coll);
    }

    protected SynchronizedCollection(Collection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        this.collection = collection;
        this.lock = this;
    }

    protected SynchronizedCollection(Collection collection, Object lock) {
        if (collection == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        this.collection = collection;
        this.lock = lock;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean add(Object object) {
        Object object2 = this.lock;
        synchronized (object2) {
            return this.collection.add(object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addAll(Collection coll) {
        Object object = this.lock;
        synchronized (object) {
            return this.collection.addAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear() {
        Object object = this.lock;
        synchronized (object) {
            this.collection.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean contains(Object object) {
        Object object2 = this.lock;
        synchronized (object2) {
            return this.collection.contains(object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsAll(Collection coll) {
        Object object = this.lock;
        synchronized (object) {
            return this.collection.containsAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isEmpty() {
        Object object = this.lock;
        synchronized (object) {
            return this.collection.isEmpty();
        }
    }

    public Iterator iterator() {
        return this.collection.iterator();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object[] toArray() {
        Object object = this.lock;
        synchronized (object) {
            return this.collection.toArray();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object[] toArray(Object[] object) {
        Object object2 = this.lock;
        synchronized (object2) {
            return this.collection.toArray(object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean remove(Object object) {
        Object object2 = this.lock;
        synchronized (object2) {
            return this.collection.remove(object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean removeAll(Collection coll) {
        Object object = this.lock;
        synchronized (object) {
            return this.collection.removeAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean retainAll(Collection coll) {
        Object object = this.lock;
        synchronized (object) {
            return this.collection.retainAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int size() {
        Object object = this.lock;
        synchronized (object) {
            return this.collection.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean equals(Object object) {
        Object object2 = this.lock;
        synchronized (object2) {
            if (object == this) {
                return true;
            }
            return this.collection.equals(object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int hashCode() {
        Object object = this.lock;
        synchronized (object) {
            return this.collection.hashCode();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        Object object = this.lock;
        synchronized (object) {
            return this.collection.toString();
        }
    }
}

