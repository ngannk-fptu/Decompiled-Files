/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

public class HazelcastException
extends RuntimeException {
    public HazelcastException() {
    }

    public HazelcastException(String message) {
        super(message);
    }

    public HazelcastException(String message, Throwable cause) {
        super(message, cause);
    }

    public HazelcastException(Throwable cause) {
        super(cause);
    }
}

