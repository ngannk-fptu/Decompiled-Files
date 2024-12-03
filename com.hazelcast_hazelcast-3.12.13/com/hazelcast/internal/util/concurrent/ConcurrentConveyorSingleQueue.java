/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.concurrent;

import com.hazelcast.internal.util.concurrent.ConcurrentConveyor;
import com.hazelcast.internal.util.concurrent.ConcurrentConveyorException;
import com.hazelcast.internal.util.concurrent.QueuedPipe;

public final class ConcurrentConveyorSingleQueue<E>
extends ConcurrentConveyor<E> {
    private final QueuedPipe<E> queue;

    private ConcurrentConveyorSingleQueue(E submitterGoneItem, QueuedPipe<E> queue) {
        super(submitterGoneItem, queue);
        this.queue = queue;
    }

    public static <E1> ConcurrentConveyorSingleQueue<E1> concurrentConveyorSingleQueue(E1 submitterGoneItem, QueuedPipe<E1> queue) {
        return new ConcurrentConveyorSingleQueue<E1>(submitterGoneItem, queue);
    }

    public boolean offer(E item) throws ConcurrentConveyorException {
        return this.offer(this.queue, item);
    }

    public void submit(E item) throws ConcurrentConveyorException {
        this.submit(this.queue, item);
    }
}

