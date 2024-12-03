/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver.util;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class ArrayMap<K, V>
extends AbstractMap<K, V> {
    private Object[] table;
    private int size;
    protected transient Collection<V> values;

    public ArrayMap() {
        this(32);
    }

    public ArrayMap(int capacity) {
        this.table = new Object[capacity * 2];
        this.size = 0;
    }

    @Override
    public V get(Object key) {
        int l = this.size << 1;
        for (int i = 0; i < l; i += 2) {
            if (!key.equals(this.table[i])) continue;
            return (V)this.table[i + 1];
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        int i;
        int l = this.size << 1;
        for (i = 0; i < l; i += 2) {
            if (!key.equals(this.table[i])) continue;
            Object old = this.table[i + 1];
            this.table[i + 1] = value;
            return (V)old;
        }
        if (this.size * 2 == this.table.length) {
            Object[] n = new Object[this.table.length * 2];
            System.arraycopy(this.table, 0, n, 0, this.table.length);
            this.table = n;
        }
        i = this.size++ << 1;
        this.table[i++] = key;
        this.table[i] = value;
        return null;
    }

    public V getOrCompute(K key) {
        int l = this.size << 1;
        for (int i = 0; i < l; i += 2) {
            if (!key.equals(this.table[i])) continue;
            return (V)this.table[i + 1];
        }
        V v = this.compute(key);
        if (this.size << 1 == this.table.length) {
            Object[] n = new Object[this.table.length << 1];
            System.arraycopy(this.table, 0, n, 0, this.table.length);
            this.table = n;
        }
        int i = this.size++ << 1;
        this.table[i++] = key;
        this.table[i] = v;
        return v;
    }

    protected V compute(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> values() {
        if (this.values == null) {
            this.values = new AbstractCollection<V>(){

                @Override
                public Iterator<V> iterator() {
                    return new Iterator<V>(){
                        int index = 0;

                        @Override
                        public boolean hasNext() {
                            return this.index < ArrayMap.this.size;
                        }

                        @Override
                        public V next() {
                            if (this.index >= ArrayMap.this.size) {
                                throw new NoSuchElementException();
                            }
                            return ArrayMap.this.table[(this.index++ << 1) + 1];
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }

                @Override
                public int size() {
                    return ArrayMap.this.size;
                }
            };
        }
        return this.values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new AbstractSet<Map.Entry<K, V>>(){

            @Override
            public Iterator<Map.Entry<K, V>> iterator() {
                return new Iterator<Map.Entry<K, V>>(){
                    FastEntry<K, V> entry = new FastEntry();
                    int index = 0;

                    @Override
                    public boolean hasNext() {
                        return this.index < ArrayMap.this.size;
                    }

                    @Override
                    public FastEntry<K, V> next() {
                        if (this.index >= ArrayMap.this.size) {
                            throw new NoSuchElementException();
                        }
                        int i = this.index << 1;
                        this.entry.key = ArrayMap.this.table[i];
                        this.entry.value = ArrayMap.this.table[i + 1];
                        ++this.index;
                        return this.entry;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            @Override
            public int size() {
                return ArrayMap.this.size;
            }
        };
    }

    static class FastEntry<K, V>
    implements Map.Entry<K, V> {
        K key;
        V value;

        FastEntry() {
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }
}

