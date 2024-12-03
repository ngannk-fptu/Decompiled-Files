/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections.Unmodifiable;
import org.apache.commons.collections.iterators.UnmodifiableIterator;
import org.apache.commons.collections.iterators.UnmodifiableListIterator;
import org.apache.commons.collections.list.AbstractSerializableListDecorator;

public final class UnmodifiableList
extends AbstractSerializableListDecorator
implements Unmodifiable {
    private static final long serialVersionUID = 6595182819922443652L;

    public static List decorate(List list) {
        if (list instanceof Unmodifiable) {
            return list;
        }
        return new UnmodifiableList(list);
    }

    private UnmodifiableList(List list) {
        super(list);
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

    public ListIterator listIterator() {
        return UnmodifiableListIterator.decorate(this.getList().listIterator());
    }

    public ListIterator listIterator(int index) {
        return UnmodifiableListIterator.decorate(this.getList().listIterator(index));
    }

    public void add(int index, Object object) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, Collection coll) {
        throw new UnsupportedOperationException();
    }

    public Object remove(int index) {
        throw new UnsupportedOperationException();
    }

    public Object set(int index, Object object) {
        throw new UnsupportedOperationException();
    }

    public List subList(int fromIndex, int toIndex) {
        List sub = this.getList().subList(fromIndex, toIndex);
        return new UnmodifiableList(sub);
    }
}

