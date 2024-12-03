/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.collection;

import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.collections.BoundedCollection;
import org.apache.commons.collections.collection.AbstractCollectionDecorator;
import org.apache.commons.collections.collection.AbstractSerializableCollectionDecorator;
import org.apache.commons.collections.collection.SynchronizedCollection;
import org.apache.commons.collections.iterators.UnmodifiableIterator;

public final class UnmodifiableBoundedCollection
extends AbstractSerializableCollectionDecorator
implements BoundedCollection {
    private static final long serialVersionUID = -7112672385450340330L;

    public static BoundedCollection decorate(BoundedCollection coll) {
        return new UnmodifiableBoundedCollection(coll);
    }

    public static BoundedCollection decorateUsing(Collection coll) {
        if (coll == null) {
            throw new IllegalArgumentException("The collection must not be null");
        }
        for (int i = 0; i < 1000 && !(coll instanceof BoundedCollection); ++i) {
            if (coll instanceof AbstractCollectionDecorator) {
                coll = ((AbstractCollectionDecorator)coll).collection;
                continue;
            }
            if (!(coll instanceof SynchronizedCollection)) break;
            coll = ((SynchronizedCollection)coll).collection;
        }
        if (!(coll instanceof BoundedCollection)) {
            throw new IllegalArgumentException("The collection is not a bounded collection");
        }
        return new UnmodifiableBoundedCollection((BoundedCollection)coll);
    }

    private UnmodifiableBoundedCollection(BoundedCollection coll) {
        super(coll);
    }

    public Iterator iterator() {
        return UnmodifiableIterator.decorate(this.getCollection().iterator());
    }

    public boolean add(Object object) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection coll) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection coll) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection coll) {
        throw new UnsupportedOperationException();
    }

    public boolean isFull() {
        return ((BoundedCollection)this.collection).isFull();
    }

    public int maxSize() {
        return ((BoundedCollection)this.collection).maxSize();
    }
}

