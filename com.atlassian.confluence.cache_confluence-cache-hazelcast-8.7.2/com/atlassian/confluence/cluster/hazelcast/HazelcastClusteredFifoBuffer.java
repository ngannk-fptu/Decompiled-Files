/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.FifoBuffer
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  com.hazelcast.core.IQueue
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.core.task.FifoBuffer;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hazelcast.core.IQueue;
import java.util.Collection;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(since="8.2", forRemoval=true)
public class HazelcastClusteredFifoBuffer<T>
implements FifoBuffer<T> {
    private static final Logger log = LoggerFactory.getLogger(HazelcastClusteredFifoBuffer.class);
    private final IQueue<T> queue;

    public HazelcastClusteredFifoBuffer(IQueue<T> queue) {
        this.queue = (IQueue)Preconditions.checkNotNull(queue);
        log.trace("Created for {}", (Object)queue.getName());
    }

    public T remove() {
        log.trace("remove() called for {}", (Object)this.queue.getName());
        return (T)(0 < this.queue.size() ? this.queue.remove() : null);
    }

    public void add(T o) {
        log.trace("add() called for {}", (Object)this.queue.getName());
        if (null != o) {
            this.queue.add(o);
        }
    }

    public int size() {
        log.trace("size() called for {}", (Object)this.queue.getName());
        return this.queue.size();
    }

    public Collection<T> getItems() {
        log.trace("getItems() called for {}", (Object)this.queue.getName());
        return Lists.newArrayList((Iterator)this.queue.iterator());
    }

    public void clear() {
        log.trace("clear() called for {}", (Object)this.queue.getName());
        this.queue.clear();
    }
}

