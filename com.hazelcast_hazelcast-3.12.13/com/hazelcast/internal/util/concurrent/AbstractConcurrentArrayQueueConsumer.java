/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.concurrent;

import com.hazelcast.internal.util.concurrent.AbstractConcurrentArrayQueuePadding2;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

class AbstractConcurrentArrayQueueConsumer
extends AbstractConcurrentArrayQueuePadding2 {
    protected static final AtomicLongFieldUpdater<AbstractConcurrentArrayQueueConsumer> HEAD = AtomicLongFieldUpdater.newUpdater(AbstractConcurrentArrayQueueConsumer.class, "head");
    protected volatile long head;

    AbstractConcurrentArrayQueueConsumer() {
    }
}

