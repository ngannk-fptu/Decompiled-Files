/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class ConcurrentWeakHashMap<K, V>
extends AbstractMap<K, V> {
    private ConcurrentHashMap<Key, V> map = new ConcurrentHashMap();
    private ReferenceQueue<Object> queue = new ReferenceQueue();

    @Override
    public void clear() {
        this.expungeStaleEntries();
        this.map.clear();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(Object key) {
        return this.map.get(this.wrap(key));
    }

    @Override
    public V put(K key, V value) {
        this.expungeStaleEntries();
        return this.map.put(this.wrap(key), value);
    }

    @Override
    public V remove(Object key) {
        this.expungeStaleEntries();
        return this.map.remove(this.wrap(key));
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public int size() {
        this.expungeStaleEntries();
        return this.map.size();
    }

    private Key wrap(Object key) {
        return new Key(key, this.queue);
    }

    private void expungeStaleEntries() {
        Key key;
        while ((key = (Key)this.queue.poll()) != null) {
            this.map.remove(key);
        }
    }

    private static class Key
    extends WeakReference<Object> {
        private int hashCode;

        public Key(Object key, ReferenceQueue<Object> queue) {
            super(key, queue);
            this.hashCode = key.hashCode();
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Key) {
                Key that = (Key)obj;
                Object value = this.get();
                return value != null && value.equals(that.get());
            }
            return false;
        }
    }
}

