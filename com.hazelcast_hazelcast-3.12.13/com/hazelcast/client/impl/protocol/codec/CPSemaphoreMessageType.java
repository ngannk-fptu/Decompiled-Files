/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum CPSemaphoreMessageType {
    CPSEMAPHORE_INIT(9985),
    CPSEMAPHORE_ACQUIRE(9986),
    CPSEMAPHORE_RELEASE(9987),
    CPSEMAPHORE_DRAIN(9988),
    CPSEMAPHORE_CHANGE(9989),
    CPSEMAPHORE_AVAILABLEPERMITS(9990),
    CPSEMAPHORE_GETSEMAPHORETYPE(9991);

    private final int id;

    private CPSemaphoreMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

