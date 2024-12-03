/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.WeakHashMap;
import org.apache.tools.ant.types.Resource;

class FailFast
implements Iterator<Resource> {
    private static final WeakHashMap<Object, Set<FailFast>> MAP = new WeakHashMap();
    private final Object parent;
    private Iterator<Resource> wrapped;

    static synchronized void invalidate(Object o) {
        Set<FailFast> s = MAP.get(o);
        if (s != null) {
            s.clear();
        }
    }

    private static synchronized void add(FailFast f) {
        MAP.computeIfAbsent(f.parent, k -> new HashSet()).add(f);
    }

    private static synchronized void remove(FailFast f) {
        Set<FailFast> s = MAP.get(f.parent);
        if (s != null) {
            s.remove(f);
        }
    }

    private static synchronized void failFast(FailFast f) {
        Set<FailFast> s = MAP.get(f.parent);
        if (!s.contains(f)) {
            throw new ConcurrentModificationException();
        }
    }

    FailFast(Object o, Iterator<Resource> i) {
        if (o == null) {
            throw new IllegalArgumentException("parent object is null");
        }
        if (i == null) {
            throw new IllegalArgumentException("cannot wrap null iterator");
        }
        this.parent = o;
        if (i.hasNext()) {
            this.wrapped = i;
            FailFast.add(this);
        }
    }

    @Override
    public boolean hasNext() {
        if (this.wrapped == null) {
            return false;
        }
        FailFast.failFast(this);
        return this.wrapped.hasNext();
    }

    @Override
    public Resource next() {
        if (this.wrapped == null || !this.wrapped.hasNext()) {
            throw new NoSuchElementException();
        }
        FailFast.failFast(this);
        try {
            Resource resource = this.wrapped.next();
            return resource;
        }
        finally {
            if (!this.wrapped.hasNext()) {
                this.wrapped = null;
                FailFast.remove(this);
            }
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

