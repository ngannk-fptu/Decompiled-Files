/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.util.HashMap;
import java.util.Map;
import org.apache.axis.utils.IDKey;

public class IdentityHashMap
extends HashMap {
    public IdentityHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public IdentityHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public IdentityHashMap() {
    }

    public IdentityHashMap(Map t) {
        super(t);
    }

    public Object get(Object key) {
        return super.get(new IDKey(key));
    }

    public Object put(Object key, Object value) {
        return super.put(new IDKey(key), value);
    }

    public Object add(Object value) {
        IDKey key = new IDKey(value);
        if (!super.containsKey(key)) {
            return super.put(key, value);
        }
        return null;
    }

    public Object remove(Object key) {
        return super.remove(new IDKey(key));
    }

    public boolean containsKey(Object key) {
        return super.containsKey(new IDKey(key));
    }
}

