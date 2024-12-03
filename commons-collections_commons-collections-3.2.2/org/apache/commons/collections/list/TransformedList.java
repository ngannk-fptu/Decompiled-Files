/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.list;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.collection.TransformedCollection;
import org.apache.commons.collections.iterators.AbstractListIteratorDecorator;

public class TransformedList
extends TransformedCollection
implements List {
    private static final long serialVersionUID = 1077193035000013141L;

    public static List decorate(List list, Transformer transformer) {
        return new TransformedList(list, transformer);
    }

    protected TransformedList(List list, Transformer transformer) {
        super(list, transformer);
    }

    protected List getList() {
        return (List)this.collection;
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

    public Object remove(int index) {
        return this.getList().remove(index);
    }

    public void add(int index, Object object) {
        object = this.transform(object);
        this.getList().add(index, object);
    }

    public boolean addAll(int index, Collection coll) {
        coll = this.transform(coll);
        return this.getList().addAll(index, coll);
    }

    public ListIterator listIterator() {
        return this.listIterator(0);
    }

    public ListIterator listIterator(int i) {
        return new TransformedListIterator(this.getList().listIterator(i));
    }

    public Object set(int index, Object object) {
        object = this.transform(object);
        return this.getList().set(index, object);
    }

    public List subList(int fromIndex, int toIndex) {
        List sub = this.getList().subList(fromIndex, toIndex);
        return new TransformedList(sub, this.transformer);
    }

    protected class TransformedListIterator
    extends AbstractListIteratorDecorator {
        protected TransformedListIterator(ListIterator iterator) {
            super(iterator);
        }

        public void add(Object object) {
            object = TransformedList.this.transform(object);
            this.iterator.add(object);
        }

        public void set(Object object) {
            object = TransformedList.this.transform(object);
            this.iterator.set(object);
        }
    }
}

