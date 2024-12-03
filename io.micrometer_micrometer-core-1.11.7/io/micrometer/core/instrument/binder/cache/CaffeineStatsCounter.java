/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.benmanes.caffeine.cache.Cache
 *  com.github.benmanes.caffeine.cache.RemovalCause
 *  com.github.benmanes.caffeine.cache.stats.CacheStats
 *  com.github.benmanes.caffeine.cache.stats.StatsCounter
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 */
package io.micrometer.core.instrument.binder.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.github.benmanes.caffeine.cache.stats.StatsCounter;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@NonNullApi
@NonNullFields
public final class CaffeineStatsCounter
implements StatsCounter {
    private static final String DESCRIPTION_CACHE_GETS = "The number of times cache lookup methods have returned a cached (hit) or uncached (newly loaded) value (miss).";
    private static final String DESCRIPTION_CACHE_LOADS = "The number of times cache lookup methods have successfully loaded a new value or failed to load a new value, either because no value was found or an exception was thrown while loading";
    private final MeterRegistry registry;
    private final Tags tags;
    private final Counter hitCount;
    private final Counter missCount;
    private final Timer loadSuccesses;
    private final Timer loadFailures;
    private final EnumMap<RemovalCause, DistributionSummary> evictionMetrics;

    public CaffeineStatsCounter(MeterRegistry registry, String cacheName) {
        this(registry, cacheName, Tags.empty());
    }

    public CaffeineStatsCounter(MeterRegistry registry, String cacheName, Iterable<Tag> extraTags) {
        Objects.requireNonNull(registry);
        Objects.requireNonNull(cacheName);
        Objects.requireNonNull(extraTags);
        this.registry = registry;
        this.tags = Tags.concat(extraTags, "cache", cacheName);
        this.hitCount = Counter.builder("cache.gets").tag("result", "hit").tags(this.tags).description(DESCRIPTION_CACHE_GETS).register(registry);
        this.missCount = Counter.builder("cache.gets").tag("result", "miss").tags(this.tags).description(DESCRIPTION_CACHE_GETS).register(registry);
        this.loadSuccesses = ((Timer.Builder)Timer.builder("cache.loads").tag("result", "success").tags((Iterable)this.tags)).description(DESCRIPTION_CACHE_LOADS).register(registry);
        this.loadFailures = ((Timer.Builder)Timer.builder("cache.loads").tag("result", "failure").tags((Iterable)this.tags)).description(DESCRIPTION_CACHE_LOADS).register(registry);
        this.evictionMetrics = new EnumMap(RemovalCause.class);
        Arrays.stream(RemovalCause.values()).forEach(cause -> this.evictionMetrics.put((RemovalCause)cause, DistributionSummary.builder("cache.evictions").tag("cause", cause.name()).tags(this.tags).description("The number of times the cache was evicted.").register(registry)));
    }

    public void registerSizeMetric(Cache<?, ?> cache) {
        Gauge.builder("cache.size", cache, Cache::estimatedSize).tags(this.tags).description("The approximate number of entries in this cache.").register(this.registry);
    }

    public void recordHits(int count) {
        this.hitCount.increment(count);
    }

    public void recordMisses(int count) {
        this.missCount.increment(count);
    }

    public void recordLoadSuccess(long loadTime) {
        this.loadSuccesses.record(loadTime, TimeUnit.NANOSECONDS);
    }

    public void recordLoadFailure(long loadTime) {
        this.loadFailures.record(loadTime, TimeUnit.NANOSECONDS);
    }

    public void recordEviction() {
    }

    public void recordEviction(int weight, RemovalCause cause) {
        this.evictionMetrics.get(cause).record(weight);
    }

    public CacheStats snapshot() {
        return CacheStats.of((long)((long)this.hitCount.count()), (long)((long)this.missCount.count()), (long)this.loadSuccesses.count(), (long)this.loadFailures.count(), (long)((long)this.loadSuccesses.totalTime(TimeUnit.NANOSECONDS) + (long)this.loadFailures.totalTime(TimeUnit.NANOSECONDS)), (long)this.evictionMetrics.values().stream().mapToLong(DistributionSummary::count).sum(), (long)((long)this.evictionMetrics.values().stream().mapToDouble(DistributionSummary::totalAmount).sum()));
    }

    public String toString() {
        return this.snapshot().toString();
    }
}

