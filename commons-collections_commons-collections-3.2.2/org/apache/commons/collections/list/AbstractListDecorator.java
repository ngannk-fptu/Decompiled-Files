/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.list;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections.collection.AbstractCollectionDecorator;

public abstract class AbstractListDecorator
extends AbstractCollectionDecorator
implements List {
    protected AbstractListDecorator() {
    }

    protected AbstractListDecorator(List list) {
        super(list);
    }

    protected List getList() {
        return (List)this.getCollection();
    }

    public void add(int index, Object object) {
        this.getList().add(index, object);
    }

    public boolean addAll(int index, Collection coll) {
        return this.getList().addAll(index, coll);
    }

    public Object get(int index) {
        return this.getList().get(index);
    }

    public int indexOf(Object object) {
        return this.getList().indexOf(object);
    }

    public int lastIndexOf(Object object) {
        return this.getList().lastIndexOf(object);
    }

    public ListIterator listIterator() {
        return this.getList().listIterator();
    }

    public ListIterator listIterator(int index) {
        return this.getList().listIterator(index);
    }

    public Object remove(int index) {
        return this.getList().remove(index);
    }

    public Object set(int index, Object object) {
        return this.getList().set(index, object);
    }

    public List subList(int fromIndex, int toIndex) {
        return this.getList().subList(fromIndex, toIndex);
    }
}

