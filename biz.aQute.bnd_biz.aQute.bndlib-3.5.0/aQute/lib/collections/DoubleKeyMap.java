/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DoubleKeyMap<K1, K2, V>
extends HashMap<K1, Map<K2, V>>
implements Map<K1, Map<K2, V>> {
    private static final long serialVersionUID = 1L;
    private final Class<?> k1Class;
    private final Class<?> k2Class;
    private final Class<?> valueClass;

    public DoubleKeyMap() {
        this.k1Class = Object.class;
        this.k2Class = Object.class;
        this.valueClass = Object.class;
    }

    public DoubleKeyMap(Class<K1> k1Class, Class<K2> k2Class, Class<V> valueClass) {
        this.k1Class = k1Class;
        this.k2Class = k2Class;
        this.valueClass = valueClass;
    }

    public DoubleKeyMap(Map<K1, Map<K2, V>> other) {
        this();
        for (Map.Entry<K1, Map<K2, V>> e : other.entrySet()) {
            this.putAll(e.getKey(), e.getValue());
        }
    }

    public DoubleKeyMap(DoubleKeyMap<K1, K2, V> other) {
        this.k1Class = other.k1Class;
        this.k2Class = other.k2Class;
        this.valueClass = other.valueClass;
        for (Map.Entry e : other.entrySet()) {
            this.putAll(e.getKey(), (Map)e.getValue());
        }
    }

    public V put(K1 key1, K2 key2, V value) {
        assert (this.k1Class.isInstance(key1));
        assert (this.k2Class.isInstance(key2));
        assert (this.valueClass.isInstance(value));
        Map<Object, V> map = (HashMap<K2, V>)this.get(key1);
        if (map == null) {
            map = new HashMap<K2, V>();
            if (this.valueClass != Object.class) {
                map = Collections.checkedMap(map, this.k2Class, this.valueClass);
            }
            this.put(key1, map);
        }
        return map.put(key2, value);
    }

    public V get(K1 key1, K2 key2) {
        Map map = (Map)this.get(key1);
        if (map == null) {
            return null;
        }
        return map.get(key2);
    }

    public boolean containsKeys(K1 key1, K2 key2) {
        Map map = (Map)this.get(key1);
        if (map == null) {
            return false;
        }
        return map.containsKey(key2);
    }

    public void putAll(K1 key1, Map<K2, V> map) {
        assert (this.k1Class.isInstance(key1));
        for (Map.Entry<K2, V> e : map.entrySet()) {
            this.put(key1, e.getKey(), e.getValue());
        }
    }

    public boolean removeValue(K1 key1, K2 key2, V value) {
        assert (this.k1Class.isInstance(key1));
        assert (this.k2Class.isInstance(key2));
        assert (this.valueClass.isInstance(value));
        Map set = (Map)this.get(key1);
        if (set == null) {
            return false;
        }
        boolean result = set.values().remove(value);
        if (set.isEmpty()) {
            this.remove(key1);
        }
        return result;
    }

    public V removeKey(K1 key1, K2 key2) {
        assert (this.k1Class.isInstance(key1));
        assert (this.k2Class.isInstance(key2));
        Map set = (Map)this.get(key1);
        if (set == null) {
            return null;
        }
        Object result = set.remove(key2);
        if (set.isEmpty()) {
            this.remove(key1);
        }
        return result;
    }

    public Iterator<Map.Entry<K2, V>> iterate(K1 key) {
        assert (this.k1Class.isInstance(key));
        Map set = (Map)this.get(key);
        if (set == null) {
            return new Iterator<Map.Entry<K2, V>>(){

                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Map.Entry<K2, V> next() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return set.entrySet().iterator();
    }

    public Iterator<V> all() {
        return new Iterator<V>(){
            Iterator<Map.Entry<K1, Map<K2, V>>> master;
            Iterator<Map.Entry<K2, V>> current;
            {
                this.master = DoubleKeyMap.this.entrySet().iterator();
                this.current = null;
            }

            @Override
            public boolean hasNext() {
                if (this.current == null || !this.current.hasNext()) {
                    if (this.master.hasNext()) {
                        this.current = this.master.next().getValue().entrySet().iterator();
                        return this.current.hasNext();
                    }
                    return false;
                }
                return true;
            }

            @Override
            public V next() {
                return this.current.next().getValue();
            }

            @Override
            public void remove() {
                this.current.remove();
            }
        };
    }
}

