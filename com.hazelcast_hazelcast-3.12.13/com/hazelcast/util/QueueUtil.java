/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.util.function.Predicate;
import java.util.Queue;

public final class QueueUtil {
    private QueueUtil() {
    }

    public static int drainQueue(Queue<?> queue) {
        return QueueUtil.drainQueue(queue, queue.size(), null);
    }

    public static <E> int drainQueue(Queue<E> queue, Predicate<E> drainedCountFilter) {
        return QueueUtil.drainQueue(queue, queue.size(), drainedCountFilter);
    }

    public static <E> int drainQueue(Queue<E> queue, int elementsToDrain, Predicate<E> drainedCountFilter) {
        int drained = 0;
        boolean drainMore = true;
        for (int i = 0; i < elementsToDrain && drainMore; ++i) {
            E polled = queue.poll();
            if (polled != null) {
                if (drainedCountFilter != null && !drainedCountFilter.test(polled)) continue;
                ++drained;
                continue;
            }
            drainMore = false;
        }
        return drained;
    }
}

