/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class ProxyMap
implements Map {
    protected Map map;

    public ProxyMap(Map map) {
        this.map = map;
    }

    public void clear() {
        this.map.clear();
    }

    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    public Set entrySet() {
        return this.map.entrySet();
    }

    public boolean equals(Object m) {
        return this.map.equals(m);
    }

    public Object get(Object key) {
        return this.map.get(key);
    }

    public int hashCode() {
        return this.map.hashCode();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public Set keySet() {
        return this.map.keySet();
    }

    public Object put(Object key, Object value) {
        return this.map.put(key, value);
    }

    public void putAll(Map t) {
        this.map.putAll(t);
    }

    public Object remove(Object key) {
        return this.map.remove(key);
    }

    public int size() {
        return this.map.size();
    }

    public Collection values() {
        return this.map.values();
    }
}

