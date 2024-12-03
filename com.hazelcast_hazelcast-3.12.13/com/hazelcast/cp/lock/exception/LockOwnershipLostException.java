/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.lock.exception;

public class LockOwnershipLostException
extends IllegalMonitorStateException {
    public LockOwnershipLostException() {
    }

    public LockOwnershipLostException(String message) {
        super(message);
    }
}

