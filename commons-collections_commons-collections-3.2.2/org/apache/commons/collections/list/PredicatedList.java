/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.collection.PredicatedCollection;
import org.apache.commons.collections.iterators.AbstractListIteratorDecorator;

public class PredicatedList
extends PredicatedCollection
implements List {
    private static final long serialVersionUID = -5722039223898659102L;

    public static List decorate(List list, Predicate predicate) {
        return new PredicatedList(list, predicate);
    }

    protected PredicatedList(List list, Predicate predicate) {
        super(list, predicate);
    }

    protected List getList() {
        return (List)this.getCollection();
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
        this.validate(object);
        this.getList().add(index, object);
    }

    public boolean addAll(int index, Collection coll) {
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            this.validate(it.next());
        }
        return this.getList().addAll(index, coll);
    }

    public ListIterator listIterator() {
        return this.listIterator(0);
    }

    public ListIterator listIterator(int i) {
        return new PredicatedListIterator(this.getList().listIterator(i));
    }

    public Object set(int index, Object object) {
        this.validate(object);
        return this.getList().set(index, object);
    }

    public List subList(int fromIndex, int toIndex) {
        List sub = this.getList().subList(fromIndex, toIndex);
        return new PredicatedList(sub, this.predicate);
    }

    protected class PredicatedListIterator
    extends AbstractListIteratorDecorator {
        protected PredicatedListIterator(ListIterator iterator) {
            super(iterator);
        }

        public void add(Object object) {
            PredicatedList.this.validate(object);
            this.iterator.add(object);
        }

        public void set(Object object) {
            PredicatedList.this.validate(object);
            this.iterator.set(object);
        }
    }
}

