/*
 * Decompiled with CFR 0.152.
 */
package javax.cache;

import java.io.Closeable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;

public interface Cache<K, V>
extends Iterable<Entry<K, V>>,
Closeable {
    public V get(K var1);

    public Map<K, V> getAll(Set<? extends K> var1);

    public boolean containsKey(K var1);

    public void loadAll(Set<? extends K> var1, boolean var2, CompletionListener var3);

    public void put(K var1, V var2);

    public V getAndPut(K var1, V var2);

    public void putAll(Map<? extends K, ? extends V> var1);

    public boolean putIfAbsent(K var1, V var2);

    public boolean remove(K var1);

    public boolean remove(K var1, V var2);

    public V getAndRemove(K var1);

    public boolean replace(K var1, V var2, V var3);

    public boolean replace(K var1, V var2);

    public V getAndReplace(K var1, V var2);

    public void removeAll(Set<? extends K> var1);

    public void removeAll();

    public void clear();

    public <C extends Configuration<K, V>> C getConfiguration(Class<C> var1);

    public <T> T invoke(K var1, EntryProcessor<K, V, T> var2, Object ... var3) throws EntryProcessorException;

    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> var1, EntryProcessor<K, V, T> var2, Object ... var3);

    public String getName();

    public CacheManager getCacheManager();

    @Override
    public void close();

    public boolean isClosed();

    public <T> T unwrap(Class<T> var1);

    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> var1);

    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> var1);

    @Override
    public Iterator<Entry<K, V>> iterator();

    public static interface Entry<K, V> {
        public K getKey();

        public V getValue();

        public <T> T unwrap(Class<T> var1);
    }
}

