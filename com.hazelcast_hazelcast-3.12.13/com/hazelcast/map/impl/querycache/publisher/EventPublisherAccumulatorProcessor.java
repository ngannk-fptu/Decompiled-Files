/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.publisher;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.impl.querycache.ListenerRegistrationHelper;
import com.hazelcast.map.impl.querycache.QueryCacheEventService;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorProcessor;
import com.hazelcast.map.impl.querycache.event.sequence.Sequenced;

public class EventPublisherAccumulatorProcessor
implements AccumulatorProcessor<Sequenced> {
    private AccumulatorInfo info;
    private final QueryCacheEventService eventService;
    private final ILogger logger = Logger.getLogger(this.getClass());

    public EventPublisherAccumulatorProcessor(QueryCacheEventService eventService) {
        this(null, eventService);
    }

    public EventPublisherAccumulatorProcessor(AccumulatorInfo info, QueryCacheEventService eventService) {
        this.info = info;
        this.eventService = eventService;
    }

    @Override
    public void process(Sequenced sequenced) {
        String listenerName = ListenerRegistrationHelper.generateListenerName(this.info.getMapName(), this.info.getCacheId());
        this.eventService.sendEventToSubscriber(listenerName, sequenced, sequenced.getPartitionId());
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Publisher sent events " + sequenced);
        }
    }

    public void setInfo(AccumulatorInfo info) {
        this.info = info;
    }
}

