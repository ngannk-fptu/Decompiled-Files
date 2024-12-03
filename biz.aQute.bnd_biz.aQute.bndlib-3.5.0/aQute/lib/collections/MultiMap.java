/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.collections;

import aQute.lib.collections.IteratorList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultiMap<K, V>
extends HashMap<K, List<V>>
implements Map<K, List<V>> {
    private static final long serialVersionUID = 1L;
    private final boolean noduplicates;
    private final Class<?> keyClass;
    private final Class<?> valueClass;

    public MultiMap() {
        this(false);
    }

    public MultiMap(boolean noduplicates) {
        this.noduplicates = noduplicates;
        this.keyClass = Object.class;
        this.valueClass = Object.class;
    }

    public MultiMap(Class<K> keyClass, Class<V> valueClass, boolean noduplicates) {
        this.noduplicates = noduplicates;
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    public <S extends K, T extends V> MultiMap(Map<S, ? extends List<T>> other) {
        this();
        for (Map.Entry<S, List<T>> e : other.entrySet()) {
            this.addAll(e.getKey(), (Collection)e.getValue());
        }
    }

    public <S extends K, T extends V> MultiMap(MultiMap<S, T> other) {
        this.keyClass = other.keyClass;
        this.valueClass = other.valueClass;
        this.noduplicates = other.noduplicates;
        for (Map.Entry e : other.entrySet()) {
            this.addAll(e.getKey(), (Collection)e.getValue());
        }
    }

    public boolean add(K key, V value) {
        assert (this.keyClass.isInstance(key));
        assert (this.valueClass.isInstance(value));
        List<Object> set = (ArrayList<V>)this.get(key);
        if (set == null) {
            set = new ArrayList<V>();
            if (this.valueClass != Object.class) {
                set = Collections.checkedList(set, this.valueClass);
            }
            this.put(key, set);
        } else if (this.noduplicates && set.contains(value)) {
            return false;
        }
        return set.add(value);
    }

    public boolean addAll(K key, Collection<? extends V> value) {
        if (value == null) {
            return false;
        }
        assert (this.keyClass.isInstance(key));
        List<V> set = (ArrayList<V>)this.get(key);
        if (set == null) {
            set = new ArrayList<V>();
            if (this.valueClass != Object.class) {
                set = Collections.checkedList(set, this.valueClass);
            }
            this.put(key, set);
        } else if (this.noduplicates) {
            boolean r = false;
            for (V v : value) {
                assert (this.valueClass.isInstance(v));
                if (set.contains(v)) continue;
                r |= set.add(v);
            }
            return r;
        }
        return set.addAll(value);
    }

    public boolean addAll(Map<K, ? extends Collection<? extends V>> map) {
        boolean added = false;
        for (Map.Entry<K, Collection<V>> e : map.entrySet()) {
            added |= this.addAll(e.getKey(), e.getValue());
        }
        return added;
    }

    public boolean removeValue(K key, V value) {
        assert (this.keyClass.isInstance(key));
        assert (this.valueClass.isInstance(value));
        List set = (List)this.get(key);
        if (set == null) {
            return false;
        }
        boolean result = set.remove(value);
        if (set.isEmpty()) {
            this.remove(key);
        }
        return result;
    }

    public boolean removeAll(K key, Collection<? extends V> value) {
        assert (this.keyClass.isInstance(key));
        List set = (List)this.get(key);
        if (set == null) {
            return false;
        }
        boolean result = set.removeAll(value);
        if (set.isEmpty()) {
            this.remove(key);
        }
        return result;
    }

    public Iterator<V> iterate(K key) {
        assert (this.keyClass.isInstance(key));
        List set = (List)this.get(key);
        if (set == null) {
            return Collections.emptyList().iterator();
        }
        return set.iterator();
    }

    public Iterator<V> all() {
        return new Iterator<V>(){
            Iterator<List<V>> master;
            Iterator<V> current;
            {
                this.master = MultiMap.this.values().iterator();
                this.current = null;
            }

            @Override
            public boolean hasNext() {
                if (this.current == null || !this.current.hasNext()) {
                    if (this.master.hasNext()) {
                        this.current = this.master.next().iterator();
                        return this.current.hasNext();
                    }
                    return false;
                }
                return true;
            }

            @Override
            public V next() {
                return this.current.next();
            }

            @Override
            public void remove() {
                this.current.remove();
            }
        };
    }

    public Map<K, V> flatten() {
        LinkedHashMap map = new LinkedHashMap();
        for (Map.Entry entry : this.entrySet()) {
            List v = (List)entry.getValue();
            if (v == null || v.isEmpty()) continue;
            map.put(entry.getKey(), v.get(0));
        }
        return map;
    }

    public MultiMap<V, K> transpose() {
        MultiMap inverted = new MultiMap();
        for (Map.Entry entry : this.entrySet()) {
            Object key = entry.getKey();
            List value = (List)entry.getValue();
            if (value == null) continue;
            for (Object v : value) {
                inverted.add(v, key);
            }
        }
        return inverted;
    }

    public List<V> allValues() {
        return new IteratorList<V>(this.all());
    }
}

