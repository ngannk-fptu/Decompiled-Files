/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

@PublicApi
public interface LocalCacheOperations<K, V> {
    public Optional<V> get(K var1);

    public V get(K var1, Supplier<? extends V> var2);

    default public Map<K, V> getBulk(Function<Set<K>, Map<K, V>> factory, K ... keys) {
        return this.getBulk(factory, (Iterable<K>)Arrays.asList(keys));
    }

    public Map<K, V> getBulk(Function<Set<K>, Map<K, V>> var1, Iterable<K> var2);

    public void put(K var1, V var2);

    public Optional<V> putIfAbsent(K var1, V var2);

    public boolean replaceIf(K var1, V var2, V var3);

    public boolean removeIf(K var1, V var2);

    public void remove(K var1);

    public void removeAll();
}

