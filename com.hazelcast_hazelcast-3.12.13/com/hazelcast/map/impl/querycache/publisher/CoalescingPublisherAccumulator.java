/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.publisher;

import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.QueryCacheEventService;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorProcessor;
import com.hazelcast.map.impl.querycache.accumulator.BasicAccumulator;
import com.hazelcast.map.impl.querycache.event.BatchEventData;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.map.impl.querycache.event.sequence.Sequenced;
import com.hazelcast.map.impl.querycache.publisher.EventPublisherAccumulatorProcessor;
import com.hazelcast.nio.serialization.Data;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class CoalescingPublisherAccumulator
extends BasicAccumulator<QueryCacheEventData> {
    private final Map<Data, Long> index = new HashMap<Data, Long>();

    CoalescingPublisherAccumulator(QueryCacheContext context, AccumulatorInfo info) {
        super(context, info);
    }

    @Override
    public void accumulate(QueryCacheEventData eventData) {
        this.setSequence(eventData);
        this.getBuffer().add(eventData);
        AccumulatorInfo info = this.getInfo();
        if (!info.isPublishable()) {
            return;
        }
        this.poll(this.handler, info.getBatchSize());
        this.poll(this.handler, info.getDelaySeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void reset() {
        this.index.clear();
        super.reset();
    }

    private void setSequence(QueryCacheEventData eventData) {
        Data dataKey = eventData.getDataKey();
        Long sequence = this.index.get(dataKey);
        if (sequence != null) {
            eventData.setSequence(sequence);
        } else {
            long nextSequence = this.partitionSequencer.nextSequence();
            eventData.setSequence(nextSequence);
            this.index.put(dataKey, nextSequence);
        }
        if (this.logger.isFinestEnabled()) {
            this.logger.finest(String.format("Added to index key=%s, sequence=%d, indexSize=%d", eventData.getKey(), eventData.getSequence(), this.index.size()));
        }
    }

    @Override
    protected AccumulatorProcessor<Sequenced> createAccumulatorProcessor(AccumulatorInfo info, QueryCacheEventService eventService) {
        return new CoalescedEventAccumulatorProcessor(info, eventService);
    }

    private class CoalescedEventAccumulatorProcessor
    extends EventPublisherAccumulatorProcessor {
        CoalescedEventAccumulatorProcessor(AccumulatorInfo info, QueryCacheEventService eventService) {
            super(info, eventService);
        }

        @Override
        public void process(Sequenced sequenced) {
            super.process(sequenced);
            this.clearIndexes(sequenced);
        }

        private void clearIndexes(Sequenced sequenced) {
            if (sequenced instanceof BatchEventData) {
                Collection<QueryCacheEventData> events = ((BatchEventData)sequenced).getEvents();
                for (QueryCacheEventData event : events) {
                    this.removeFromIndex(event);
                }
                return;
            }
            if (sequenced instanceof QueryCacheEventData) {
                this.removeFromIndex((QueryCacheEventData)sequenced);
                return;
            }
            throw new IllegalArgumentException(String.format("Expected an instance of %s but found %s", QueryCacheEventData.class.getSimpleName(), sequenced.getClass().getSimpleName()));
        }

        private void removeFromIndex(QueryCacheEventData eventData) {
            Data dataKey = eventData.getDataKey();
            CoalescingPublisherAccumulator.this.index.remove(dataKey);
            if (CoalescingPublisherAccumulator.this.logger.isFinestEnabled()) {
                CoalescingPublisherAccumulator.this.logger.finest(String.format("Removed from index key=%s, sequence=%d, indexSize=%d", eventData.getKey(), eventData.getSequence(), CoalescingPublisherAccumulator.this.index.size()));
            }
        }
    }
}

