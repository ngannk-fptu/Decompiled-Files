/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt.pncounter;

import com.hazelcast.crdt.AbstractCRDTReplicationOperation;
import com.hazelcast.crdt.pncounter.PNCounterImpl;
import java.util.Map;

public class PNCounterReplicationOperation
extends AbstractCRDTReplicationOperation<PNCounterImpl> {
    public PNCounterReplicationOperation() {
    }

    PNCounterReplicationOperation(Map<String, PNCounterImpl> migrationData) {
        super(migrationData);
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:PNCounterService";
    }
}

