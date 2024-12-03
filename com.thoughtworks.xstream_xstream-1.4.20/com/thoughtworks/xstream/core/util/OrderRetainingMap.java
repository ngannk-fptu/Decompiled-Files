/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderRetainingMap
extends HashMap {
    private ArraySet keyOrder = new ArraySet();
    private List valueOrder = new ArrayList();

    public OrderRetainingMap() {
    }

    public OrderRetainingMap(Map m) {
        this.putAll(m);
    }

    public void putAll(Map m) {
        Iterator iter = m.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public Object put(Object key, Object value) {
        int idx = this.keyOrder.lastIndexOf(key);
        if (idx < 0) {
            this.keyOrder.add(key);
            this.valueOrder.add(value);
        } else {
            this.valueOrder.set(idx, value);
        }
        return super.put(key, value);
    }

    public Object remove(Object key) {
        int idx = this.keyOrder.lastIndexOf(key);
        if (idx != 0) {
            this.keyOrder.remove(idx);
            this.valueOrder.remove(idx);
        }
        return super.remove(key);
    }

    public void clear() {
        this.keyOrder.clear();
        this.valueOrder.clear();
        super.clear();
    }

    public Collection values() {
        return Collections.unmodifiableList(this.valueOrder);
    }

    public Set keySet() {
        return Collections.unmodifiableSet(this.keyOrder);
    }

    public Set entrySet() {
        Map.Entry[] entries = new Map.Entry[this.size()];
        Iterator iter = super.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry;
            entries[this.keyOrder.indexOf(entry.getKey())] = entry = iter.next();
        }
        ArraySet set = new ArraySet();
        set.addAll(Arrays.asList(entries));
        return Collections.unmodifiableSet(set);
    }

    private static class ArraySet
    extends ArrayList
    implements Set {
        private ArraySet() {
        }
    }
}

