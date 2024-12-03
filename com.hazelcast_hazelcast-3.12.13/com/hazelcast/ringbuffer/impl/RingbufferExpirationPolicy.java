/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl;

import com.hazelcast.ringbuffer.impl.Ringbuffer;
import com.hazelcast.util.Clock;
import java.util.Arrays;

final class RingbufferExpirationPolicy {
    long[] ringExpirationMs;
    private final long ttlMs;

    RingbufferExpirationPolicy(long capacity, long ttlMs) {
        this.ringExpirationMs = new long[(int)capacity];
        this.ttlMs = ttlMs;
    }

    void cleanup(Ringbuffer ringbuffer) {
        if (ringbuffer.headSequence() > ringbuffer.tailSequence()) {
            return;
        }
        long now = Clock.currentTimeMillis();
        while (ringbuffer.headSequence() <= ringbuffer.tailSequence()) {
            long headSequence = ringbuffer.headSequence();
            if (this.ringExpirationMs[this.toIndex(headSequence)] > now) {
                return;
            }
            ringbuffer.set(headSequence, null);
            ringbuffer.setHeadSequence(ringbuffer.headSequence() + 1L);
        }
    }

    int toIndex(long sequence) {
        return (int)(sequence % (long)this.ringExpirationMs.length);
    }

    void setExpirationAt(long sequence) {
        this.setExpirationAt(sequence, Clock.currentTimeMillis() + this.ttlMs);
    }

    long getExpirationAt(long seq) {
        return this.ringExpirationMs[this.toIndex(seq)];
    }

    void setExpirationAt(long seq, long value) {
        this.ringExpirationMs[this.toIndex((long)seq)] = value;
    }

    long getTtlMs() {
        return this.ttlMs;
    }

    public void clear() {
        Arrays.fill(this.ringExpirationMs, 0L);
    }
}

