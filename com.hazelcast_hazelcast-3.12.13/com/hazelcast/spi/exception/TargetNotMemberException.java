/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.exception;

import com.hazelcast.spi.exception.RetryableHazelcastException;

public class TargetNotMemberException
extends RetryableHazelcastException {
    private static final long serialVersionUID = -3791433456807089118L;

    public TargetNotMemberException(String message) {
        super(message);
    }

    public TargetNotMemberException(Object target, int partitionId, String operationName, String serviceName) {
        super("Not Member! target: " + target + ", partitionId: " + partitionId + ", operation: " + operationName + ", service: " + serviceName);
    }
}

