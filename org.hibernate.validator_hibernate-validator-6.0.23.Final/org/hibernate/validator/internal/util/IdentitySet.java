/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class IdentitySet
implements Set<Object> {
    private final Map<Object, Object> map;
    private final Object CONTAINS = new Object();

    public IdentitySet() {
        this(10);
    }

    public IdentitySet(int size) {
        this.map = new IdentityHashMap<Object, Object>(size);
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
        return this.map.containsKey(o);
    }

    @Override
    public Iterator<Object> iterator() {
        return this.map.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return this.map.keySet().toArray();
    }

    @Override
    public boolean add(Object o) {
        return this.map.put(o, this.CONTAINS) == null;
    }

    @Override
    public boolean remove(Object o) {
        return this.map.remove(o) == this.CONTAINS;
    }

    @Override
    public boolean addAll(Collection<? extends Object> c) {
        boolean doThing = false;
        for (Object object : c) {
            doThing = doThing || this.add(object);
        }
        return doThing;
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public boolean removeAll(Collection<? extends Object> c) {
        boolean remove = false;
        for (Object object : c) {
            remove = remove || this.remove(object);
        }
        return remove;
    }

    @Override
    public boolean retainAll(Collection<? extends Object> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<? extends Object> c) {
        for (Object object : c) {
            if (this.contains(object)) continue;
            return false;
        }
        return true;
    }

    @Override
    public Object[] toArray(Object[] a) {
        return this.map.keySet().toArray(a);
    }

    public String toString() {
        return "IdentitySet{map=" + this.map + '}';
    }
}

