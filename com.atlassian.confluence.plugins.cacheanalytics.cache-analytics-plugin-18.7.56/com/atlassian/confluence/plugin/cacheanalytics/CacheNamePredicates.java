/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cache.CoreCache
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugin.cacheanalytics;

import com.atlassian.confluence.cache.CoreCache;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class CacheNamePredicates {
    private static final Set<String> CORE_CACHE_NAMES = CacheNamePredicates.getCoreCacheNames();

    public static Optional<Predicate<String>> coreCacheNameFilter() {
        return CORE_CACHE_NAMES == null ? Optional.empty() : Optional.of(CORE_CACHE_NAMES::contains);
    }

    @Nullable
    private static Set<String> getCoreCacheNames() {
        try {
            return (Set)Arrays.stream(CoreCache.values()).map(cache -> (String)cache.resolve(name -> name)).collect(ImmutableSet.toImmutableSet());
        }
        catch (NoClassDefFoundError ex) {
            return null;
        }
    }
}

