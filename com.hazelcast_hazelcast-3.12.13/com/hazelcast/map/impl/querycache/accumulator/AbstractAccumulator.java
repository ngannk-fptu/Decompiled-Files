/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.accumulator;

import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.Accumulator;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.accumulator.CyclicBuffer;
import com.hazelcast.map.impl.querycache.accumulator.DefaultCyclicBuffer;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.map.impl.querycache.event.sequence.DefaultPartitionSequencer;
import com.hazelcast.map.impl.querycache.event.sequence.PartitionSequencer;
import com.hazelcast.map.impl.querycache.event.sequence.Sequenced;
import com.hazelcast.util.Clock;

abstract class AbstractAccumulator<E extends Sequenced>
implements Accumulator<E> {
    protected final AccumulatorInfo info;
    protected final QueryCacheContext context;
    protected final CyclicBuffer<E> buffer;
    protected final PartitionSequencer partitionSequencer;

    public AbstractAccumulator(QueryCacheContext context, AccumulatorInfo info) {
        this.context = context;
        this.info = info;
        this.partitionSequencer = new DefaultPartitionSequencer();
        this.buffer = new DefaultCyclicBuffer(info.getBufferSize());
    }

    public CyclicBuffer<E> getBuffer() {
        return this.buffer;
    }

    protected QueryCacheContext getContext() {
        return this.context;
    }

    protected long getNow() {
        return Clock.currentTimeMillis();
    }

    protected boolean isExpired(QueryCacheEventData entry, long delayMillis, long now) {
        return entry != null && now - entry.getCreationTime() >= delayMillis;
    }

    @Override
    public void reset() {
        this.buffer.reset();
        this.partitionSequencer.reset();
    }
}

