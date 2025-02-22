/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt;

import com.hazelcast.cluster.impl.VectorClock;
import com.hazelcast.spi.Operation;
import java.util.Map;

public class CRDTReplicationContainer {
    private final Operation operation;
    private final Map<String, VectorClock> vectorClocks;

    public CRDTReplicationContainer(Operation operation, Map<String, VectorClock> vectorClocks) {
        this.operation = operation;
        this.vectorClocks = vectorClocks;
    }

    public Operation getOperation() {
        return this.operation;
    }

    public Map<String, VectorClock> getVectorClocks() {
        return this.vectorClocks;
    }
}

