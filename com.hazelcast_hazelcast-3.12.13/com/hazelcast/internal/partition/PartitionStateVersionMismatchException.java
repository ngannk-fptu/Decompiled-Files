/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition;

import com.hazelcast.core.HazelcastException;

public class PartitionStateVersionMismatchException
extends HazelcastException {
    public PartitionStateVersionMismatchException(String message) {
        super(message);
    }

    public PartitionStateVersionMismatchException(int masterVersion, int localVersion) {
        this("Local partition state version is not equal to master's version! Local: " + localVersion + ", Master: " + masterVersion);
    }
}

