/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class WeakWeakMap<K, V>
extends WeakHashMap<K, V> {
    public WeakWeakMap() {
    }

    public WeakWeakMap(int n) {
        super(n);
    }

    public WeakWeakMap(int n, float f) {
        super(n, f);
    }

    public WeakWeakMap(Map<? extends K, ? extends V> map) {
        super(map);
    }

    @Override
    public V put(K k, V v) {
        return (V)super.put(k, new WeakReference<V>(v));
    }

    @Override
    public V get(Object object) {
        WeakReference weakReference = (WeakReference)super.get(object);
        return weakReference != null ? (V)weakReference.get() : null;
    }

    @Override
    public V remove(Object object) {
        WeakReference weakReference = (WeakReference)super.remove(object);
        return weakReference != null ? (V)weakReference.get() : null;
    }

    @Override
    public boolean containsValue(Object object) {
        for (V v : this.values()) {
            if (object != v && (v == null || !v.equals(object))) continue;
            return true;
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new AbstractSet<Map.Entry<K, V>>(){

            @Override
            public Iterator<Map.Entry<K, V>> iterator() {
                return new Iterator<Map.Entry<K, V>>(){
                    final Iterator<Map.Entry<K, WeakReference<V>>> iterator;
                    {
                        this.iterator = WeakWeakMap.super.entrySet().iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.iterator.hasNext();
                    }

                    @Override
                    public Map.Entry<K, V> next() {
                        return new Map.Entry<K, V>(){
                            final Map.Entry<K, WeakReference<V>> entry;
                            {
                                this.entry = iterator.next();
                            }

                            @Override
                            public K getKey() {
                                return this.entry.getKey();
                            }

                            @Override
                            public V getValue() {
                                WeakReference weakReference = this.entry.getValue();
                                return weakReference.get();
                            }

                            @Override
                            public V setValue(V v) {
                                WeakReference weakReference = this.entry.setValue(new WeakReference(v));
                                return weakReference != null ? (Object)weakReference.get() : null;
                            }

                            @Override
                            public boolean equals(Object object) {
                                return this.entry.equals(object);
                            }

                            @Override
                            public int hashCode() {
                                return this.entry.hashCode();
                            }

                            public String toString() {
                                return this.entry.toString();
                            }
                        };
                    }

                    @Override
                    public void remove() {
                        this.iterator.remove();
                    }
                };
            }

            @Override
            public int size() {
                return WeakWeakMap.this.size();
            }
        };
    }

    @Override
    public Collection<V> values() {
        return new AbstractCollection<V>(){

            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>(){
                    Iterator<WeakReference<V>> iterator;
                    {
                        this.iterator = WeakWeakMap.super.values().iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.iterator.hasNext();
                    }

                    @Override
                    public V next() {
                        WeakReference weakReference = this.iterator.next();
                        return weakReference.get();
                    }

                    @Override
                    public void remove() {
                        this.iterator.remove();
                    }
                };
            }

            @Override
            public int size() {
                return WeakWeakMap.this.size();
            }
        };
    }
}

