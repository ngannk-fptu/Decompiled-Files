/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind;

import com.hazelcast.map.impl.mapstore.writebehind.BoundedWriteBehindQueue;
import com.hazelcast.map.impl.mapstore.writebehind.CoalescedWriteBehindQueue;
import com.hazelcast.map.impl.mapstore.writebehind.CyclicWriteBehindQueue;
import com.hazelcast.map.impl.mapstore.writebehind.SynchronizedWriteBehindQueue;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindQueue;
import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntry;
import java.util.concurrent.atomic.AtomicInteger;

public final class WriteBehindQueues {
    private WriteBehindQueues() {
    }

    public static WriteBehindQueue<DelayedEntry> createBoundedWriteBehindQueue(int maxCapacity, AtomicInteger counter) {
        WriteBehindQueue<DelayedEntry> queue = WriteBehindQueues.createCyclicWriteBehindQueue();
        WriteBehindQueue<DelayedEntry> boundedQueue = WriteBehindQueues.createBoundedWriteBehindQueue(maxCapacity, counter, queue);
        return WriteBehindQueues.createSynchronizedWriteBehindQueue(boundedQueue);
    }

    public static WriteBehindQueue<DelayedEntry> createDefaultWriteBehindQueue() {
        WriteBehindQueue<DelayedEntry> queue = WriteBehindQueues.createCoalescedWriteBehindQueue();
        return WriteBehindQueues.createSynchronizedWriteBehindQueue(queue);
    }

    private static <T> WriteBehindQueue<T> createSynchronizedWriteBehindQueue(WriteBehindQueue<T> queue) {
        return new SynchronizedWriteBehindQueue<T>(queue);
    }

    private static WriteBehindQueue<DelayedEntry> createCoalescedWriteBehindQueue() {
        return new CoalescedWriteBehindQueue();
    }

    private static WriteBehindQueue<DelayedEntry> createCyclicWriteBehindQueue() {
        return new CyclicWriteBehindQueue();
    }

    private static <T> WriteBehindQueue<T> createBoundedWriteBehindQueue(int maxCapacity, AtomicInteger counter, WriteBehindQueue<T> queue) {
        return new BoundedWriteBehindQueue<T>(maxCapacity, counter, queue);
    }
}

