/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.memory;

public class NativeOutOfMemoryError
extends Error {
    public NativeOutOfMemoryError() {
    }

    public NativeOutOfMemoryError(String message) {
        super(message);
    }

    public NativeOutOfMemoryError(String message, Throwable cause) {
        super(message, cause);
    }
}

