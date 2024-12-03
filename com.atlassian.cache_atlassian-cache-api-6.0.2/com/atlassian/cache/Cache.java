/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.cache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.Supplier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PublicApi
public interface Cache<K, V> {
    @Nonnull
    public String getName();

    public boolean containsKey(@Nonnull K var1);

    @Nonnull
    public Collection<K> getKeys();

    @Nullable
    public V get(@Nonnull K var1);

    @Nonnull
    public V get(@Nonnull K var1, @Nonnull Supplier<? extends V> var2);

    @Nonnull
    default public Map<K, V> getBulk(@Nonnull Set<K> keys, @Nonnull Function<Set<K>, Map<K, V>> valuesSupplier) {
        HashMap<K, V> result = new HashMap<K, V>();
        HashSet<K> keysToLoad = new HashSet<K>();
        for (K key2 : keys) {
            V value2 = this.get(key2);
            if (value2 != null) {
                result.put(key2, value2);
                continue;
            }
            keysToLoad.add(key2);
        }
        if (!keysToLoad.isEmpty()) {
            Map<Object, Object> loadedValues = valuesSupplier.apply(keysToLoad);
            loadedValues.forEach((key, value) -> this.get(key, () -> value));
            loadedValues.forEach(result::put);
        }
        return result;
    }

    public void put(@Nonnull K var1, @Nonnull V var2);

    @Nullable
    public V putIfAbsent(@Nonnull K var1, @Nonnull V var2);

    public void remove(@Nonnull K var1);

    public boolean remove(@Nonnull K var1, @Nonnull V var2);

    public void removeAll();

    public boolean replace(@Nonnull K var1, @Nonnull V var2, @Nonnull V var3);

    @Deprecated
    public void addListener(@Nonnull CacheEntryListener<K, V> var1, boolean var2);

    @Deprecated
    public void removeListener(@Nonnull CacheEntryListener<K, V> var1);
}

