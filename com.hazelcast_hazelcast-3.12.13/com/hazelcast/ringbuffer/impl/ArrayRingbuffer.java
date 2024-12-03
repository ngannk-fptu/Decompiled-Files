/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.ringbuffer.impl;

import com.hazelcast.ringbuffer.StaleSequenceException;
import com.hazelcast.ringbuffer.impl.ReadOnlyRingbufferIterator;
import com.hazelcast.ringbuffer.impl.Ringbuffer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.Iterator;

public class ArrayRingbuffer<E>
implements Ringbuffer<E> {
    private E[] ringItems;
    private long tailSequence = -1L;
    private long headSequence = this.tailSequence + 1L;
    private int capacity;

    public ArrayRingbuffer(int capacity) {
        this.capacity = capacity;
        this.ringItems = new Object[capacity];
    }

    @Override
    public long tailSequence() {
        return this.tailSequence;
    }

    @Override
    public long peekNextTailSequence() {
        return this.tailSequence + 1L;
    }

    @Override
    public void setTailSequence(long sequence) {
        this.tailSequence = sequence;
    }

    @Override
    public long headSequence() {
        return this.headSequence;
    }

    @Override
    public void setHeadSequence(long sequence) {
        this.headSequence = sequence;
    }

    @Override
    public long getCapacity() {
        return this.capacity;
    }

    @Override
    public long size() {
        return this.tailSequence - this.headSequence + 1L;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0L;
    }

    @Override
    public long add(E item) {
        ++this.tailSequence;
        if (this.tailSequence - (long)this.capacity == this.headSequence) {
            ++this.headSequence;
        }
        int index = this.toIndex(this.tailSequence);
        this.ringItems[index] = item;
        return this.tailSequence;
    }

    @Override
    public E read(long sequence) {
        this.checkReadSequence(sequence);
        return this.ringItems[this.toIndex(sequence)];
    }

    @Override
    public void checkBlockableReadSequence(long readSequence) {
        if (readSequence > this.tailSequence + 1L) {
            throw new IllegalArgumentException("sequence:" + readSequence + " is too large. The current tailSequence is:" + this.tailSequence);
        }
        if (readSequence < this.headSequence) {
            throw new StaleSequenceException("sequence:" + readSequence + " is too small. The current headSequence is:" + this.headSequence + " tailSequence is:" + this.tailSequence, this.headSequence);
        }
    }

    @Override
    public void checkReadSequence(long sequence) {
        if (sequence > this.tailSequence) {
            throw new IllegalArgumentException("sequence:" + sequence + " is too large. The current tailSequence is:" + this.tailSequence);
        }
        if (sequence < this.headSequence) {
            throw new StaleSequenceException("sequence:" + sequence + " is too small. The current headSequence is:" + this.headSequence + " tailSequence is:" + this.tailSequence, this.headSequence);
        }
    }

    private int toIndex(long sequence) {
        return (int)(sequence % (long)this.ringItems.length);
    }

    @Override
    public void set(long seq, E data) {
        this.ringItems[this.toIndex((long)seq)] = data;
    }

    @Override
    public void clear() {
        Arrays.fill(this.ringItems, null);
        this.tailSequence = -1L;
        this.headSequence = this.tailSequence + 1L;
    }

    @Override
    public Iterator<E> iterator() {
        return new ReadOnlyRingbufferIterator(this);
    }

    @Override
    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public E[] getItems() {
        return this.ringItems;
    }
}

