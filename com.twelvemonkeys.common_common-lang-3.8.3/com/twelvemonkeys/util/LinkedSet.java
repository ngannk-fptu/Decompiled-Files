/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import com.twelvemonkeys.util.LinkedMap;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class LinkedSet<E>
extends AbstractSet<E>
implements Set<E>,
Cloneable,
Serializable {
    private static final Object DUMMY = new Object();
    private final Map<E, Object> map = new LinkedMap<E, Object>();

    public LinkedSet() {
    }

    public LinkedSet(Collection<E> collection) {
        this();
        this.addAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean bl = false;
        for (E e : collection) {
            if (!this.add(e) || bl) continue;
            bl = true;
        }
        return bl;
    }

    @Override
    public boolean add(E e) {
        return this.map.put(e, DUMMY) == null;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }
}

