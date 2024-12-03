/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections.iterators.AbstractListIteratorDecorator;
import org.apache.commons.collections.list.AbstractSerializableListDecorator;
import org.apache.commons.collections.set.UnmodifiableSet;

public class SetUniqueList
extends AbstractSerializableListDecorator {
    private static final long serialVersionUID = 7196982186153478694L;
    protected final Set set;
    static /* synthetic */ Class class$java$util$HashSet;

    public static SetUniqueList decorate(List list) {
        if (list == null) {
            throw new IllegalArgumentException("List must not be null");
        }
        if (list.isEmpty()) {
            return new SetUniqueList(list, new HashSet());
        }
        ArrayList temp = new ArrayList(list);
        list.clear();
        SetUniqueList sl = new SetUniqueList(list, new HashSet());
        sl.addAll((Collection)temp);
        return sl;
    }

    protected SetUniqueList(List list, Set set) {
        super(list);
        if (set == null) {
            throw new IllegalArgumentException("Set must not be null");
        }
        this.set = set;
    }

    public Set asSet() {
        return UnmodifiableSet.decorate(this.set);
    }

    public boolean add(Object object) {
        int sizeBefore = this.size();
        this.add(this.size(), object);
        return sizeBefore != this.size();
    }

    public void add(int index, Object object) {
        if (!this.set.contains(object)) {
            super.add(index, object);
            this.set.add(object);
        }
    }

    public boolean addAll(Collection coll) {
        return this.addAll(this.size(), coll);
    }

    public boolean addAll(int index, Collection coll) {
        int sizeBefore = this.size();
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            int sizeBeforeAddNext = this.size();
            this.add(index, it.next());
            if (sizeBeforeAddNext == this.size()) continue;
            ++index;
        }
        return sizeBefore != this.size();
    }

    public Object set(int index, Object object) {
        int pos = this.indexOf(object);
        Object removed = super.set(index, object);
        if (pos != -1 && pos != index) {
            super.remove(pos);
        }
        this.set.remove(removed);
        this.set.add(object);
        return removed;
    }

    public boolean remove(Object object) {
        boolean result = super.remove(object);
        this.set.remove(object);
        return result;
    }

    public Object remove(int index) {
        Object result = super.remove(index);
        this.set.remove(result);
        return result;
    }

    public boolean removeAll(Collection coll) {
        boolean result = super.removeAll(coll);
        this.set.removeAll(coll);
        return result;
    }

    public boolean retainAll(Collection coll) {
        boolean result = super.retainAll(coll);
        this.set.retainAll(coll);
        return result;
    }

    public void clear() {
        super.clear();
        this.set.clear();
    }

    public boolean contains(Object object) {
        return this.set.contains(object);
    }

    public boolean containsAll(Collection coll) {
        return this.set.containsAll(coll);
    }

    public Iterator iterator() {
        return new SetListIterator(super.iterator(), this.set);
    }

    public ListIterator listIterator() {
        return new SetListListIterator(super.listIterator(), this.set);
    }

    public ListIterator listIterator(int index) {
        return new SetListListIterator(super.listIterator(index), this.set);
    }

    public List subList(int fromIndex, int toIndex) {
        List superSubList = super.subList(fromIndex, toIndex);
        Set subSet = this.createSetBasedOnList(this.set, superSubList);
        return new SetUniqueList(superSubList, subSet);
    }

    protected Set createSetBasedOnList(Set set, List list) {
        Set subSet = null;
        if (set.getClass().equals(class$java$util$HashSet == null ? (class$java$util$HashSet = SetUniqueList.class$("java.util.HashSet")) : class$java$util$HashSet)) {
            subSet = new HashSet();
        } else {
            try {
                subSet = (Set)set.getClass().newInstance();
            }
            catch (InstantiationException ie) {
                subSet = new HashSet();
            }
            catch (IllegalAccessException iae) {
                subSet = new HashSet();
            }
        }
        subSet.addAll(list);
        return subSet;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static class SetListListIterator
    extends AbstractListIteratorDecorator {
        protected final Set set;
        protected Object last = null;

        protected SetListListIterator(ListIterator it, Set set) {
            super(it);
            this.set = set;
        }

        public Object next() {
            this.last = super.next();
            return this.last;
        }

        public Object previous() {
            this.last = super.previous();
            return this.last;
        }

        public void remove() {
            super.remove();
            this.set.remove(this.last);
            this.last = null;
        }

        public void add(Object object) {
            if (!this.set.contains(object)) {
                super.add(object);
                this.set.add(object);
            }
        }

        public void set(Object object) {
            throw new UnsupportedOperationException("ListIterator does not support set");
        }
    }

    static class SetListIterator
    extends AbstractIteratorDecorator {
        protected final Set set;
        protected Object last = null;

        protected SetListIterator(Iterator it, Set set) {
            super(it);
            this.set = set;
        }

        public Object next() {
            this.last = super.next();
            return this.last;
        }

        public void remove() {
            super.remove();
            this.set.remove(this.last);
            this.last = null;
        }
    }
}

