/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBufAllocatorMetric
 *  io.netty.buffer.ByteBufAllocatorMetricProvider
 *  io.netty.buffer.PooledByteBufAllocator
 *  io.netty.buffer.PooledByteBufAllocatorMetric
 */
package io.micrometer.core.instrument.binder.netty4;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.netty4.NettyMeters;
import io.netty.buffer.ByteBufAllocatorMetric;
import io.netty.buffer.ByteBufAllocatorMetricProvider;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocatorMetric;

public class NettyAllocatorMetrics
implements MeterBinder {
    private final ByteBufAllocatorMetricProvider allocator;

    public NettyAllocatorMetrics(ByteBufAllocatorMetricProvider allocator) {
        this.allocator = allocator;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        int allocatorId = this.allocator.hashCode();
        ByteBufAllocatorMetric allocatorMetric = this.allocator.metric();
        Tags tags = Tags.of(NettyMeters.AllocatorKeyNames.ID.asString(), String.valueOf(allocatorId), NettyMeters.AllocatorKeyNames.ALLOCATOR_TYPE.asString(), this.allocator.getClass().getSimpleName());
        Gauge.builder(NettyMeters.ALLOCATOR_MEMORY_USED.getName(), allocatorMetric, ByteBufAllocatorMetric::usedHeapMemory).tags(tags.and(NettyMeters.AllocatorMemoryKeyNames.MEMORY_TYPE.asString(), "heap")).register(registry);
        Gauge.builder(NettyMeters.ALLOCATOR_MEMORY_USED.getName(), allocatorMetric, ByteBufAllocatorMetric::usedDirectMemory).tags(tags.and(NettyMeters.AllocatorMemoryKeyNames.MEMORY_TYPE.asString(), "direct")).register(registry);
        if (this.allocator instanceof PooledByteBufAllocator) {
            PooledByteBufAllocator pooledByteBufAllocator = (PooledByteBufAllocator)this.allocator;
            PooledByteBufAllocatorMetric pooledAllocatorMetric = pooledByteBufAllocator.metric();
            Gauge.builder(NettyMeters.ALLOCATOR_MEMORY_PINNED.getName(), pooledByteBufAllocator, PooledByteBufAllocator::pinnedHeapMemory).tags(tags.and(NettyMeters.AllocatorMemoryKeyNames.MEMORY_TYPE.asString(), "heap")).register(registry);
            Gauge.builder(NettyMeters.ALLOCATOR_MEMORY_PINNED.getName(), pooledByteBufAllocator, PooledByteBufAllocator::pinnedDirectMemory).tags(tags.and(NettyMeters.AllocatorMemoryKeyNames.MEMORY_TYPE.asString(), "direct")).register(registry);
            Gauge.builder(NettyMeters.ALLOCATOR_POOLED_ARENAS.getName(), pooledAllocatorMetric, PooledByteBufAllocatorMetric::numHeapArenas).tags(tags.and(NettyMeters.AllocatorMemoryKeyNames.MEMORY_TYPE.asString(), "heap")).register(registry);
            Gauge.builder(NettyMeters.ALLOCATOR_POOLED_ARENAS.getName(), pooledAllocatorMetric, PooledByteBufAllocatorMetric::numDirectArenas).tags(tags.and(NettyMeters.AllocatorMemoryKeyNames.MEMORY_TYPE.asString(), "direct")).register(registry);
            Gauge.builder(NettyMeters.ALLOCATOR_POOLED_CACHE_SIZE.getName(), pooledAllocatorMetric, PooledByteBufAllocatorMetric::normalCacheSize).tags(tags.and(NettyMeters.AllocatorPooledCacheKeyNames.CACHE_TYPE.asString(), "normal")).register(registry);
            Gauge.builder(NettyMeters.ALLOCATOR_POOLED_CACHE_SIZE.getName(), pooledAllocatorMetric, PooledByteBufAllocatorMetric::smallCacheSize).tags(tags.and(NettyMeters.AllocatorPooledCacheKeyNames.CACHE_TYPE.asString(), "small")).register(registry);
            Gauge.builder(NettyMeters.ALLOCATOR_POOLED_THREADLOCAL_CACHES.getName(), pooledAllocatorMetric, PooledByteBufAllocatorMetric::numThreadLocalCaches).tags(tags).register(registry);
            Gauge.builder(NettyMeters.ALLOCATOR_POOLED_CHUNK_SIZE.getName(), pooledAllocatorMetric, PooledByteBufAllocatorMetric::chunkSize).tags(tags).register(registry);
        }
    }
}

