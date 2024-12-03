/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.HazelcastException;

public class IndeterminateOperationStateException
extends HazelcastException {
    public IndeterminateOperationStateException() {
    }

    public IndeterminateOperationStateException(String message) {
        super(message);
    }

    public IndeterminateOperationStateException(String message, Throwable cause) {
        super(message, cause);
    }
}

