/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.exception;

import com.hazelcast.nio.Address;
import com.hazelcast.spi.exception.RetryableHazelcastException;

public class CallerNotMemberException
extends RetryableHazelcastException {
    public CallerNotMemberException(Address thisAddress, Address caller, int partitionId, String operationName, String serviceName) {
        super("Not Member! this: " + thisAddress + ", caller: " + caller + ", partitionId: " + partitionId + ", operation: " + operationName + ", service: " + serviceName);
    }

    public CallerNotMemberException(String message) {
        super(message);
    }
}

