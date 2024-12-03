/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl;

import com.hazelcast.ringbuffer.impl.ArrayRingbuffer;
import java.util.Iterator;

public class ReadOnlyRingbufferIterator<E>
implements Iterator<E> {
    private final ArrayRingbuffer<E> ringbuffer;
    private long sequence;

    ReadOnlyRingbufferIterator(ArrayRingbuffer<E> ringbuffer) {
        this.ringbuffer = ringbuffer;
        this.sequence = ringbuffer.headSequence();
    }

    @Override
    public boolean hasNext() {
        return this.sequence <= this.ringbuffer.tailSequence();
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

