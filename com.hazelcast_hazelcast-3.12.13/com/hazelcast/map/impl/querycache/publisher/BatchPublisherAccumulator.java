/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.publisher;

import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.accumulator.BasicAccumulator;
import com.hazelcast.map.impl.querycache.event.sequence.Sequenced;
import java.util.concurrent.TimeUnit;

class BatchPublisherAccumulator
extends BasicAccumulator<Sequenced> {
    BatchPublisherAccumulator(QueryCacheContext context, AccumulatorInfo info) {
        super(context, info);
    }

    @Override
    public void accumulate(Sequenced sequenced) {
        super.accumulate(sequenced);
        AccumulatorInfo info = this.getInfo();
        if (!info.isPublishable()) {
            return;
        }
        this.poll(this.handler, info.getBatchSize());
        this.poll(this.handler, info.getDelaySeconds(), TimeUnit.SECONDS);
    }
}

