/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.exception;

import com.hazelcast.core.HazelcastException;

public class ResponseAlreadySentException
extends HazelcastException {
    public ResponseAlreadySentException() {
    }

    public ResponseAlreadySentException(String message) {
        super(message);
    }
}

