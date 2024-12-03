/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections4.multiset;

import java.util.Set;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.collection.AbstractCollectionDecorator;

public abstract class AbstractMultiSetDecorator<E>
extends AbstractCollectionDecorator<E>
implements MultiSet<E> {
    private static final long serialVersionUID = 20150610L;

    protected AbstractMultiSetDecorator() {
    }

    protected AbstractMultiSetDecorator(MultiSet<E> multiset) {
        super(multiset);
    }

    @Override
    protected MultiSet<E> decorated() {
        return (MultiSet)super.decorated();
    }

    @Override
    public boolean equals(Object object) {
        return object == this || this.decorated().equals(object);
    }

    @Override
    public int hashCode() {
        return this.decorated().hashCode();
    }

    @Override
    public int getCount(Object object) {
        return this.decorated().getCount(object);
    }

    @Override
    public int setCount(E object, int count) {
        return this.decorated().setCount(object, count);
    }

    @Override
    public int add(E object, int count) {
        return this.decorated().add(object, count);
    }

    @Override
    public int remove(Object object, int count) {
        return this.decorated().remove(object, count);
    }

    @Override
    public Set<E> uniqueSet() {
        return this.decorated().uniqueSet();
    }

    @Override
    public Set<MultiSet.Entry<E>> entrySet() {
        return this.decorated().entrySet();
    }
}

