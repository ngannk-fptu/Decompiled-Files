/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.impl.labels;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.cache.ReadThroughAtlassianCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ReadThroughMostPopularCache<T extends Serializable> {
    private final ReadThroughCache<String, ValueWrapper<T>> cache;

    ReadThroughMostPopularCache(ReadThroughCache<String, ValueWrapper<T>> cache) {
        this.cache = cache;
    }

    public static <T extends Serializable> ReadThroughMostPopularCache<T> create(CacheFactory cacheFactory, CoreCache cacheName) {
        return new ReadThroughMostPopularCache<T>(ReadThroughAtlassianCache.create(cacheFactory, cacheName));
    }

    public void clear() {
        this.cache.removeAll();
    }

    public List<T> getMostPopularGlobal(Optional<Integer> maxResults, Function<Optional<Integer>, List<T>> delegate) {
        Optional<Integer> cacheableResultsLimit = ReadThroughMostPopularCache.calculateCacheableResultsLimit(maxResults);
        return this.cache.get(ReadThroughMostPopularCache.cacheKey(cacheableResultsLimit), () -> ReadThroughMostPopularCache.valueWrapper(delegate, cacheableResultsLimit)).trim(maxResults);
    }

    public List<T> getMostPopularInSpace(String spaceKey, Optional<Integer> maxResults, Function<Optional<Integer>, List<T>> delegate) {
        Optional<Integer> cacheableResultsLimit = ReadThroughMostPopularCache.calculateCacheableResultsLimit(maxResults);
        return this.cache.get(ReadThroughMostPopularCache.cacheKey(spaceKey, cacheableResultsLimit), () -> ReadThroughMostPopularCache.valueWrapper(delegate, cacheableResultsLimit)).trim(maxResults);
    }

    private static <T extends Serializable> ValueWrapper<T> valueWrapper(Function<Optional<Integer>, List<T>> delegate, Optional<Integer> cacheableResultsLimit) {
        return new ValueWrapper<T>(delegate.apply(cacheableResultsLimit));
    }

    private static String cacheKey(Optional<Integer> maxResults) {
        return "MostPopular-Global-" + ReadThroughMostPopularCache.maxResultsCacheKey(maxResults);
    }

    private static String maxResultsCacheKey(Optional<Integer> maxResults) {
        return maxResults.map(x -> "limit- " + x).orElse("unlimited");
    }

    private static String cacheKey(String spaceKey, Optional<Integer> maxResults) {
        return "MostPopular-Space-" + spaceKey + "-" + ReadThroughMostPopularCache.maxResultsCacheKey(maxResults);
    }

    private static Optional<Integer> calculateCacheableResultsLimit(Optional<Integer> maxResults) {
        return maxResults.map(i -> {
            int highestOneBit = Integer.highestOneBit(i);
            int ceilingOneBit = i == highestOneBit ? highestOneBit : Integer.rotateLeft(highestOneBit, 1);
            return Math.max(128, ceilingOneBit);
        });
    }

    private static class ValueWrapper<T extends Serializable>
    implements Serializable {
        private final ImmutableList<T> results;

        public ValueWrapper(List<T> results) {
            this.results = ImmutableList.copyOf(results);
        }

        public List<T> trim(Optional<Integer> maxResults) {
            return (List)maxResults.map(i -> this.results.subList(0, Math.min(this.results.size(), i))).orElse(this.results);
        }
    }
}

