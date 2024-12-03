/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.accumulator;

import com.hazelcast.map.impl.querycache.accumulator.CyclicBuffer;
import com.hazelcast.map.impl.querycache.event.sequence.Sequenced;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.QuickMath;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultCyclicBuffer<E extends Sequenced>
implements CyclicBuffer<E> {
    private static final long UNAVAILABLE = -1L;
    private int capacity;
    private E[] buffer;
    private AtomicLong headSequence;
    private AtomicLong tailSequence;

    public DefaultCyclicBuffer(int capacity) throws IllegalArgumentException {
        Preconditions.checkPositive(capacity, "capacity");
        this.init(capacity);
    }

    private void init(int maxSize) {
        this.capacity = QuickMath.nextPowerOfTwo(maxSize);
        this.buffer = new Sequenced[this.capacity];
        this.tailSequence = new AtomicLong(-1L);
        this.headSequence = new AtomicLong(-1L);
    }

    @Override
    public void add(E event) {
        int headIndex;
        Preconditions.checkNotNull(event, "event cannot be null");
        long sequence = event.getSequence();
        int tailIndex = this.findIndex(sequence);
        this.buffer[tailIndex] = event;
        this.tailSequence.set(sequence);
        long head = this.headSequence.get();
        if (head == -1L) {
            this.headSequence.set(sequence);
        } else if (head != sequence && (headIndex = this.findIndex(head)) == tailIndex) {
            E e;
            if (++headIndex == this.capacity) {
                headIndex = 0;
            }
            if ((e = this.buffer[headIndex]) != null) {
                this.headSequence.set(e.getSequence());
            } else {
                this.headSequence.incrementAndGet();
            }
        }
    }

    @Override
    public E get(long sequence) {
        Preconditions.checkPositive(sequence, "sequence");
        int index = this.findIndex(sequence);
        E e = this.buffer[index];
        if (e != null && e.getSequence() != sequence) {
            return null;
        }
        return e;
    }

    @Override
    public boolean setHead(long sequence) {
        Preconditions.checkPositive(sequence, "sequence");
        E e = this.get(sequence);
        if (e == null) {
            return false;
        }
        this.headSequence.set(sequence);
        return true;
    }

    @Override
    public E getAndAdvance() {
        long head = this.headSequence.get();
        long tail = this.tailSequence.get();
        if (tail == -1L || head > tail) {
            return null;
        }
        int headIndex = this.findIndex(head);
        E e = this.buffer[headIndex];
        if (e == null) {
            return null;
        }
        this.headSequence.incrementAndGet();
        return e;
    }

    @Override
    public void reset() {
        this.init(this.capacity);
    }

    @Override
    public int size() {
        long head = this.headSequence.get();
        long tail = this.tailSequence.get();
        if (tail == -1L) {
            return 0;
        }
        int avail = (int)(tail - head + 1L);
        if (avail <= this.capacity) {
            return avail;
        }
        return this.capacity;
    }

    @Override
    public long getHeadSequence() {
        return this.headSequence.get();
    }

    private int findIndex(long sequence) {
        return (int)(sequence % (long)this.capacity);
    }
}

