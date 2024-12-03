/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.publisher;

import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.accumulator.BasicAccumulator;
import com.hazelcast.map.impl.querycache.event.sequence.Sequenced;
import java.util.concurrent.TimeUnit;

class NonStopPublisherAccumulator
extends BasicAccumulator<Sequenced> {
    NonStopPublisherAccumulator(QueryCacheContext context, AccumulatorInfo info) {
        super(context, info);
    }

    @Override
    public void accumulate(Sequenced eventData) {
        super.accumulate(eventData);
        AccumulatorInfo info = this.getInfo();
        if (!info.isPublishable()) {
            return;
        }
        this.poll(this.handler, 0L, TimeUnit.SECONDS);
    }
}

