/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.HazelcastException;

public class OperationTimeoutException
extends HazelcastException {
    public OperationTimeoutException() {
    }

    public OperationTimeoutException(String message) {
        super(message);
    }

    public OperationTimeoutException(String op, String message) {
        super("[" + op + "] " + message);
    }

    public OperationTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}

