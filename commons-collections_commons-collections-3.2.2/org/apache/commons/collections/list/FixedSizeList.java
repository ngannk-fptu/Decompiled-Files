/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections.BoundedCollection;
import org.apache.commons.collections.iterators.AbstractListIteratorDecorator;
import org.apache.commons.collections.iterators.UnmodifiableIterator;
import org.apache.commons.collections.list.AbstractSerializableListDecorator;

public class FixedSizeList
extends AbstractSerializableListDecorator
implements BoundedCollection {
    private static final long serialVersionUID = -2218010673611160319L;

    public static List decorate(List list) {
        return new FixedSizeList(list);
    }

    protected FixedSizeList(List list) {
        super(list);
    }

    public boolean add(Object object) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public void add(int index, Object object) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public boolean addAll(Collection coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public boolean addAll(int index, Collection coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public void clear() {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public Object get(int index) {
        return this.getList().get(index);
    }

    public int indexOf(Object object) {
        return this.getList().indexOf(object);
    }

    public Iterator iterator() {
        return UnmodifiableIterator.decorate(this.getCollection().iterator());
    }

    public int lastIndexOf(Object object) {
        return this.getList().lastIndexOf(object);
    }

    public ListIterator listIterator() {
        return new FixedSizeListIterator(this.getList().listIterator(0));
    }

    public ListIterator listIterator(int index) {
        return new FixedSizeListIterator(this.getList().listIterator(index));
    }

    public Object remove(int index) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public boolean remove(Object object) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public boolean removeAll(Collection coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public boolean retainAll(Collection coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public Object set(int index, Object object) {
        return this.getList().set(index, object);
    }

    public List subList(int fromIndex, int toIndex) {
        List sub = this.getList().subList(fromIndex, toIndex);
        return new FixedSizeList(sub);
    }

    public boolean isFull() {
        return true;
    }

    public int maxSize() {
        return this.size();
    }

    static class FixedSizeListIterator
    extends AbstractListIteratorDecorator {
        protected FixedSizeListIterator(ListIterator iterator) {
            super(iterator);
        }

        public void remove() {
            throw new UnsupportedOperationException("List is fixed size");
        }

        public void add(Object object) {
            throw new UnsupportedOperationException("List is fixed size");
        }
    }
}

