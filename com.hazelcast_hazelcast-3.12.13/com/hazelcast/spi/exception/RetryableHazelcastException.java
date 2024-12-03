/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.exception;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.spi.exception.RetryableException;

public class RetryableHazelcastException
extends HazelcastException
implements RetryableException {
    public RetryableHazelcastException() {
    }

    public RetryableHazelcastException(String message) {
        super(message);
    }

    public RetryableHazelcastException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryableHazelcastException(Throwable cause) {
        super(cause);
    }
}

