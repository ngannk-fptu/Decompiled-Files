/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.integration;

import java.util.Map;
import javax.cache.integration.CacheLoaderException;

public interface CacheLoader<K, V> {
    public V load(K var1) throws CacheLoaderException;

    public Map<K, V> loadAll(Iterable<? extends K> var1) throws CacheLoaderException;
}

