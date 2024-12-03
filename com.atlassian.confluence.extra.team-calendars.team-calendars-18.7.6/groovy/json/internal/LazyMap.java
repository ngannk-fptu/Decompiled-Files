/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.internal.Sys;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LazyMap
extends AbstractMap<String, Object> {
    static final String JDK_MAP_ALTHASHING_SYSPROP = System.getProperty("jdk.map.althashing.threshold");
    private Map<String, Object> map;
    private int size;
    private String[] keys;
    private Object[] values;

    public LazyMap() {
        this.keys = new String[5];
        this.values = new Object[5];
    }

    public LazyMap(int initialSize) {
        this.keys = new String[initialSize];
        this.values = new Object[initialSize];
    }

    @Override
    public Object put(String key, Object value) {
        if (this.map == null) {
            for (int i = 0; i < this.size; ++i) {
                String curKey = this.keys[i];
                if ((key != null || curKey != null) && (key == null || !key.equals(curKey))) continue;
                Object val = this.values[i];
                this.keys[i] = key;
                this.values[i] = value;
                return val;
            }
            this.keys[this.size] = key;
            this.values[this.size] = value;
            ++this.size;
            if (this.size == this.keys.length) {
                this.keys = LazyMap.grow(this.keys);
                this.values = LazyMap.grow(this.values);
            }
            return null;
        }
        return this.map.put(key, value);
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        this.buildIfNeeded();
        return this.map.entrySet();
    }

    @Override
    public int size() {
        if (this.map == null) {
            return this.size;
        }
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        if (this.map == null) {
            return this.size == 0;
        }
        return this.map.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        this.buildIfNeeded();
        return this.map.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        this.buildIfNeeded();
        return this.map.containsKey(key);
    }

    @Override
    public Object get(Object key) {
        this.buildIfNeeded();
        return this.map.get(key);
    }

    private void buildIfNeeded() {
        if (this.map == null) {
            this.map = Sys.is1_8OrLater() || Sys.is1_7() && JDK_MAP_ALTHASHING_SYSPROP != null ? new LinkedHashMap<String, Object>(this.size, 0.01f) : new TreeMap<String, Object>();
            for (int index = 0; index < this.size; ++index) {
                this.map.put(this.keys[index], this.values[index]);
            }
            this.keys = null;
            this.values = null;
        }
    }

    @Override
    public Object remove(Object key) {
        this.buildIfNeeded();
        return this.map.remove(key);
    }

    @Override
    public void putAll(Map m) {
        this.buildIfNeeded();
        this.map.putAll(m);
    }

    @Override
    public void clear() {
        if (this.map == null) {
            this.size = 0;
        } else {
            this.map.clear();
        }
    }

    @Override
    public Set<String> keySet() {
        this.buildIfNeeded();
        return this.map.keySet();
    }

    @Override
    public Collection<Object> values() {
        this.buildIfNeeded();
        return this.map.values();
    }

    @Override
    public boolean equals(Object o) {
        this.buildIfNeeded();
        return this.map.equals(o);
    }

    @Override
    public int hashCode() {
        this.buildIfNeeded();
        return this.map.hashCode();
    }

    @Override
    public String toString() {
        this.buildIfNeeded();
        return this.map.toString();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        if (this.map == null) {
            return null;
        }
        if (this.map instanceof LinkedHashMap) {
            return ((LinkedHashMap)this.map).clone();
        }
        return new LinkedHashMap<String, Object>(this);
    }

    public LazyMap clearAndCopy() {
        LazyMap map = new LazyMap();
        for (int index = 0; index < this.size; ++index) {
            map.put(this.keys[index], this.values[index]);
        }
        this.size = 0;
        return map;
    }

    public static <V> V[] grow(V[] array) {
        Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length * 2);
        System.arraycopy(array, 0, newArray, 0, array.length);
        return (Object[])newArray;
    }
}

