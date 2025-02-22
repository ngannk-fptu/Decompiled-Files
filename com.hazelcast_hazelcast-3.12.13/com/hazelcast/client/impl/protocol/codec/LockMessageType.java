/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

public enum LockMessageType {
    LOCK_ISLOCKED(1793),
    LOCK_ISLOCKEDBYCURRENTTHREAD(1794),
    LOCK_GETLOCKCOUNT(1795),
    LOCK_GETREMAININGLEASETIME(1796),
    LOCK_LOCK(1797),
    LOCK_UNLOCK(1798),
    LOCK_FORCEUNLOCK(1799),
    LOCK_TRYLOCK(1800);

    private final int id;

    private LockMessageType(int messageType) {
        this.id = messageType;
    }

    public int id() {
        return this.id;
    }
}

