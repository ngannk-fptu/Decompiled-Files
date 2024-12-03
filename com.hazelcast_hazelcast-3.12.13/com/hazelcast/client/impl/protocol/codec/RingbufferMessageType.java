/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum RingbufferMessageType {
    RINGBUFFER_SIZE(6401),
    RINGBUFFER_TAILSEQUENCE(6402),
    RINGBUFFER_HEADSEQUENCE(6403),
    RINGBUFFER_CAPACITY(6404),
    RINGBUFFER_REMAININGCAPACITY(6405),
    RINGBUFFER_ADD(6406),
    RINGBUFFER_READONE(6408),
    RINGBUFFER_ADDALL(6409),
    RINGBUFFER_READMANY(6410);

    private final int id;

    private RingbufferMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

