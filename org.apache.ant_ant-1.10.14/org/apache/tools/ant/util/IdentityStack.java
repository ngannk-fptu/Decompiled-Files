/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.Stack;

public class IdentityStack<E>
extends Stack<E> {
    private static final long serialVersionUID = -5555522620060077046L;

    public static <E> IdentityStack<E> getInstance(Stack<E> s) {
        if (s instanceof IdentityStack) {
            return (IdentityStack)s;
        }
        IdentityStack<E> result = new IdentityStack<E>();
        if (s != null) {
            result.addAll(s);
        }
        return result;
    }

    public IdentityStack() {
    }

    public IdentityStack(E o) {
        this.push(o);
    }

    @Override
    public synchronized boolean contains(Object o) {
        return this.indexOf(o) >= 0;
    }

    @Override
    public synchronized int indexOf(Object o, int pos) {
        int size = this.size();
        for (int i = pos; i < size; ++i) {
            if (this.get(i) != o) continue;
            return i;
        }
        return -1;
    }

    @Override
    public synchronized int lastIndexOf(Object o, int pos) {
        for (int i = pos; i >= 0; --i) {
            if (this.get(i) != o) continue;
            return i;
        }
        return -1;
    }

    @Override
    public synchronized boolean removeAll(Collection<?> c) {
        if (!(c instanceof Set)) {
            c = new HashSet(c);
        }
        return super.removeAll(c);
    }

    @Override
    public synchronized boolean retainAll(Collection<?> c) {
        if (!(c instanceof Set)) {
            c = new HashSet(c);
        }
        return super.retainAll(c);
    }

    @Override
    public synchronized boolean containsAll(Collection<?> c) {
        IdentityHashMap map = new IdentityHashMap();
        for (Object e : this) {
            map.put(e, Boolean.TRUE);
        }
        return map.keySet().containsAll(c);
    }
}

