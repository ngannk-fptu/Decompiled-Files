/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.collections;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

public class IdentitySet
implements Set {
    private static final Object DUMP_VALUE = new Object();
    private final IdentityHashMap map;

    public IdentitySet() {
        this.map = new IdentityHashMap();
    }

    public IdentitySet(int sizing) {
        this.map = new IdentityHashMap(sizing);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.map.get(o) == DUMP_VALUE;
    }

    @Override
    public Iterator iterator() {
        return this.map.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return this.map.keySet().toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        return this.map.keySet().toArray(a);
    }

    @Override
    public boolean add(Object o) {
        return this.map.put(o, DUMP_VALUE) == null;
    }

    @Override
    public boolean remove(Object o) {
        return this.map.remove(o) == DUMP_VALUE;
    }

    @Override
    public boolean containsAll(Collection c) {
        Iterator it = c.iterator();
        while (it.hasNext()) {
            if (this.map.containsKey(it.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection c) {
        Iterator it = c.iterator();
        boolean changed = false;
        while (it.hasNext()) {
            if (!this.add(it.next())) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection c) {
        Iterator it = c.iterator();
        boolean changed = false;
        while (it.hasNext()) {
            if (!this.remove(it.next())) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public void clear() {
        this.map.clear();
    }
}

