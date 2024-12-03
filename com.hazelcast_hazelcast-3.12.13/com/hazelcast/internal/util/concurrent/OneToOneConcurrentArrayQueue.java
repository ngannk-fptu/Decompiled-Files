/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.concurrent;

import com.hazelcast.internal.util.concurrent.AbstractConcurrentArrayQueue;
import com.hazelcast.util.function.Predicate;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class OneToOneConcurrentArrayQueue<E>
extends AbstractConcurrentArrayQueue<E> {
    public OneToOneConcurrentArrayQueue(int requestedCapacity) {
        super(requestedCapacity);
    }

    @Override
    public boolean offer(E e) {
        assert (e != null) : "Attempted to offer null to a concurrent array queue";
        long currentTail = this.tail;
        long acquiredHead = this.headCache;
        int capacity = this.capacity;
        long bufferLimit = acquiredHead + (long)capacity;
        if (currentTail >= bufferLimit) {
            acquiredHead = this.head;
            bufferLimit = acquiredHead + (long)capacity;
            if (currentTail >= bufferLimit) {
                return false;
            }
            this.headCache = acquiredHead;
        }
        int arrayIndex = OneToOneConcurrentArrayQueue.seqToArrayIndex(currentTail, capacity - 1);
        this.buffer.lazySet(arrayIndex, e);
        TAIL.lazySet(this, currentTail + 1L);
        return true;
    }

    @Override
    public E poll() {
        AtomicReferenceArray buffer = this.buffer;
        long currentHead = this.head;
        int arrayIndex = OneToOneConcurrentArrayQueue.seqToArrayIndex(currentHead, this.capacity - 1);
        Object e = buffer.get(arrayIndex);
        if (e != null) {
            buffer.lazySet(arrayIndex, null);
            HEAD.lazySet(this, currentHead + 1L);
        }
        return e;
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
        while (nextSequence < limit && (item = buffer.get(arrayIndex = OneToOneConcurrentArrayQueue.seqToArrayIndex(nextSequence, mask))) != null) {
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
        if (limit <= 0) {
            return 0;
        }
        AtomicReferenceArray buffer = this.buffer;
        long mask = this.capacity - 1;
        long nextSequence = this.head;
        for (count = 0; count < limit && (item = buffer.get(arrayIndex = OneToOneConcurrentArrayQueue.seqToArrayIndex(nextSequence, mask))) != null; ++count) {
            buffer.lazySet(arrayIndex, null);
            HEAD.lazySet(this, ++nextSequence);
            target.add(item);
        }
        return count;
    }
}

