/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.exception;

import com.hazelcast.core.HazelcastException;

public class DistributedObjectDestroyedException
extends HazelcastException {
    public DistributedObjectDestroyedException(String message) {
        super(message);
    }
}

