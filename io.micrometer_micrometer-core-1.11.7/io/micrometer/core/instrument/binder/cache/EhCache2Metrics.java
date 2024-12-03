/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  io.micrometer.common.lang.Nullable
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.statistics.StatisticsGateway
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
import io.micrometer.core.instrument.binder.cache.CacheMeterBinder;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.statistics.StatisticsGateway;

@NonNullApi
@NonNullFields
public class EhCache2Metrics
extends CacheMeterBinder<Ehcache> {
    private static final String DESCRIPTION_CACHE_PUTS_ADDED = "Cache puts (added or updated)";
    private static final String DESCRIPTION_CACHE_MISSES = "The number of times cache lookup methods have not returned a value, due to expiry or because the key was not found";
    private static final String DESCRIPTION_CACHE_XA_COMMITS = "The number of transaction commits";
    private static final String DESCRIPTION_CACHE_XA_ROLLBACKS = "The number of transaction rollbacks";
    private static final String DESCRIPTION_CACHE_XA_RECOVERIES = "The number of transaction recoveries";

    public EhCache2Metrics(Ehcache cache, Iterable<Tag> tags) {
        super(cache, cache.getName(), tags);
    }

    public static Ehcache monitor(MeterRegistry registry, Ehcache cache, String ... tags) {
        return EhCache2Metrics.monitor(registry, cache, Tags.of(tags));
    }

    public static Ehcache monitor(MeterRegistry registry, Ehcache cache, Iterable<Tag> tags) {
        new EhCache2Metrics(cache, tags).bindTo(registry);
        return cache;
    }

    @Override
    protected Long size() {
        return this.getOrDefault(StatisticsGateway::getSize, null);
    }

    @Override
    protected long hitCount() {
        return this.getOrDefault(StatisticsGateway::cacheHitCount, 0L);
    }

    @Override
    protected Long missCount() {
        return this.getOrDefault(StatisticsGateway::cacheMissCount, null);
    }

    @Override
    protected Long evictionCount() {
        return this.getOrDefault(StatisticsGateway::cacheEvictedCount, null);
    }

    @Override
    protected long putCount() {
        return this.getOrDefault(StatisticsGateway::cachePutCount, 0L);
    }

    @Override
    protected void bindImplementationSpecificMetrics(MeterRegistry registry) {
        StatisticsGateway stats = this.getStats();
        Gauge.builder("cache.remoteSize", stats, StatisticsGateway::getRemoteSize).tags(this.getTagsWithCacheName()).description("The number of entries held remotely in this cache").register(registry);
        FunctionCounter.builder("cache.removals", stats, StatisticsGateway::cacheRemoveCount).tags(this.getTagsWithCacheName()).description("Cache removals").register(registry);
        FunctionCounter.builder("cache.puts.added", stats, StatisticsGateway::cachePutAddedCount).tags(this.getTagsWithCacheName()).tags("result", "added").description(DESCRIPTION_CACHE_PUTS_ADDED).register(registry);
        FunctionCounter.builder("cache.puts.added", stats, StatisticsGateway::cachePutUpdatedCount).tags(this.getTagsWithCacheName()).tags("result", "updated").description(DESCRIPTION_CACHE_PUTS_ADDED).register(registry);
        this.missMetrics(registry);
        this.commitTransactionMetrics(registry);
        this.rollbackTransactionMetrics(registry);
        this.recoveryTransactionMetrics(registry);
        Gauge.builder("cache.local.offheap.size", stats, StatisticsGateway::getLocalOffHeapSizeInBytes).tags(this.getTagsWithCacheName()).description("Local off-heap size").baseUnit("bytes").register(registry);
        Gauge.builder("cache.local.heap.size", stats, StatisticsGateway::getLocalHeapSizeInBytes).tags(this.getTagsWithCacheName()).description("Local heap size").baseUnit("bytes").register(registry);
        Gauge.builder("cache.local.disk.size", stats, StatisticsGateway::getLocalDiskSizeInBytes).tags(this.getTagsWithCacheName()).description("Local disk size").baseUnit("bytes").register(registry);
    }

    @Nullable
    private StatisticsGateway getStats() {
        Ehcache cache = (Ehcache)this.getCache();
        return cache != null ? cache.getStatistics() : null;
    }

    private void missMetrics(MeterRegistry registry) {
        StatisticsGateway stats = this.getStats();
        FunctionCounter.builder("cache.misses", stats, StatisticsGateway::cacheMissExpiredCount).tags(this.getTagsWithCacheName()).tags("reason", "expired").description(DESCRIPTION_CACHE_MISSES).register(registry);
        FunctionCounter.builder("cache.misses", stats, StatisticsGateway::cacheMissNotFoundCount).tags(this.getTagsWithCacheName()).tags("reason", "notFound").description(DESCRIPTION_CACHE_MISSES).register(registry);
    }

    private void commitTransactionMetrics(MeterRegistry registry) {
        StatisticsGateway stats = this.getStats();
        FunctionCounter.builder("cache.xa.commits", stats, StatisticsGateway::xaCommitReadOnlyCount).tags(this.getTagsWithCacheName()).tags("result", "readOnly").description(DESCRIPTION_CACHE_XA_COMMITS).register(registry);
        FunctionCounter.builder("cache.xa.commits", stats, StatisticsGateway::xaCommitExceptionCount).tags(this.getTagsWithCacheName()).tags("result", "exception").description(DESCRIPTION_CACHE_XA_COMMITS).register(registry);
        FunctionCounter.builder("cache.xa.commits", stats, StatisticsGateway::xaCommitCommittedCount).tags(this.getTagsWithCacheName()).tags("result", "committed").description(DESCRIPTION_CACHE_XA_COMMITS).register(registry);
    }

    private void rollbackTransactionMetrics(MeterRegistry registry) {
        StatisticsGateway stats = this.getStats();
        FunctionCounter.builder("cache.xa.rollbacks", stats, StatisticsGateway::xaRollbackExceptionCount).tags(this.getTagsWithCacheName()).tags("result", "exception").description(DESCRIPTION_CACHE_XA_ROLLBACKS).register(registry);
        FunctionCounter.builder("cache.xa.rollbacks", stats, StatisticsGateway::xaRollbackSuccessCount).tags(this.getTagsWithCacheName()).tags("result", "success").description(DESCRIPTION_CACHE_XA_ROLLBACKS).register(registry);
    }

    private void recoveryTransactionMetrics(MeterRegistry registry) {
        StatisticsGateway stats = this.getStats();
        FunctionCounter.builder("cache.xa.recoveries", stats, StatisticsGateway::xaRecoveryNothingCount).tags(this.getTagsWithCacheName()).tags("result", "nothing").description(DESCRIPTION_CACHE_XA_RECOVERIES).register(registry);
        FunctionCounter.builder("cache.xa.recoveries", stats, StatisticsGateway::xaRecoveryRecoveredCount).tags(this.getTagsWithCacheName()).tags("result", "success").description(DESCRIPTION_CACHE_XA_RECOVERIES).register(registry);
    }

    @Nullable
    private Long getOrDefault(Function<StatisticsGateway, Long> function, @Nullable Long defaultValue) {
        StatisticsGateway ref = this.getStats();
        if (ref != null) {
            return function.apply(ref);
        }
        return defaultValue;
    }

    private long getOrDefault(ToLongFunction<StatisticsGateway> function, long defaultValue) {
        StatisticsGateway ref = this.getStats();
        if (ref != null) {
            return function.applyAsLong(ref);
        }
        return defaultValue;
    }
}

