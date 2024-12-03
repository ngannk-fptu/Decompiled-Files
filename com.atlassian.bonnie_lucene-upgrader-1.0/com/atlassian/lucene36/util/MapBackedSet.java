/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class MapBackedSet<E>
extends AbstractSet<E>
implements Serializable {
    private static final long serialVersionUID = -6761513279741915432L;
    private final Map<E, Boolean> map;

    public MapBackedSet(Map<E, Boolean> map) {
        this.map = map;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean contains(Object o) {
        return this.map.containsKey(o);
    }

    @Override
    public boolean add(E o) {
        return this.map.put(o, Boolean.TRUE) == null;
    }

    @Override
    public boolean remove(Object o) {
        return this.map.remove(o) != null;
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }
}

