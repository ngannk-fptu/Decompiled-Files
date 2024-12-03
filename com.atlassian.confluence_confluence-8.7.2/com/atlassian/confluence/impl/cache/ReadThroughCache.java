/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.cache;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface ReadThroughCache<K, V> {
    default public V get(K cacheKey, Supplier<V> delegateLoader) {
        return (V)this.get(cacheKey, delegateLoader, v -> true);
    }

    public V get(K var1, Supplier<V> var2, Predicate<V> var3);

    public Map<K, V> getBulk(Set<K> var1, Function<Set<K>, Map<K, V>> var2);

    public void remove(K var1);

    public void removeAll();
}

