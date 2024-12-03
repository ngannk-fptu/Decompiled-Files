/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.util;

import java.util.HashMap;
import java.util.Set;
import org.supercsv.util.TwoDHashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ThreeDHashMap<K1, K2, K3, V> {
    private final HashMap<K1, HashMap<K2, HashMap<K3, V>>> map = new HashMap();

    public boolean containsKey(K1 firstKey, K2 secondKey) {
        HashMap<K2, HashMap<K3, V>> innerMap1 = this.map.get(firstKey);
        if (innerMap1 == null) {
            return false;
        }
        return innerMap1.containsKey(secondKey);
    }

    public boolean containsKey(K1 firstKey, K2 secondKey, K3 thirdKey) {
        HashMap<K2, HashMap<K3, V>> innerMap1 = this.map.get(firstKey);
        if (innerMap1 == null) {
            return false;
        }
        HashMap<K3, V> innerMap2 = innerMap1.get(secondKey);
        if (innerMap2 == null) {
            return false;
        }
        return innerMap2.containsKey(thirdKey);
    }

    public HashMap<K2, HashMap<K3, V>> get(K1 firstKey) {
        return this.map.get(firstKey);
    }

    public TwoDHashMap<K2, K3, V> getAs2d(K1 firstKey) {
        HashMap<K2, HashMap<K3, V>> innerMap1 = this.map.get(firstKey);
        if (innerMap1 != null) {
            return new TwoDHashMap<K2, K3, V>(innerMap1);
        }
        return new TwoDHashMap();
    }

    public HashMap<K3, V> get(K1 firstKey, K2 secondKey) {
        HashMap<K2, HashMap<K3, V>> innerMap1 = this.map.get(firstKey);
        if (innerMap1 == null) {
            return null;
        }
        return innerMap1.get(secondKey);
    }

    public V get(K1 firstKey, K2 secondKey, K3 thirdKey) {
        HashMap<K2, HashMap<K3, V>> innerMap1 = this.map.get(firstKey);
        if (innerMap1 == null) {
            return null;
        }
        HashMap<K3, V> innerMap2 = innerMap1.get(secondKey);
        if (innerMap2 == null) {
            return null;
        }
        return innerMap2.get(thirdKey);
    }

    public Object set(K1 firstKey, K2 secondKey, K3 thirdKey, V value) {
        HashMap<Object, V> innerMap2;
        HashMap<Object, HashMap<Object, V>> innerMap1 = this.map.get(firstKey);
        if (innerMap1 == null) {
            innerMap1 = new HashMap();
            this.map.put(firstKey, innerMap1);
        }
        if ((innerMap2 = innerMap1.get(secondKey)) == null) {
            innerMap2 = new HashMap();
            innerMap1.put(secondKey, innerMap2);
        }
        return innerMap2.put(thirdKey, value);
    }

    public int size() {
        return this.map.size();
    }

    public int size(K1 firstKey) {
        HashMap<K2, HashMap<K3, V>> innerMap = this.map.get(firstKey);
        if (innerMap == null) {
            return 0;
        }
        return innerMap.size();
    }

    public int size(K1 firstKey, K2 secondKey) {
        HashMap<K2, HashMap<K3, V>> innerMap1 = this.map.get(firstKey);
        if (innerMap1 == null) {
            return 0;
        }
        HashMap<K3, V> innerMap2 = innerMap1.get(secondKey);
        if (innerMap2 == null) {
            return 0;
        }
        return innerMap2.size();
    }

    public Set<K1> keySet() {
        return this.map.keySet();
    }
}

