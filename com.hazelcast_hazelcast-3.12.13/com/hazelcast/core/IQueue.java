/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.BaseQueue;
import com.hazelcast.core.ICollection;
import com.hazelcast.monitor.LocalQueueStats;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public interface IQueue<E>
extends BlockingQueue<E>,
BaseQueue<E>,
ICollection<E> {
    @Override
    public E poll();

    @Override
    public E poll(long var1, TimeUnit var3) throws InterruptedException;

    @Override
    public E take() throws InterruptedException;

    public LocalQueueStats getLocalQueueStats();
}

