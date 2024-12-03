/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.exception;

import com.hazelcast.core.Member;
import com.hazelcast.spi.exception.RetryableHazelcastException;

public class WrongTargetException
extends RetryableHazelcastException {
    private static final long serialVersionUID = -84600702836709317L;
    private transient Member target;

    public WrongTargetException(Member localMember, Member target, int partitionId, int replicaIndex, String operationName) {
        this(localMember, target, partitionId, replicaIndex, operationName, null);
    }

    public WrongTargetException(Member localMember, Member target, int partitionId, int replicaIndex, String operationName, String serviceName) {
        super("WrongTarget! local: " + localMember + ", expected-target: " + target + ", partitionId: " + partitionId + ", replicaIndex: " + replicaIndex + ", operation: " + operationName + ", service: " + serviceName);
        this.target = target;
    }

    public WrongTargetException(String message) {
        super(message);
    }

    public Member getTarget() {
        return this.target;
    }
}

