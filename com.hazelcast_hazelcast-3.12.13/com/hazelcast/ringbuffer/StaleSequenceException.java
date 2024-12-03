/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer;

import com.hazelcast.spi.exception.SilentException;

public class StaleSequenceException
extends RuntimeException
implements SilentException {
    private final long headSeq;

    public StaleSequenceException(String message, long headSeq) {
        super(message);
        this.headSeq = headSeq;
    }

    public long getHeadSeq() {
        return this.headSeq;
    }
}

