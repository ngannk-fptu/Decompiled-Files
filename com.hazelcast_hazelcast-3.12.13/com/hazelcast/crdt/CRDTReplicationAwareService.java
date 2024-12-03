/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt;

import com.hazelcast.cluster.impl.VectorClock;
import com.hazelcast.crdt.CRDTReplicationContainer;
import java.util.Map;

public interface CRDTReplicationAwareService<T> {
    public CRDTReplicationContainer prepareReplicationOperation(Map<String, VectorClock> var1, int var2);

    public String getName();

    public void merge(String var1, T var2);

    public CRDTReplicationContainer prepareMigrationOperation(int var1);

    public boolean clearCRDTState(Map<String, VectorClock> var1);

    public void prepareToSafeShutdown();
}

