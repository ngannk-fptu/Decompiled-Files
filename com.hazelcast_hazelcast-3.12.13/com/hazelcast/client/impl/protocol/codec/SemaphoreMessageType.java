/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum SemaphoreMessageType {
    SEMAPHORE_INIT(3329),
    SEMAPHORE_ACQUIRE(3330),
    SEMAPHORE_AVAILABLEPERMITS(3331),
    SEMAPHORE_DRAINPERMITS(3332),
    SEMAPHORE_REDUCEPERMITS(3333),
    SEMAPHORE_RELEASE(3334),
    SEMAPHORE_TRYACQUIRE(3335),
    SEMAPHORE_INCREASEPERMITS(3336);

    private final int id;

    private SemaphoreMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

