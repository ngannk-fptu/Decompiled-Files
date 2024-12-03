/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.collections;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.tomcat.util.res.StringManager;

public class CaseInsensitiveKeyMap<V>
extends AbstractMap<String, V> {
    private static final StringManager sm = StringManager.getManager(CaseInsensitiveKeyMap.class);
    private final Map<Key, V> map = new HashMap<Key, V>();

    @Override
    public V get(Object key) {
        return this.map.get(Key.getInstance(key));
    }

    @Override
    public V put(String key, V value) {
        Key caseInsensitiveKey = Key.getInstance(key);
        if (caseInsensitiveKey == null) {
            throw new NullPointerException(sm.getString("caseInsensitiveKeyMap.nullKey"));
        }
        return this.map.put(caseInsensitiveKey, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(Key.getInstance(key));
    }

    @Override
    public V remove(Object key) {
        return this.map.remove(Key.getInstance(key));
    }

    @Override
    public Set<Map.Entry<String, V>> entrySet() {
        return new EntrySet<V>(this.map.entrySet());
    }

    private static class Key {
        private final String key;
        private final String lcKey;

        private Key(String key) {
            this.key = key;
            this.lcKey = key.toLowerCase(Locale.ENGLISH);
        }

        public String getKey() {
            return this.key;
        }

        public int hashCode() {
            return this.lcKey.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key)obj;
            return this.lcKey.equals(other.lcKey);
        }

        public static Key getInstance(Object o) {
            if (o instanceof String) {
                return new Key((String)o);
            }
            return null;
        }
    }

    private static class EntrySet<V>
    extends AbstractSet<Map.Entry<String, V>> {
        private final Set<Map.Entry<Key, V>> entrySet;

        EntrySet(Set<Map.Entry<Key, V>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public Iterator<Map.Entry<String, V>> iterator() {
            return new EntryIterator<V>(this.entrySet.iterator());
        }

        @Override
        public int size() {
            return this.entrySet.size();
        }
    }

    private static class EntryImpl<V>
    implements Map.Entry<String, V> {
        private final String key;
        private final V value;

        EntryImpl(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
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

    private static class EntryIterator<V>
    implements Iterator<Map.Entry<String, V>> {
        private final Iterator<Map.Entry<Key, V>> iterator;

        EntryIterator(Iterator<Map.Entry<Key, V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public Map.Entry<String, V> next() {
            Map.Entry<Key, V> entry = this.iterator.next();
            return new EntryImpl<V>(entry.getKey().getKey(), entry.getValue());
        }

        @Override
        public void remove() {
            this.iterator.remove();
        }
    }
}

