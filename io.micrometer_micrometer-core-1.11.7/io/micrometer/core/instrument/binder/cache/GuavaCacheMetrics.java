/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.LoadingCache
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.binder.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.LoadingCache;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.binder.cache.CacheMeterBinder;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.ToLongFunction;

@NonNullApi
@NonNullFields
public class GuavaCacheMetrics<K, V, C extends Cache<K, V>>
extends CacheMeterBinder<C> {
    private static final String DESCRIPTION_CACHE_LOAD = "The number of times cache lookup methods have successfully loaded a new value or failed to load a new value because an exception was thrown while loading";

    public static <K, V, C extends Cache<K, V>> C monitor(MeterRegistry registry, C cache, String cacheName, String ... tags) {
        return GuavaCacheMetrics.monitor(registry, cache, cacheName, Tags.of(tags));
    }

    public static <K, V, C extends Cache<K, V>> C monitor(MeterRegistry registry, C cache, String cacheName, Iterable<Tag> tags) {
        new GuavaCacheMetrics<K, V, C>(cache, cacheName, tags).bindTo(registry);
        return cache;
    }

    public GuavaCacheMetrics(C cache, String cacheName, Iterable<Tag> tags) {
        super(cache, cacheName, tags);
    }

    @Override
    protected Long size() {
        return this.getOrDefault(Cache::size, null);
    }

    @Override
    protected long hitCount() {
        return this.getOrDefault((Cache<?, ?> c) -> c.stats().hitCount(), 0L);
    }

    @Override
    protected Long missCount() {
        return this.getOrDefault((Cache<?, ?> c) -> c.stats().missCount(), null);
    }

    @Override
    protected Long evictionCount() {
        return this.getOrDefault((Cache<?, ?> c) -> c.stats().evictionCount(), null);
    }

    @Override
    protected long putCount() {
        return this.getOrDefault((Cache<?, ?> c) -> c.stats().loadCount(), 0L);
    }

    @Override
    protected void bindImplementationSpecificMetrics(MeterRegistry registry) {
        Cache cache = (Cache)this.getCache();
        if (cache instanceof LoadingCache) {
            TimeGauge.builder("cache.load.duration", cache, TimeUnit.NANOSECONDS, c -> c.stats().totalLoadTime()).tags(this.getTagsWithCacheName()).description("The time the cache has spent loading new values").register(registry);
            FunctionCounter.builder("cache.load", cache, c -> c.stats().loadSuccessCount()).tags(this.getTagsWithCacheName()).tags("result", "success").description(DESCRIPTION_CACHE_LOAD).register(registry);
            FunctionCounter.builder("cache.load", cache, c -> c.stats().loadExceptionCount()).tags(this.getTagsWithCacheName()).tags("result", "failure").description(DESCRIPTION_CACHE_LOAD).register(registry);
        }
    }

    @Nullable
    private Long getOrDefault(Function<Cache<?, ?>, Long> function, @Nullable Long defaultValue) {
        Cache ref = (Cache)this.getCache();
        if (ref != null) {
            return function.apply(ref);
        }
        return defaultValue;
    }

    private long getOrDefault(ToLongFunction<Cache<?, ?>> function, long defaultValue) {
        Cache ref = (Cache)this.getCache();
        if (ref != null) {
            return function.applyAsLong(ref);
        }
        return defaultValue;
    }
}

