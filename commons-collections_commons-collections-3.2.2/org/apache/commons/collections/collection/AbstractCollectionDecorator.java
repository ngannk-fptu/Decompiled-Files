/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.collection;

import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractCollectionDecorator
implements Collection {
    protected Collection collection;

    protected AbstractCollectionDecorator() {
    }

    protected AbstractCollectionDecorator(Collection coll) {
        if (coll == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        this.collection = coll;
    }

    protected Collection getCollection() {
        return this.collection;
    }

    public boolean add(Object object) {
        return this.collection.add(object);
    }

    public boolean addAll(Collection coll) {
        return this.collection.addAll(coll);
    }

    public void clear() {
        this.collection.clear();
    }

    public boolean contains(Object object) {
        return this.collection.contains(object);
    }

    public boolean isEmpty() {
        return this.collection.isEmpty();
    }

    public Iterator iterator() {
        return this.collection.iterator();
    }

    public boolean remove(Object object) {
        return this.collection.remove(object);
    }

    public int size() {
        return this.collection.size();
    }

    public Object[] toArray() {
        return this.collection.toArray();
    }

    public Object[] toArray(Object[] object) {
        return this.collection.toArray(object);
    }

    public boolean containsAll(Collection coll) {
        return this.collection.containsAll(coll);
    }

    public boolean removeAll(Collection coll) {
        return this.collection.removeAll(coll);
    }

    public boolean retainAll(Collection coll) {
        return this.collection.retainAll(coll);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return this.collection.equals(object);
    }

    public int hashCode() {
        return this.collection.hashCode();
    }

    public String toString() {
        return this.collection.toString();
    }
}

