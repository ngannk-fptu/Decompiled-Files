/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.exception;

import com.hazelcast.nio.Address;
import com.hazelcast.spi.exception.RetryableHazelcastException;

public class PartitionMigratingException
extends RetryableHazelcastException {
    public PartitionMigratingException(Address thisAddress, int partitionId, String operationName, String serviceName) {
        super("Partition is migrating! this: " + thisAddress + ", partitionId: " + partitionId + ", operation: " + operationName + ", service: " + serviceName);
    }

    public PartitionMigratingException(String message) {
        super(message);
    }
}

