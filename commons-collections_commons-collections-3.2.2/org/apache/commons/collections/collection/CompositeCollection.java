/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.collections.iterators.EmptyIterator;
import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.collections.list.UnmodifiableList;

public class CompositeCollection
implements Collection {
    protected CollectionMutator mutator;
    protected Collection[] all = new Collection[0];

    public CompositeCollection() {
    }

    public CompositeCollection(Collection coll) {
        this();
        this.addComposited(coll);
    }

    public CompositeCollection(Collection[] colls) {
        this();
        this.addComposited(colls);
    }

    public int size() {
        int size = 0;
        for (int i = this.all.length - 1; i >= 0; --i) {
            size += this.all[i].size();
        }
        return size;
    }

    public boolean isEmpty() {
        for (int i = this.all.length - 1; i >= 0; --i) {
            if (this.all[i].isEmpty()) continue;
            return false;
        }
        return true;
    }

    public boolean contains(Object obj) {
        for (int i = this.all.length - 1; i >= 0; --i) {
            if (!this.all[i].contains(obj)) continue;
            return true;
        }
        return false;
    }

    public Iterator iterator() {
        if (this.all.length == 0) {
            return EmptyIterator.INSTANCE;
        }
        IteratorChain chain = new IteratorChain();
        for (int i = 0; i < this.all.length; ++i) {
            chain.addIterator(this.all[i].iterator());
        }
        return chain;
    }

    public Object[] toArray() {
        Object[] result = new Object[this.size()];
        int i = 0;
        Iterator it = this.iterator();
        while (it.hasNext()) {
            result[i] = it.next();
            ++i;
        }
        return result;
    }

    public Object[] toArray(Object[] array) {
        int size = this.size();
        Object[] result = null;
        result = array.length >= size ? array : (Object[])Array.newInstance(array.getClass().getComponentType(), size);
        int offset = 0;
        for (int i = 0; i < this.all.length; ++i) {
            Iterator it = this.all[i].iterator();
            while (it.hasNext()) {
                result[offset++] = it.next();
            }
        }
        if (result.length > size) {
            result[size] = null;
        }
        return result;
    }

    public boolean add(Object obj) {
        if (this.mutator == null) {
            throw new UnsupportedOperationException("add() is not supported on CompositeCollection without a CollectionMutator strategy");
        }
        return this.mutator.add(this, this.all, obj);
    }

    public boolean remove(Object obj) {
        if (this.mutator == null) {
            throw new UnsupportedOperationException("remove() is not supported on CompositeCollection without a CollectionMutator strategy");
        }
        return this.mutator.remove(this, this.all, obj);
    }

    public boolean containsAll(Collection coll) {
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            if (this.contains(it.next())) continue;
            return false;
        }
        return true;
    }

    public boolean addAll(Collection coll) {
        if (this.mutator == null) {
            throw new UnsupportedOperationException("addAll() is not supported on CompositeCollection without a CollectionMutator strategy");
        }
        return this.mutator.addAll(this, this.all, coll);
    }

    public boolean removeAll(Collection coll) {
        if (coll.size() == 0) {
            return false;
        }
        boolean changed = false;
        for (int i = this.all.length - 1; i >= 0; --i) {
            changed = this.all[i].removeAll(coll) || changed;
        }
        return changed;
    }

    public boolean retainAll(Collection coll) {
        boolean changed = false;
        for (int i = this.all.length - 1; i >= 0; --i) {
            changed = this.all[i].retainAll(coll) || changed;
        }
        return changed;
    }

    public void clear() {
        for (int i = 0; i < this.all.length; ++i) {
            this.all[i].clear();
        }
    }

    public void setMutator(CollectionMutator mutator) {
        this.mutator = mutator;
    }

    public void addComposited(Collection[] comps) {
        ArrayList<Collection> list = new ArrayList<Collection>(Arrays.asList(this.all));
        list.addAll(Arrays.asList(comps));
        this.all = list.toArray(new Collection[list.size()]);
    }

    public void addComposited(Collection c) {
        this.addComposited(new Collection[]{c});
    }

    public void addComposited(Collection c, Collection d) {
        this.addComposited(new Collection[]{c, d});
    }

    public void removeComposited(Collection coll) {
        ArrayList<Collection> list = new ArrayList<Collection>(this.all.length);
        list.addAll(Arrays.asList(this.all));
        list.remove(coll);
        this.all = list.toArray(new Collection[list.size()]);
    }

    public Collection toCollection() {
        return new ArrayList(this);
    }

    public Collection getCollections() {
        return UnmodifiableList.decorate(Arrays.asList(this.all));
    }

    public static interface CollectionMutator {
        public boolean add(CompositeCollection var1, Collection[] var2, Object var3);

        public boolean addAll(CompositeCollection var1, Collection[] var2, Collection var3);

        public boolean remove(CompositeCollection var1, Collection[] var2, Object var3);
    }
}

