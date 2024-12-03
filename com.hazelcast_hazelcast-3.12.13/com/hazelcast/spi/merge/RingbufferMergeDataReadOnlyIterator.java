/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.merge;

import com.hazelcast.spi.merge.RingbufferMergeData;
import java.util.Iterator;

public class RingbufferMergeDataReadOnlyIterator<E>
implements Iterator<E> {
    private final RingbufferMergeData ringbuffer;
    private long sequence;

    RingbufferMergeDataReadOnlyIterator(RingbufferMergeData ringbuffer) {
        this.ringbuffer = ringbuffer;
        this.sequence = ringbuffer.getHeadSequence();
    }

    @Override
    public boolean hasNext() {
        return this.sequence <= this.ringbuffer.getTailSequence();
    }

    @Override
    public E next() {
        return this.ringbuffer.read(this.sequence++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

