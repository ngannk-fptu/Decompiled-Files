/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.Unmodifiable;
import org.apache.commons.collections.bag.AbstractBagDecorator;
import org.apache.commons.collections.iterators.UnmodifiableIterator;
import org.apache.commons.collections.set.UnmodifiableSet;

public final class UnmodifiableBag
extends AbstractBagDecorator
implements Unmodifiable,
Serializable {
    private static final long serialVersionUID = -1873799975157099624L;

    public static Bag decorate(Bag bag) {
        if (bag instanceof Unmodifiable) {
            return bag;
        }
        return new UnmodifiableBag(bag);
    }

    private UnmodifiableBag(Bag bag) {
        super(bag);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.collection);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.collection = (Collection)in.readObject();
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

    public boolean add(Object object, int count) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object object, int count) {
        throw new UnsupportedOperationException();
    }

    public Set uniqueSet() {
        Set set = this.getBag().uniqueSet();
        return UnmodifiableSet.decorate(set);
    }
}

