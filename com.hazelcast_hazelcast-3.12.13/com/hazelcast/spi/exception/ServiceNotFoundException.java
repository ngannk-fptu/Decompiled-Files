/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.exception;

import com.hazelcast.core.HazelcastException;

public class ServiceNotFoundException
extends HazelcastException {
    public ServiceNotFoundException(String message) {
        super(message);
    }
}

