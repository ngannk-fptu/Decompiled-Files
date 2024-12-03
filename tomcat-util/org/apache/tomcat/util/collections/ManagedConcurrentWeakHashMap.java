/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.collections;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ManagedConcurrentWeakHashMap<K, V>
extends AbstractMap<K, V>
implements ConcurrentMap<K, V> {
    private final ConcurrentMap<Key, V> map = new ConcurrentHashMap<Key, V>();
    private final ReferenceQueue<Object> queue = new ReferenceQueue();

    public void maintain() {
        Key key;
        while ((key = (Key)this.queue.poll()) != null) {
            if (key.isDead()) continue;
            key.ackDeath();
            this.map.remove(key);
        }
    }

    private Key createStoreKey(Object key) {
        return new Key(key, this.queue);
    }

    private Key createLookupKey(Object key) {
        return new Key(key, null);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            return false;
        }
        return this.map.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        return this.map.containsKey(this.createLookupKey(key));
    }

    @Override
    public V get(Object key) {
        if (key == null) {
            return null;
        }
        return this.map.get(this.createLookupKey(key));
    }

    @Override
    public V put(K key, V value) {
        Objects.requireNonNull(value);
        return this.map.put(this.createStoreKey(key), value);
    }

    @Override
    public V remove(Object key) {
        return this.map.remove(this.createLookupKey(key));
    }

    @Override
    public void clear() {
        this.map.clear();
        this.maintain();
    }

    @Override
    public V putIfAbsent(K key, V value) {
        Objects.requireNonNull(value);
        Key storeKey = this.createStoreKey(key);
        V oldValue = this.map.putIfAbsent(storeKey, value);
        if (oldValue != null) {
            storeKey.ackDeath();
        }
        return oldValue;
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (value == null) {
            return false;
        }
        return this.map.remove(this.createLookupKey(key), value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        Objects.requireNonNull(newValue);
        return this.map.replace(this.createLookupKey(key), oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        Objects.requireNonNull(value);
        return this.map.replace(this.createLookupKey(key), value);
    }

    @Override
    public Collection<V> values() {
        return this.map.values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new AbstractSet<Map.Entry<K, V>>(){

            @Override
            public boolean isEmpty() {
                return ManagedConcurrentWeakHashMap.this.map.isEmpty();
            }

            @Override
            public int size() {
                return ManagedConcurrentWeakHashMap.this.map.size();
            }

            @Override
            public Iterator<Map.Entry<K, V>> iterator() {
                return new Iterator<Map.Entry<K, V>>(){
                    private final Iterator<Map.Entry<Key, V>> it;
                    {
                        this.it = ManagedConcurrentWeakHashMap.this.map.entrySet().iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.it.hasNext();
                    }

                    @Override
                    public Map.Entry<K, V> next() {
                        return new Map.Entry<K, V>(){
                            private final Map.Entry<Key, V> en;
                            {
                                this.en = (Map.Entry)it.next();
                            }

                            @Override
                            public K getKey() {
                                return this.en.getKey().get();
                            }

                            @Override
                            public V getValue() {
                                return this.en.getValue();
                            }

                            @Override
                            public V setValue(V value) {
                                Objects.requireNonNull(value);
                                return this.en.setValue(value);
                            }
                        };
                    }

                    @Override
                    public void remove() {
                        this.it.remove();
                    }
                };
            }
        };
    }

    private static class Key
    extends WeakReference<Object> {
        private final int hash;
        private boolean dead;

        Key(Object key, ReferenceQueue<Object> queue) {
            super(key, queue);
            this.hash = key.hashCode();
        }

        public int hashCode() {
            return this.hash;
        }

        public boolean equals(Object obj) {
            Object oB;
            if (this == obj) {
                return true;
            }
            if (this.dead) {
                return false;
            }
            if (!(obj instanceof Reference)) {
                return false;
            }
            Object oA = this.get();
            if (oA == (oB = ((Reference)obj).get())) {
                return true;
            }
            if (oA == null || oB == null) {
                return false;
            }
            return oA.equals(oB);
        }

        public void ackDeath() {
            this.dead = true;
        }

        public boolean isDead() {
            return this.dead;
        }
    }
}

