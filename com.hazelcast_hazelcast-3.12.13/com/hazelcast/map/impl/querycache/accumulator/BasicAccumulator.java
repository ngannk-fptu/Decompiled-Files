/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.accumulator;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.QueryCacheEventService;
import com.hazelcast.map.impl.querycache.accumulator.AbstractAccumulator;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorHandler;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorProcessor;
import com.hazelcast.map.impl.querycache.accumulator.CyclicBuffer;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.map.impl.querycache.event.sequence.Sequenced;
import com.hazelcast.map.impl.querycache.publisher.EventPublisherAccumulatorProcessor;
import com.hazelcast.map.impl.querycache.publisher.PublisherAccumulatorHandler;
import com.hazelcast.util.Preconditions;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class BasicAccumulator<E extends Sequenced>
extends AbstractAccumulator<E> {
    protected final AccumulatorHandler<E> handler;
    protected final ILogger logger = Logger.getLogger(this.getClass());

    protected BasicAccumulator(QueryCacheContext context, AccumulatorInfo info) {
        super(context, info);
        this.handler = this.createAccumulatorHandler(context, info);
    }

    @Override
    public void accumulate(E event) {
        long sequence = this.partitionSequencer.nextSequence();
        event.setSequence(sequence);
        this.getBuffer().add(event);
    }

    @Override
    public int poll(AccumulatorHandler<E> handler, int maxItems) {
        Object current;
        if (maxItems < 1) {
            return 0;
        }
        CyclicBuffer buffer = this.getBuffer();
        int size = this.size();
        if (size < 1 || size < maxItems) {
            return 0;
        }
        int count = 0;
        while ((current = buffer.getAndAdvance()) != null) {
            handler.handle(current, ++count == maxItems);
            if (count < maxItems) continue;
        }
        return count;
    }

    @Override
    public int poll(AccumulatorHandler<E> handler, long delay, TimeUnit unit) {
        E current;
        CyclicBuffer buffer = this.getBuffer();
        if (this.size() < 1) {
            return 0;
        }
        long now = this.getNow();
        int count = 0;
        while ((current = this.readCurrentExpiredOrNull(now, delay, unit)) != null) {
            E next = this.readNextExpiredOrNull(now, delay, unit);
            handler.handle(current, next == null);
            ++count;
            buffer.getAndAdvance();
            if (next != null) continue;
        }
        return count;
    }

    @Override
    public Iterator<E> iterator() {
        CyclicBuffer buffer = this.getBuffer();
        return new ReadOnlyIterator(buffer);
    }

    @Override
    public int size() {
        return this.buffer.size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public AccumulatorInfo getInfo() {
        return this.info;
    }

    @Override
    public boolean setHead(long sequence) {
        return this.buffer.setHead(sequence);
    }

    @Override
    public void reset() {
        this.handler.reset();
        super.reset();
    }

    private E readNextExpiredOrNull(long now, long delay, TimeUnit unit) {
        Object sequenced;
        long headSequence = this.buffer.getHeadSequence();
        if ((sequenced = this.buffer.get(++headSequence)) == null) {
            return null;
        }
        return (E)(this.isExpired((QueryCacheEventData)sequenced, unit.toMillis(delay), now) ? sequenced : null);
    }

    private E readCurrentExpiredOrNull(long now, long delay, TimeUnit unit) {
        long headSequence = this.buffer.getHeadSequence();
        Object sequenced = this.buffer.get(headSequence);
        if (sequenced == null) {
            return null;
        }
        return (E)(this.isExpired((QueryCacheEventData)sequenced, unit.toMillis(delay), now) ? sequenced : null);
    }

    protected AccumulatorHandler<E> createAccumulatorHandler(QueryCacheContext context, AccumulatorInfo info) {
        QueryCacheEventService queryCacheEventService = context.getQueryCacheEventService();
        AccumulatorProcessor<Sequenced> processor = this.createAccumulatorProcessor(info, queryCacheEventService);
        return new PublisherAccumulatorHandler(context, processor);
    }

    protected AccumulatorProcessor<Sequenced> createAccumulatorProcessor(AccumulatorInfo info, QueryCacheEventService eventService) {
        return new EventPublisherAccumulatorProcessor(info, eventService);
    }

    static class ReadOnlyIterator<T extends Sequenced>
    implements Iterator<T> {
        private final CyclicBuffer<T> buffer;

        ReadOnlyIterator(CyclicBuffer<T> buffer) {
            this.buffer = Preconditions.checkNotNull(buffer, "buffer cannot be null");
        }

        @Override
        public boolean hasNext() {
            return this.buffer.size() > 0;
        }

        @Override
        public T next() {
            return this.buffer.getAndAdvance();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Only read only iteration is allowed");
        }
    }
}

