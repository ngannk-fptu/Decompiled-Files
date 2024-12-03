/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

class WeakFastHashMap<K, V>
extends HashMap<K, V> {
    private Map<K, V> map = null;
    private boolean fast = false;

    public WeakFastHashMap() {
        this.map = this.createMap();
    }

    public WeakFastHashMap(int capacity) {
        this.map = this.createMap(capacity);
    }

    public WeakFastHashMap(int capacity, float factor) {
        this.map = this.createMap(capacity, factor);
    }

    public WeakFastHashMap(Map<? extends K, ? extends V> map) {
        this.map = this.createMap(map);
    }

    public boolean getFast() {
        return this.fast;
    }

    public void setFast(boolean fast) {
        this.fast = fast;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V get(Object key) {
        if (this.fast) {
            return this.map.get(key);
        }
        Map<K, V> map = this.map;
        synchronized (map) {
            return this.map.get(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int size() {
        if (this.fast) {
            return this.map.size();
        }
        Map<K, V> map = this.map;
        synchronized (map) {
            return this.map.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isEmpty() {
        if (this.fast) {
            return this.map.isEmpty();
        }
        Map<K, V> map = this.map;
        synchronized (map) {
            return this.map.isEmpty();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsKey(Object key) {
        if (this.fast) {
            return this.map.containsKey(key);
        }
        Map<K, V> map = this.map;
        synchronized (map) {
            return this.map.containsKey(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsValue(Object value) {
        if (this.fast) {
            return this.map.containsValue(value);
        }
        Map<K, V> map = this.map;
        synchronized (map) {
            return this.map.containsValue(value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V put(K key, V value) {
        if (this.fast) {
            WeakFastHashMap weakFastHashMap = this;
            synchronized (weakFastHashMap) {
                Map<K, V> temp = this.cloneMap(this.map);
                V result = temp.put(key, value);
                this.map = temp;
                return result;
            }
        }
        Map<K, V> map = this.map;
        synchronized (map) {
            return this.map.put(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> in) {
        if (this.fast) {
            WeakFastHashMap weakFastHashMap = this;
            synchronized (weakFastHashMap) {
                Map<? extends K, ? extends V> temp = this.cloneMap(this.map);
                temp.putAll(in);
                this.map = temp;
            }
        }
        Map<K, V> map = this.map;
        synchronized (map) {
            this.map.putAll(in);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V remove(Object key) {
        if (this.fast) {
            WeakFastHashMap weakFastHashMap = this;
            synchronized (weakFastHashMap) {
                Map<K, V> temp = this.cloneMap(this.map);
                V result = temp.remove(key);
                this.map = temp;
                return result;
            }
        }
        Map<K, V> map = this.map;
        synchronized (map) {
            return this.map.remove(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        if (this.fast) {
            WeakFastHashMap weakFastHashMap = this;
            synchronized (weakFastHashMap) {
                this.map = this.createMap();
            }
        }
        Map<K, V> map = this.map;
        synchronized (map) {
            this.map.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Map)) {
            return false;
        }
        Map mo = (Map)o;
        if (this.fast) {
            if (mo.size() != this.map.size()) {
                return false;
            }
            for (Map.Entry<K, V> e : this.map.entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                if (!(value == null ? mo.get(key) != null || !mo.containsKey(key) : !value.equals(mo.get(key)))) continue;
                return false;
            }
            return true;
        }
        Map<K, V> map = this.map;
        synchronized (map) {
            if (mo.size() != this.map.size()) {
                return false;
            }
            for (Map.Entry<K, V> e : this.map.entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                if (value == null) {
                    if (mo.get(key) == null && mo.containsKey(key)) continue;
                    return false;
                }
                if (value.equals(mo.get(key))) continue;
                return false;
            }
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int hashCode() {
        if (this.fast) {
            int h = 0;
            for (Map.Entry<K, V> e : this.map.entrySet()) {
                h += e.hashCode();
            }
            return h;
        }
        Map<K, V> map = this.map;
        synchronized (map) {
            int h = 0;
            for (Map.Entry<K, V> e : this.map.entrySet()) {
                h += e.hashCode();
            }
            return h;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object clone() {
        WeakFastHashMap<K, V> results = null;
        if (this.fast) {
            results = new WeakFastHashMap<K, V>(this.map);
        } else {
            Map<K, V> map = this.map;
            synchronized (map) {
                results = new WeakFastHashMap<K, V>(this.map);
            }
        }
        results.setFast(this.getFast());
        return results;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    @Override
    public Set<K> keySet() {
        return new KeySet();
    }

    @Override
    public Collection<V> values() {
        return new Values();
    }

    protected Map<K, V> createMap() {
        return new WeakHashMap();
    }

    protected Map<K, V> createMap(int capacity) {
        return new WeakHashMap(capacity);
    }

    protected Map<K, V> createMap(int capacity, float factor) {
        return new WeakHashMap(capacity, factor);
    }

    protected Map<K, V> createMap(Map<? extends K, ? extends V> map) {
        return new WeakHashMap<K, V>(map);
    }

    protected Map<K, V> cloneMap(Map<? extends K, ? extends V> map) {
        return this.createMap(map);
    }

    private class EntrySet
    extends CollectionView<Map.Entry<K, V>>
    implements Set<Map.Entry<K, V>> {
        private EntrySet() {
        }

        @Override
        protected Collection<Map.Entry<K, V>> get(Map<K, V> map) {
            return map.entrySet();
        }

        @Override
        protected Map.Entry<K, V> iteratorNext(Map.Entry<K, V> entry) {
            return entry;
        }
    }

    private class Values
    extends CollectionView<V> {
        private Values() {
        }

        @Override
        protected Collection<V> get(Map<K, V> map) {
            return map.values();
        }

        @Override
        protected V iteratorNext(Map.Entry<K, V> entry) {
            return entry.getValue();
        }
    }

    private class KeySet
    extends CollectionView<K>
    implements Set<K> {
        private KeySet() {
        }

        @Override
        protected Collection<K> get(Map<K, V> map) {
            return map.keySet();
        }

        @Override
        protected K iteratorNext(Map.Entry<K, V> entry) {
            return entry.getKey();
        }
    }

    private abstract class CollectionView<E>
    implements Collection<E> {
        protected abstract Collection<E> get(Map<K, V> var1);

        protected abstract E iteratorNext(Map.Entry<K, V> var1);

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            if (WeakFastHashMap.this.fast) {
                WeakFastHashMap weakFastHashMap = WeakFastHashMap.this;
                synchronized (weakFastHashMap) {
                    WeakFastHashMap.this.map = WeakFastHashMap.this.createMap();
                }
            }
            Map map = WeakFastHashMap.this.map;
            synchronized (map) {
                this.get(WeakFastHashMap.this.map).clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(Object o) {
            if (WeakFastHashMap.this.fast) {
                WeakFastHashMap weakFastHashMap = WeakFastHashMap.this;
                synchronized (weakFastHashMap) {
                    Map temp = WeakFastHashMap.this.cloneMap(WeakFastHashMap.this.map);
                    boolean r = this.get(temp).remove(o);
                    WeakFastHashMap.this.map = temp;
                    return r;
                }
            }
            Map map = WeakFastHashMap.this.map;
            synchronized (map) {
                return this.get(WeakFastHashMap.this.map).remove(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(Collection<?> o) {
            if (WeakFastHashMap.this.fast) {
                WeakFastHashMap weakFastHashMap = WeakFastHashMap.this;
                synchronized (weakFastHashMap) {
                    Map temp = WeakFastHashMap.this.cloneMap(WeakFastHashMap.this.map);
                    boolean r = this.get(temp).removeAll(o);
                    WeakFastHashMap.this.map = temp;
                    return r;
                }
            }
            Map map = WeakFastHashMap.this.map;
            synchronized (map) {
                return this.get(WeakFastHashMap.this.map).removeAll(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(Collection<?> o) {
            if (WeakFastHashMap.this.fast) {
                WeakFastHashMap weakFastHashMap = WeakFastHashMap.this;
                synchronized (weakFastHashMap) {
                    Map temp = WeakFastHashMap.this.cloneMap(WeakFastHashMap.this.map);
                    boolean r = this.get(temp).retainAll(o);
                    WeakFastHashMap.this.map = temp;
                    return r;
                }
            }
            Map map = WeakFastHashMap.this.map;
            synchronized (map) {
                return this.get(WeakFastHashMap.this.map).retainAll(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).size();
            }
            Map map = WeakFastHashMap.this.map;
            synchronized (map) {
                return this.get(WeakFastHashMap.this.map).size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).isEmpty();
            }
            Map map = WeakFastHashMap.this.map;
            synchronized (map) {
                return this.get(WeakFastHashMap.this.map).isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(Object o) {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).contains(o);
            }
            Map map = WeakFastHashMap.this.map;
            synchronized (map) {
                return this.get(WeakFastHashMap.this.map).contains(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsAll(Collection<?> o) {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).containsAll(o);
            }
            Map map = WeakFastHashMap.this.map;
            synchronized (map) {
                return this.get(WeakFastHashMap.this.map).containsAll(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public <T> T[] toArray(T[] o) {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).toArray(o);
            }
            Map map = WeakFastHashMap.this.map;
            synchronized (map) {
                return this.get(WeakFastHashMap.this.map).toArray(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Object[] toArray() {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).toArray();
            }
            Map map = WeakFastHashMap.this.map;
            synchronized (map) {
                return this.get(WeakFastHashMap.this.map).toArray();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).equals(o);
            }
            Map map = WeakFastHashMap.this.map;
            synchronized (map) {
                return this.get(WeakFastHashMap.this.map).equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).hashCode();
            }
            Map map = WeakFastHashMap.this.map;
            synchronized (map) {
                return this.get(WeakFastHashMap.this.map).hashCode();
            }
        }

        @Override
        public boolean add(E o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<E> iterator() {
            return new CollectionViewIterator();
        }

        private class CollectionViewIterator
        implements Iterator<E> {
            private Map<K, V> expected;
            private Map.Entry<K, V> lastReturned = null;
            private final Iterator<Map.Entry<K, V>> iterator;

            public CollectionViewIterator() {
                this.expected = WeakFastHashMap.this.map;
                this.iterator = this.expected.entrySet().iterator();
            }

            @Override
            public boolean hasNext() {
                if (this.expected != WeakFastHashMap.this.map) {
                    throw new ConcurrentModificationException();
                }
                return this.iterator.hasNext();
            }

            @Override
            public E next() {
                if (this.expected != WeakFastHashMap.this.map) {
                    throw new ConcurrentModificationException();
                }
                this.lastReturned = this.iterator.next();
                return CollectionView.this.iteratorNext(this.lastReturned);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void remove() {
                if (this.lastReturned == null) {
                    throw new IllegalStateException();
                }
                if (WeakFastHashMap.this.fast) {
                    WeakFastHashMap weakFastHashMap = WeakFastHashMap.this;
                    synchronized (weakFastHashMap) {
                        if (this.expected != WeakFastHashMap.this.map) {
                            throw new ConcurrentModificationException();
                        }
                        WeakFastHashMap.this.remove(this.lastReturned.getKey());
                        this.lastReturned = null;
                        this.expected = WeakFastHashMap.this.map;
                    }
                } else {
                    this.iterator.remove();
                    this.lastReturned = null;
                }
            }
        }
    }
}

