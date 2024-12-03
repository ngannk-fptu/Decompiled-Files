/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class OrderedMap
extends HashMap {
    private List keyOrder = new LinkedList();

    @Override
    public void clear() {
        this.keyOrder.clear();
    }

    public Iterator iterator() {
        return this.keyOrder.iterator();
    }

    @Override
    public Object put(Object key, Object value) {
        this.keyOrder.add(key);
        return super.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        this.keyOrder.remove(key);
        return super.remove(key);
    }
}

