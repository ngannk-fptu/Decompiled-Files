/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.exception;

import com.hazelcast.core.HazelcastException;

public class WaitKeyCancelledException
extends HazelcastException {
    private static final long serialVersionUID = 437835746549840631L;

    public WaitKeyCancelledException() {
    }

    public WaitKeyCancelledException(String message) {
        super(message);
    }

    public WaitKeyCancelledException(String message, Throwable cause) {
        super(message, cause);
    }
}

