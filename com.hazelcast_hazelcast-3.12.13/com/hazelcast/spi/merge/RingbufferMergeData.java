/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.spi.merge;

import com.hazelcast.ringbuffer.StaleSequenceException;
import com.hazelcast.ringbuffer.impl.Ringbuffer;
import com.hazelcast.spi.merge.RingbufferMergeDataReadOnlyIterator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.Iterator;

public class RingbufferMergeData
implements Iterable<Object> {
    private Object[] items;
    private long tailSequence = -1L;
    private long headSequence = this.tailSequence + 1L;

    public RingbufferMergeData(int capacity) {
        this.items = new Object[capacity];
    }

    public RingbufferMergeData(Ringbuffer<Object> ringbuffer) {
        this.items = ringbuffer.getItems();
        this.headSequence = ringbuffer.headSequence();
        this.tailSequence = ringbuffer.tailSequence();
    }

    public long getTailSequence() {
        return this.tailSequence;
    }

    public void setTailSequence(long sequence) {
        this.tailSequence = sequence;
    }

    public long getHeadSequence() {
        return this.headSequence;
    }

    public void setHeadSequence(long sequence) {
        this.headSequence = sequence;
    }

    public int getCapacity() {
        return this.items.length;
    }

    public int size() {
        return (int)(this.tailSequence - this.headSequence + 1L);
    }

    public long add(Object item) {
        ++this.tailSequence;
        if (this.tailSequence - (long)this.items.length == this.headSequence) {
            ++this.headSequence;
        }
        int index = this.toIndex(this.tailSequence);
        this.items[index] = item;
        return this.tailSequence;
    }

    public <E> E read(long sequence) {
        this.checkReadSequence(sequence);
        return (E)this.items[this.toIndex(sequence)];
    }

    public void set(long seq, Object data) {
        this.items[this.toIndex((long)seq)] = data;
    }

    public void clear() {
        Arrays.fill(this.items, null);
        this.tailSequence = -1L;
        this.headSequence = this.tailSequence + 1L;
    }

    private void checkReadSequence(long sequence) {
        if (sequence > this.tailSequence) {
            throw new IllegalArgumentException("sequence:" + sequence + " is too large. The current tailSequence is:" + this.tailSequence);
        }
        if (sequence < this.headSequence) {
            throw new StaleSequenceException("sequence:" + sequence + " is too small. The current headSequence is:" + this.headSequence + " tailSequence is:" + this.tailSequence, this.headSequence);
        }
    }

    private int toIndex(long sequence) {
        return (int)(sequence % (long)this.items.length);
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public Object[] getItems() {
        return this.items;
    }

    @Override
    public Iterator<Object> iterator() {
        return new RingbufferMergeDataReadOnlyIterator<Object>(this);
    }
}

