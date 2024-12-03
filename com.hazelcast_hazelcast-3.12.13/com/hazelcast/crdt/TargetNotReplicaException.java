/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt;

import com.hazelcast.spi.exception.RetryableHazelcastException;

public class TargetNotReplicaException
extends RetryableHazelcastException {
    public TargetNotReplicaException(String message) {
        super(message);
    }
}

