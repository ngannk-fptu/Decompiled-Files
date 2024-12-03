/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

