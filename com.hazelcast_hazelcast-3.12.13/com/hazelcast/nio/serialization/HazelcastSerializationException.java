/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import com.hazelcast.core.HazelcastException;

public class HazelcastSerializationException
extends HazelcastException {
    public HazelcastSerializationException(String message) {
        super(message);
    }

    public HazelcastSerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public HazelcastSerializationException(Throwable e) {
        super(e);
    }
}

