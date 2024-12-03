/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorHandler;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.accumulator.BasicAccumulator;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.map.impl.querycache.event.sequence.DefaultSubscriberSequencerProvider;
import com.hazelcast.map.impl.querycache.event.sequence.Sequenced;
import com.hazelcast.map.impl.querycache.event.sequence.SubscriberSequencerProvider;
import com.hazelcast.map.impl.querycache.subscriber.EventPublisherHelper;
import com.hazelcast.map.impl.querycache.subscriber.InternalQueryCache;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheFactory;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberAccumulatorHandler;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContext;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SubscriberAccumulator
extends BasicAccumulator<QueryCacheEventData> {
    private final SubscriberSequencerProvider sequenceProvider;
    private final ConcurrentMap<Integer, Long> brokenSequences = new ConcurrentHashMap<Integer, Long>();

    protected SubscriberAccumulator(QueryCacheContext context, AccumulatorInfo info) {
        super(context, info);
        this.sequenceProvider = new DefaultSubscriberSequencerProvider();
    }

    @Override
    public void reset() {
        this.brokenSequences.clear();
        this.sequenceProvider.resetAll();
        super.reset();
    }

    ConcurrentMap<Integer, Long> getBrokenSequences() {
        return this.brokenSequences;
    }

    @Override
    public void accumulate(QueryCacheEventData event) {
        if (this.isApplicable(event)) {
            this.addQueryCache(event);
        }
    }

    private boolean isApplicable(QueryCacheEventData event) {
        if (!this.getInfo().isPublishable()) {
            return false;
        }
        int partitionId = event.getPartitionId();
        if (this.isEndEvent(event)) {
            this.sequenceProvider.reset(partitionId);
            this.removeFromBrokenSequences(event);
            return false;
        }
        if (this.isNextEvent(event)) {
            long currentSequence = this.sequenceProvider.getSequence(partitionId);
            this.sequenceProvider.compareAndSetSequence(currentSequence, event.getSequence(), partitionId);
            this.removeFromBrokenSequences(event);
            return true;
        }
        this.handleUnexpectedEvent(event);
        return false;
    }

    private void handleUnexpectedEvent(QueryCacheEventData event) {
        int partitionId = event.getPartitionId();
        long sequence = event.getSequence();
        Long prev = this.brokenSequences.putIfAbsent(partitionId, sequence);
        if (prev != null) {
            return;
        }
        InternalQueryCache queryCache = this.getQueryCache();
        if (queryCache != null) {
            if (this.logger.isWarningEnabled()) {
                long currentSequence = this.sequenceProvider.getSequence(partitionId);
                this.logger.warning(String.format("Event lost detected for queryCache=`%s`: partitionId=%d, expectedSequence=%d, foundSequence=%d, cacheSize=%d", queryCache.getCacheId(), partitionId, currentSequence + 1L, sequence, queryCache.size()));
            }
            EventPublisherHelper.publishEventLost(this.context, this.info.getMapName(), this.info.getCacheId(), event.getPartitionId(), queryCache.getExtractors());
        }
    }

    private void removeFromBrokenSequences(QueryCacheEventData event) {
        if (this.brokenSequences.isEmpty()) {
            return;
        }
        int partitionId = event.getPartitionId();
        long sequence = event.getSequence();
        if (sequence == -1L) {
            this.brokenSequences.remove(partitionId);
        } else {
            Long expected = (Long)this.brokenSequences.get(partitionId);
            if (expected != null && expected.longValue() == event.getSequence()) {
                this.brokenSequences.remove(partitionId);
            }
        }
        if (this.logger.isFinestEnabled()) {
            this.logger.finest(String.format("Size of broken sequences=%d", this.brokenSequences.size()));
        }
    }

    protected boolean isNextEvent(Sequenced event) {
        long expectedSequence;
        int partitionId = event.getPartitionId();
        long currentSequence = this.sequenceProvider.getSequence(partitionId);
        long foundSequence = event.getSequence();
        return foundSequence == (expectedSequence = currentSequence + 1L);
    }

    private InternalQueryCache getQueryCache() {
        AccumulatorInfo info = this.getInfo();
        String cacheId = info.getCacheId();
        SubscriberContext subscriberContext = this.context.getSubscriberContext();
        QueryCacheFactory queryCacheFactory = subscriberContext.getQueryCacheFactory();
        return queryCacheFactory.getOrNull(cacheId);
    }

    @Override
    protected AccumulatorHandler<QueryCacheEventData> createAccumulatorHandler(QueryCacheContext context, AccumulatorInfo info) {
        boolean includeValue = info.isIncludeValue();
        InternalQueryCache queryCache = this.getQueryCache();
        InternalSerializationService serializationService = context.getSerializationService();
        return new SubscriberAccumulatorHandler(includeValue, queryCache, serializationService);
    }

    private void addQueryCache(QueryCacheEventData eventData) {
        this.handler.handle(eventData, false);
    }

    private boolean isEndEvent(QueryCacheEventData event) {
        return event.getSequence() == -1L;
    }
}

