/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.properties;

import com.hazelcast.core.HazelcastException;

public class ValidationException
extends HazelcastException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        this(cause.getMessage(), cause);
    }
}

