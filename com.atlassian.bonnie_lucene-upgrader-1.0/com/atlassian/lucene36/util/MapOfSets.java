/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MapOfSets<K, V> {
    private final Map<K, Set<V>> theMap;

    public MapOfSets(Map<K, Set<V>> m) {
        this.theMap = m;
    }

    public Map<K, Set<V>> getMap() {
        return this.theMap;
    }

    public int put(K key, V val) {
        Set<Object> theSet;
        if (this.theMap.containsKey(key)) {
            theSet = this.theMap.get(key);
        } else {
            theSet = new HashSet(23);
            this.theMap.put(key, theSet);
        }
        theSet.add(val);
        return theSet.size();
    }

    public int putAll(K key, Collection<? extends V> vals) {
        Set<Object> theSet;
        if (this.theMap.containsKey(key)) {
            theSet = this.theMap.get(key);
        } else {
            theSet = new HashSet(23);
            this.theMap.put(key, theSet);
        }
        theSet.addAll(vals);
        return theSet.size();
    }
}

