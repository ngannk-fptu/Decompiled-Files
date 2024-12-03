/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.concurrent;

import com.hazelcast.internal.util.concurrent.AbstractConcurrentArrayQueuePadding1;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

class AbstractConcurrentArrayQueueProducer
extends AbstractConcurrentArrayQueuePadding1 {
    protected static final AtomicLongFieldUpdater<AbstractConcurrentArrayQueueProducer> TAIL = AtomicLongFieldUpdater.newUpdater(AbstractConcurrentArrayQueueProducer.class, "tail");
    protected static final AtomicLongFieldUpdater<AbstractConcurrentArrayQueueProducer> SHARED_HEAD_CACHE = AtomicLongFieldUpdater.newUpdater(AbstractConcurrentArrayQueueProducer.class, "sharedHeadCache");
    protected volatile long tail;
    protected long headCache;
    protected volatile long sharedHeadCache;

    AbstractConcurrentArrayQueueProducer() {
    }
}

