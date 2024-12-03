/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.cache.ToolkitCache
 *  org.terracotta.toolkit.cache.ToolkitCacheListener
 *  org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock
 *  org.terracotta.toolkit.config.Configuration
 *  org.terracotta.toolkit.search.QueryBuilder
 *  org.terracotta.toolkit.search.attribute.ToolkitAttributeExtractor
 */
package org.terracotta.modules.ehcache.collections;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.terracotta.modules.ehcache.collections.SerializationHelper;
import org.terracotta.toolkit.cache.ToolkitCache;
import org.terracotta.toolkit.cache.ToolkitCacheListener;
import org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock;
import org.terracotta.toolkit.config.Configuration;
import org.terracotta.toolkit.search.QueryBuilder;
import org.terracotta.toolkit.search.attribute.ToolkitAttributeExtractor;

public class SerializedToolkitCache<K, V extends Serializable>
implements ToolkitCache<K, V> {
    private final ToolkitCache<String, V> toolkitCache;

    public SerializedToolkitCache(ToolkitCache toolkitMap) {
        this.toolkitCache = toolkitMap;
    }

    public int size() {
        return this.toolkitCache.size();
    }

    public boolean isEmpty() {
        return this.toolkitCache.isEmpty();
    }

    private static String serializeToString(Object key) {
        try {
            return SerializationHelper.serializeToString(key);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object deserializeFromString(String key) {
        try {
            return SerializationHelper.deserializeFromString(key, null);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean containsKey(Object key) {
        return this.toolkitCache.containsKey((Object)SerializedToolkitCache.serializeToString(key));
    }

    public V get(Object key) {
        return (V)((Serializable)this.toolkitCache.get((Object)SerializedToolkitCache.serializeToString(key)));
    }

    public V put(K key, V value) {
        return (V)((Serializable)this.toolkitCache.put((Object)SerializedToolkitCache.serializeToString(key), value));
    }

    public V remove(Object key) {
        return (V)((Serializable)this.toolkitCache.remove((Object)SerializedToolkitCache.serializeToString(key)));
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        HashMap<String, Serializable> tempMap = new HashMap<String, Serializable>();
        for (Map.Entry<K, V> entry : m.entrySet()) {
            tempMap.put(SerializedToolkitCache.serializeToString(entry.getKey()), (Serializable)entry.getValue());
        }
        this.toolkitCache.putAll(tempMap);
    }

    public void clear() {
        this.toolkitCache.clear();
    }

    public Set<K> keySet() {
        return new ToolkitKeySet(this.toolkitCache.keySet());
    }

    public boolean isDestroyed() {
        return this.toolkitCache.isDestroyed();
    }

    public void destroy() {
        this.toolkitCache.destroy();
    }

    public String getName() {
        return this.toolkitCache.getName();
    }

    public ToolkitReadWriteLock createLockForKey(K key) {
        return this.toolkitCache.createLockForKey((Object)SerializedToolkitCache.serializeToString(key));
    }

    public void removeNoReturn(Object key) {
        this.toolkitCache.removeNoReturn((Object)SerializedToolkitCache.serializeToString(key));
    }

    public void putNoReturn(K key, V value) {
        this.toolkitCache.putNoReturn((Object)SerializedToolkitCache.serializeToString(key), value);
    }

    public Map<K, V> getAll(Collection<? extends K> keys) {
        HashSet<String> tempSet = new HashSet<String>();
        for (K key : keys) {
            tempSet.add(SerializedToolkitCache.serializeToString(key));
        }
        Map m = this.toolkitCache.getAll(tempSet);
        Map tempMap = m.isEmpty() ? Collections.EMPTY_MAP : new HashMap();
        for (Map.Entry entry : m.entrySet()) {
            tempMap.put(SerializedToolkitCache.deserializeFromString((String)entry.getKey()), (Serializable)entry.getValue());
        }
        return tempMap;
    }

    public Configuration getConfiguration() {
        return this.toolkitCache.getConfiguration();
    }

    public void setConfigField(String name, Serializable value) {
        this.toolkitCache.setConfigField(name, value);
    }

    public boolean containsValue(Object value) {
        return this.toolkitCache.containsValue(value);
    }

    public V putIfAbsent(K key, V value) {
        return (V)((Serializable)this.toolkitCache.putIfAbsent((Object)SerializedToolkitCache.serializeToString(key), value));
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return new ToolkitEntrySet(this.toolkitCache.entrySet());
    }

    public Collection<V> values() {
        return this.toolkitCache.values();
    }

    public boolean remove(Object key, Object value) {
        return this.toolkitCache.remove((Object)SerializedToolkitCache.serializeToString(key), value);
    }

    public boolean replace(K key, V oldValue, V newValue) {
        return this.toolkitCache.replace((Object)SerializedToolkitCache.serializeToString(key), oldValue, newValue);
    }

    public V replace(K key, V value) {
        return (V)((Serializable)this.toolkitCache.replace((Object)SerializedToolkitCache.serializeToString(key), value));
    }

    public Map<K, V> getAllQuiet(Collection<K> keys) {
        HashSet<String> tempSet = new HashSet<String>();
        for (K key : keys) {
            tempSet.add(SerializedToolkitCache.serializeToString(key));
        }
        Map m = this.toolkitCache.getAllQuiet(tempSet);
        Map tempMap = m.isEmpty() ? Collections.EMPTY_MAP : new HashMap();
        for (Map.Entry entry : m.entrySet()) {
            tempMap.put(SerializedToolkitCache.deserializeFromString((String)entry.getKey()), (Serializable)entry.getValue());
        }
        return tempMap;
    }

    public V getQuiet(Object key) {
        return (V)((Serializable)this.toolkitCache.get((Object)SerializedToolkitCache.serializeToString(key)));
    }

    public void putNoReturn(K key, V value, long createTimeInSecs, int customMaxTTISeconds, int customMaxTTLSeconds) {
        this.toolkitCache.putNoReturn((Object)SerializedToolkitCache.serializeToString(key), value, createTimeInSecs, customMaxTTISeconds, customMaxTTLSeconds);
    }

    public V putIfAbsent(K key, V value, long createTimeInSecs, int customMaxTTISeconds, int customMaxTTLSeconds) {
        return (V)((Serializable)this.toolkitCache.putIfAbsent((Object)SerializedToolkitCache.serializeToString(key), value, createTimeInSecs, customMaxTTISeconds, customMaxTTLSeconds));
    }

    public void addListener(ToolkitCacheListener<K> listener) {
        throw new UnsupportedOperationException();
    }

    public void removeListener(ToolkitCacheListener<K> listener) {
        throw new UnsupportedOperationException();
    }

    public void setAttributeExtractor(ToolkitAttributeExtractor attrExtractor) {
        this.toolkitCache.setAttributeExtractor(attrExtractor);
    }

    public QueryBuilder createQueryBuilder() {
        return this.toolkitCache.createQueryBuilder();
    }

    public boolean isBulkLoadEnabled() {
        return this.toolkitCache.isBulkLoadEnabled();
    }

    public boolean isNodeBulkLoadEnabled() {
        return this.toolkitCache.isNodeBulkLoadEnabled();
    }

    public void setNodeBulkLoadEnabled(boolean enabledBulkLoad) {
        this.toolkitCache.setNodeBulkLoadEnabled(enabledBulkLoad);
    }

    public void waitUntilBulkLoadComplete() throws InterruptedException {
        this.toolkitCache.waitUntilBulkLoadComplete();
    }

    private static class ToolkitKeyIterator<K>
    implements Iterator<K> {
        private final Iterator<String> iter;

        public ToolkitKeyIterator(Iterator<String> iter) {
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public K next() {
            String k = this.iter.next();
            if (k == null) {
                return null;
            }
            return (K)SerializedToolkitCache.deserializeFromString(k);
        }

        @Override
        public void remove() {
            this.iter.remove();
        }
    }

    private static class ToolkitKeySet<K>
    implements Set<K> {
        private final Set<String> set;

        public ToolkitKeySet(Set<String> set) {
            this.set = set;
        }

        @Override
        public int size() {
            return this.set.size();
        }

        @Override
        public boolean isEmpty() {
            return this.set.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return this.set.contains(SerializedToolkitCache.serializeToString(o));
        }

        @Override
        public Iterator<K> iterator() {
            return new ToolkitKeyIterator(this.set.iterator());
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(K e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends K> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }

    private static class ToolkitCacheEntry<K, V>
    implements Map.Entry<K, V> {
        private final K k;
        private final V v;

        public ToolkitCacheEntry(K k, V v) {
            this.k = k;
            this.v = v;
        }

        @Override
        public K getKey() {
            return this.k;
        }

        @Override
        public V getValue() {
            return this.v;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class ToolkitEntryIterator<K, V>
    implements Iterator<Map.Entry<K, V>> {
        private final Iterator<Map.Entry<String, V>> iter;

        public ToolkitEntryIterator(Iterator<Map.Entry<String, V>> iter) {
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public Map.Entry<K, V> next() {
            Map.Entry<String, V> entry = this.iter.next();
            if (entry == null) {
                return null;
            }
            return new ToolkitCacheEntry<Object, V>(SerializedToolkitCache.deserializeFromString(entry.getKey()), entry.getValue());
        }

        @Override
        public void remove() {
            this.iter.remove();
        }
    }

    private static class ToolkitEntrySet<K, V>
    implements Set<Map.Entry<K, V>> {
        private final Set<Map.Entry<String, V>> set;

        public ToolkitEntrySet(Set<Map.Entry<String, V>> set) {
            this.set = set;
        }

        @Override
        public int size() {
            return this.set.size();
        }

        @Override
        public boolean isEmpty() {
            return this.set.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry)o;
            ToolkitCacheEntry toolkitEntry = null;
            toolkitEntry = new ToolkitCacheEntry(SerializedToolkitCache.serializeToString(entry.getKey()), entry.getValue());
            return this.set.contains(toolkitEntry);
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new ToolkitEntryIterator(this.set.iterator());
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(Map.Entry<K, V> e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Map.Entry<K, V>> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }
}

