/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections.list.UnmodifiableList;
import org.apache.commons.collections.set.AbstractSerializableSetDecorator;

public class ListOrderedSet
extends AbstractSerializableSetDecorator
implements Set {
    private static final long serialVersionUID = -228664372470420141L;
    protected final List setOrder;

    public static ListOrderedSet decorate(Set set, List list) {
        if (set == null) {
            throw new IllegalArgumentException("Set must not be null");
        }
        if (list == null) {
            throw new IllegalArgumentException("List must not be null");
        }
        if (set.size() > 0 || list.size() > 0) {
            throw new IllegalArgumentException("Set and List must be empty");
        }
        return new ListOrderedSet(set, list);
    }

    public static ListOrderedSet decorate(Set set) {
        return new ListOrderedSet(set);
    }

    public static ListOrderedSet decorate(List list) {
        if (list == null) {
            throw new IllegalArgumentException("List must not be null");
        }
        HashSet set = new HashSet(list);
        list.retainAll(set);
        return new ListOrderedSet(set, list);
    }

    public ListOrderedSet() {
        super(new HashSet());
        this.setOrder = new ArrayList();
    }

    protected ListOrderedSet(Set set) {
        super(set);
        this.setOrder = new ArrayList(set);
    }

    protected ListOrderedSet(Set set, List list) {
        super(set);
        if (list == null) {
            throw new IllegalArgumentException("List must not be null");
        }
        this.setOrder = list;
    }

    public List asList() {
        return UnmodifiableList.decorate(this.setOrder);
    }

    public void clear() {
        this.collection.clear();
        this.setOrder.clear();
    }

    public Iterator iterator() {
        return new OrderedSetIterator(this.setOrder.iterator(), this.collection);
    }

    public boolean add(Object object) {
        if (this.collection.contains(object)) {
            return this.collection.add(object);
        }
        boolean result = this.collection.add(object);
        this.setOrder.add(object);
        return result;
    }

    public boolean addAll(Collection coll) {
        boolean result = false;
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            Object object = it.next();
            result |= this.add(object);
        }
        return result;
    }

    public boolean remove(Object object) {
        boolean result = this.collection.remove(object);
        this.setOrder.remove(object);
        return result;
    }

    public boolean removeAll(Collection coll) {
        boolean result = false;
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            Object object = it.next();
            result |= this.remove(object);
        }
        return result;
    }

    public boolean retainAll(Collection coll) {
        boolean result = this.collection.retainAll(coll);
        if (!result) {
            return false;
        }
        if (this.collection.size() == 0) {
            this.setOrder.clear();
        } else {
            Iterator it = this.setOrder.iterator();
            while (it.hasNext()) {
                Object object = it.next();
                if (this.collection.contains(object)) continue;
                it.remove();
            }
        }
        return result;
    }

    public Object[] toArray() {
        return this.setOrder.toArray();
    }

    public Object[] toArray(Object[] a) {
        return this.setOrder.toArray(a);
    }

    public Object get(int index) {
        return this.setOrder.get(index);
    }

    public int indexOf(Object object) {
        return this.setOrder.indexOf(object);
    }

    public void add(int index, Object object) {
        if (!this.contains(object)) {
            this.collection.add(object);
            this.setOrder.add(index, object);
        }
    }

    public boolean addAll(int index, Collection coll) {
        boolean changed = false;
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            Object object = it.next();
            if (this.contains(object)) continue;
            this.collection.add(object);
            this.setOrder.add(index, object);
            ++index;
            changed = true;
        }
        return changed;
    }

    public Object remove(int index) {
        Object obj = this.setOrder.remove(index);
        this.remove(obj);
        return obj;
    }

    public String toString() {
        return this.setOrder.toString();
    }

    static class OrderedSetIterator
    extends AbstractIteratorDecorator {
        protected final Collection set;
        protected Object last;

        private OrderedSetIterator(Iterator iterator, Collection set) {
            super(iterator);
            this.set = set;
        }

        public Object next() {
            this.last = this.iterator.next();
            return this.last;
        }

        public void remove() {
            this.set.remove(this.last);
            this.iterator.remove();
            this.last = null;
        }
    }
}

