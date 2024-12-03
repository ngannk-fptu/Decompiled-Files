/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.binder.cache;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.lang.ref.WeakReference;

@NonNullApi
@NonNullFields
public abstract class CacheMeterBinder<C>
implements MeterBinder {
    private static final String DESCRIPTION_CACHE_GETS = "The number of times cache lookup methods have returned a cached (hit) or uncached (newly loaded or null) value (miss).";
    private final WeakReference<C> cacheRef;
    private final Iterable<Tag> tags;

    public CacheMeterBinder(C cache, String cacheName, Iterable<Tag> tags) {
        this.tags = Tags.concat(tags, "cache", cacheName);
        this.cacheRef = new WeakReference<C>(cache);
    }

    @Nullable
    protected C getCache() {
        return (C)this.cacheRef.get();
    }

    @Override
    public final void bindTo(MeterRegistry registry) {
        C cache = this.getCache();
        if (this.size() != null) {
            Gauge.builder("cache.size", cache, c -> {
                Long size = this.size();
                return size == null ? 0.0 : (double)size.longValue();
            }).tags(this.tags).description("The number of entries in this cache. This may be an approximation, depending on the type of cache.").register(registry);
        }
        if (this.missCount() != null) {
            FunctionCounter.builder("cache.gets", cache, c -> {
                Long misses = this.missCount();
                return misses == null ? 0.0 : (double)misses.longValue();
            }).tags(this.tags).tag("result", "miss").description(DESCRIPTION_CACHE_GETS).register(registry);
        }
        FunctionCounter.builder("cache.gets", cache, c -> this.hitCount()).tags(this.tags).tag("result", "hit").description(DESCRIPTION_CACHE_GETS).register(registry);
        FunctionCounter.builder("cache.puts", cache, c -> this.putCount()).tags(this.tags).description("The number of entries added to the cache").register(registry);
        if (this.evictionCount() != null) {
            FunctionCounter.builder("cache.evictions", cache, c -> {
                Long evictions = this.evictionCount();
                return evictions == null ? 0.0 : (double)evictions.longValue();
            }).tags(this.tags).description("The number of times the cache was evicted.").register(registry);
        }
        this.bindImplementationSpecificMetrics(registry);
    }

    @Nullable
    protected abstract Long size();

    protected abstract long hitCount();

    @Nullable
    protected abstract Long missCount();

    @Nullable
    protected abstract Long evictionCount();

    protected abstract long putCount();

    protected abstract void bindImplementationSpecificMetrics(MeterRegistry var1);

    protected Iterable<Tag> getTagsWithCacheName() {
        return this.tags;
    }
}

