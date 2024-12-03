/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ListHashMap<K, V>
implements Map<K, V> {
    private final Object[] listKeys;
    private final Object[] listValues;
    private int size = 0;
    private Map<K, V> innerMap;
    private final int maxListFill;

    public ListHashMap() {
        this(3);
    }

    public ListHashMap(int listSize) {
        this.listKeys = new Object[listSize];
        this.listValues = new Object[listSize];
        this.maxListFill = listSize;
    }

    @Override
    public void clear() {
        this.innerMap = null;
        this.clearArrays();
        this.size = 0;
    }

    private void clearArrays() {
        for (int i = 0; i < this.maxListFill; ++i) {
            this.listValues[i] = null;
            this.listKeys[i] = null;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        if (this.size == 0) {
            return false;
        }
        if (this.innerMap == null) {
            for (int i = 0; i < this.size; ++i) {
                if (!this.listKeys[i].equals(key)) continue;
                return true;
            }
            return false;
        }
        return this.innerMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (this.size == 0) {
            return false;
        }
        if (this.innerMap == null) {
            for (int i = 0; i < this.size; ++i) {
                if (!this.listValues[i].equals(value)) continue;
                return true;
            }
            return false;
        }
        return this.innerMap.containsValue(value);
    }

    private Map<K, V> makeMap() {
        HashMap<Object, Object> m = new HashMap<Object, Object>();
        for (int i = 0; i < this.size; ++i) {
            m.put(this.listKeys[i], this.listValues[i]);
        }
        return m;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Map<K, V> m = this.innerMap != null ? this.innerMap : this.makeMap();
        return m.entrySet();
    }

    @Override
    public V get(Object key) {
        if (this.size == 0) {
            return null;
        }
        if (this.innerMap == null) {
            for (int i = 0; i < this.size; ++i) {
                if (!this.listKeys[i].equals(key)) continue;
                return (V)this.listValues[i];
            }
            return null;
        }
        return this.innerMap.get(key);
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public Set<K> keySet() {
        Map<K, V> m = this.innerMap != null ? this.innerMap : this.makeMap();
        return m.keySet();
    }

    @Override
    public V put(K key, V value) {
        if (this.innerMap == null) {
            for (int i = 0; i < this.size; ++i) {
                if (!this.listKeys[i].equals(key)) continue;
                Object old = this.listValues[i];
                this.listValues[i] = value;
                return (V)old;
            }
            if (this.size < this.maxListFill) {
                this.listKeys[this.size] = key;
                this.listValues[this.size] = value;
                ++this.size;
                return null;
            }
            this.innerMap = this.makeMap();
            this.clearArrays();
        }
        V val = this.innerMap.put(key, value);
        this.size = this.innerMap.size();
        return val;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<K, V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        if (this.innerMap == null) {
            for (int i = 0; i < this.size; ++i) {
                if (!this.listKeys[i].equals(key)) continue;
                Object old = this.listValues[i];
                --this.size;
                if (i < this.size) {
                    this.listValues[i] = this.listValues[this.size];
                    this.listKeys[i] = this.listKeys[this.size];
                }
                this.listValues[this.size] = null;
                this.listKeys[this.size] = null;
                return (V)old;
            }
            return null;
        }
        V old = this.innerMap.remove(key);
        this.size = this.innerMap.size();
        if (this.size <= this.maxListFill) {
            this.mapToList();
        }
        return old;
    }

    private void mapToList() {
        int i = 0;
        for (Map.Entry<K, V> entry : this.innerMap.entrySet()) {
            this.listKeys[i] = entry.getKey();
            this.listValues[i] = entry.getValue();
            ++i;
        }
        this.size = this.innerMap.size();
        this.innerMap = null;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Collection<V> values() {
        if (this.innerMap == null) {
            ArrayList<Object> list = new ArrayList<Object>(this.size);
            for (int i = 0; i < this.size; ++i) {
                list.add(this.listValues[i]);
            }
            return list;
        }
        return this.innerMap.values();
    }
}

