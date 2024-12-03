/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum CPFencedLockMessageType {
    CPFENCEDLOCK_LOCK(9729),
    CPFENCEDLOCK_TRYLOCK(9730),
    CPFENCEDLOCK_UNLOCK(9731),
    CPFENCEDLOCK_GETLOCKOWNERSHIP(9732);

    private final int id;

    private CPFencedLockMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

