/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.concurrent;

import com.hazelcast.internal.util.concurrent.AbstractConcurrentArrayQueue;
import com.hazelcast.util.function.Predicate;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ManyToOneConcurrentArrayQueue<E>
extends AbstractConcurrentArrayQueue<E> {
    public ManyToOneConcurrentArrayQueue(int requestedCapacity) {
        super(requestedCapacity);
    }

    @Override
    public boolean offer(E e) {
        long acquiredTail;
        assert (e != null) : "attempt to offer a null element";
        int capacity = this.capacity;
        long acquiredHead = this.sharedHeadCache;
        long bufferLimit = acquiredHead + (long)capacity;
        do {
            if ((acquiredTail = this.tail) < bufferLimit) continue;
            acquiredHead = this.head;
            bufferLimit = acquiredHead + (long)capacity;
            if (acquiredTail >= bufferLimit) {
                return false;
            }
            SHARED_HEAD_CACHE.lazySet(this, acquiredHead);
        } while (!TAIL.compareAndSet(this, acquiredTail, acquiredTail + 1L));
        this.buffer.lazySet(ManyToOneConcurrentArrayQueue.seqToArrayIndex(acquiredTail, capacity - 1), e);
        return true;
    }

    @Override
    public E poll() {
        AtomicReferenceArray buffer = this.buffer;
        long head = this.head;
        int arrayIndex = ManyToOneConcurrentArrayQueue.seqToArrayIndex(head, this.capacity - 1);
        Object item = buffer.get(arrayIndex);
        if (item != null) {
            buffer.lazySet(arrayIndex, null);
            HEAD.lazySet(this, head + 1L);
        }
        return item;
    }

    @Override
    public int drain(Predicate<? super E> itemHandler) {
        int arrayIndex;
        Object item;
        AtomicReferenceArray buffer = this.buffer;
        long mask = this.capacity - 1;
        long acquiredHead = this.head;
        long limit = acquiredHead + mask + 1L;
        long nextSequence = acquiredHead;
        while (nextSequence < limit && (item = buffer.get(arrayIndex = ManyToOneConcurrentArrayQueue.seqToArrayIndex(nextSequence, mask))) != null) {
            buffer.lazySet(arrayIndex, null);
            HEAD.lazySet(this, ++nextSequence);
            if (itemHandler.test(item)) continue;
            break;
        }
        return (int)(nextSequence - acquiredHead);
    }

    @Override
    public int drainTo(Collection<? super E> target, int limit) {
        int arrayIndex;
        Object item;
        int count;
        AtomicReferenceArray buffer = this.buffer;
        long mask = this.capacity - 1;
        long nextSequence = this.head;
        for (count = 0; count < limit && (item = buffer.get(arrayIndex = ManyToOneConcurrentArrayQueue.seqToArrayIndex(nextSequence, mask))) != null; ++count) {
            buffer.lazySet(arrayIndex, null);
            HEAD.lazySet(this, ++nextSequence);
            target.add(item);
        }
        return count;
    }
}

