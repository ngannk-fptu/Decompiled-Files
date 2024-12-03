/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.util;

import java.util.HashMap;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TwoDHashMap<K1, K2, V> {
    private final HashMap<K1, HashMap<K2, V>> map;

    public TwoDHashMap() {
        this.map = new HashMap();
    }

    public TwoDHashMap(HashMap<K1, HashMap<K2, V>> map) {
        if (map == null) {
            throw new NullPointerException("map should not be null");
        }
        this.map = map;
    }

    public boolean containsKey(K1 firstKey, K2 secondKey) {
        HashMap<K2, V> innerMap = this.map.get(firstKey);
        if (innerMap == null) {
            return false;
        }
        return innerMap.containsKey(secondKey);
    }

    public V get(K1 firstKey, K2 secondKey) {
        HashMap<K2, V> innerMap = this.map.get(firstKey);
        if (innerMap == null) {
            return null;
        }
        return innerMap.get(secondKey);
    }

    public Object set(K1 firstKey, K2 secondKey, V value) {
        HashMap<Object, V> innerMap = this.map.get(firstKey);
        if (innerMap == null) {
            innerMap = new HashMap();
            this.map.put(firstKey, innerMap);
        }
        return innerMap.put(secondKey, value);
    }

    public int size() {
        return this.map.size();
    }

    public int size(K1 firstKey) {
        HashMap<K2, V> innerMap = this.map.get(firstKey);
        if (innerMap == null) {
            return 0;
        }
        return innerMap.size();
    }

    public Set<K1> keySet() {
        return this.map.keySet();
    }
}

