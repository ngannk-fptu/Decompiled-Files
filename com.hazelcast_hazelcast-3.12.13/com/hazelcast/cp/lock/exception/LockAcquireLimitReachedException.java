/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.lock.exception;

import com.hazelcast.core.HazelcastException;

public class LockAcquireLimitReachedException
extends HazelcastException {
    public LockAcquireLimitReachedException() {
    }

    public LockAcquireLimitReachedException(String message) {
        super(message);
    }
}

