/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.publisher;

import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.Accumulator;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorFactory;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.publisher.BatchPublisherAccumulator;
import com.hazelcast.map.impl.querycache.publisher.CoalescingPublisherAccumulator;
import com.hazelcast.map.impl.querycache.publisher.NonStopPublisherAccumulator;

public class PublisherAccumulatorFactory
implements AccumulatorFactory {
    private final QueryCacheContext context;

    public PublisherAccumulatorFactory(QueryCacheContext context) {
        this.context = context;
    }

    @Override
    public Accumulator createAccumulator(AccumulatorInfo info) {
        long delayTime = info.getDelaySeconds();
        if (delayTime <= 0L) {
            return new NonStopPublisherAccumulator(this.context, info);
        }
        if (info.isCoalesce()) {
            return new CoalescingPublisherAccumulator(this.context, info);
        }
        return new BatchPublisherAccumulator(this.context, info);
    }
}

