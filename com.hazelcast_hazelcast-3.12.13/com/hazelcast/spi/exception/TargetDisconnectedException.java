/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.exception;

import com.hazelcast.core.HazelcastException;

public class TargetDisconnectedException
extends HazelcastException {
    public TargetDisconnectedException(String message) {
        super(message);
    }

    public TargetDisconnectedException(String message, Throwable cause) {
        super(message, cause);
    }
}

