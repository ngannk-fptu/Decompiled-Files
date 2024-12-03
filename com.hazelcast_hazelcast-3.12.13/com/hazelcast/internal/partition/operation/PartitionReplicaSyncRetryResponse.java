/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.internal.partition.MigrationCycleOperation;
import com.hazelcast.internal.partition.ReplicaErrorLogger;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.PartitionReplicaManager;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ServiceNamespace;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PartitionReplicaSyncRetryResponse
extends AbstractPartitionOperation
implements PartitionAwareOperation,
BackupOperation,
MigrationCycleOperation,
Versioned {
    private Collection<ServiceNamespace> namespaces;

    public PartitionReplicaSyncRetryResponse() {
        this.namespaces = Collections.emptySet();
    }

    public PartitionReplicaSyncRetryResponse(Collection<ServiceNamespace> namespaces) {
        this.namespaces = namespaces;
    }

    @Override
    public void run() throws Exception {
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        int partitionId = this.getPartitionId();
        int replicaIndex = this.getReplicaIndex();
        PartitionReplicaManager replicaManager = partitionService.getReplicaManager();
        for (ServiceNamespace namespace : this.namespaces) {
            replicaManager.clearReplicaSyncRequest(partitionId, namespace, replicaIndex);
        }
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    public boolean validatesTarget() {
        return false;
    }

    @Override
    public String getServiceName() {
        return "hz:core:partitionService";
    }

    @Override
    public void logError(Throwable e) {
        ReplicaErrorLogger.log(e, this.getLogger());
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.namespaces.size());
        for (ServiceNamespace namespace : this.namespaces) {
            out.writeObject(namespace);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int len = in.readInt();
        this.namespaces = new ArrayList<ServiceNamespace>(len);
        for (int i = 0; i < len; ++i) {
            ServiceNamespace ns = (ServiceNamespace)in.readObject();
            this.namespaces.add(ns);
        }
    }

    @Override
    public int getId() {
        return 13;
    }
}

