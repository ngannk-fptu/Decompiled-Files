/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.MigrationCycleOperation;
import com.hazelcast.internal.partition.NonFragmentedServiceNamespace;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.PartitionReplicaVersionManager;
import com.hazelcast.internal.partition.ReplicaErrorLogger;
import com.hazelcast.internal.partition.impl.InternalPartitionImpl;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.PartitionStateManager;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.internal.partition.operation.PartitionReplicaSyncResponse;
import com.hazelcast.internal.partition.operation.PartitionReplicaSyncRetryResponse;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.ServiceNamespace;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class PartitionReplicaSyncRequest
extends AbstractPartitionOperation
implements PartitionAwareOperation,
MigrationCycleOperation,
Versioned {
    private List<ServiceNamespace> namespaces;

    public PartitionReplicaSyncRequest() {
        this.namespaces = Collections.emptyList();
    }

    public PartitionReplicaSyncRequest(int partitionId, List<ServiceNamespace> namespaces, int replicaIndex) {
        this.namespaces = namespaces;
        this.setPartitionId(partitionId);
        this.setReplicaIndex(replicaIndex);
    }

    @Override
    public void beforeRun() {
        int syncReplicaIndex = this.getReplicaIndex();
        if (syncReplicaIndex < 1 || syncReplicaIndex > 6) {
            throw new IllegalArgumentException("Replica index " + syncReplicaIndex + " should be in the range [1-" + 6 + "]");
        }
    }

    @Override
    public void run() {
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        if (!partitionService.areMigrationTasksAllowed()) {
            ILogger logger = this.getLogger();
            if (logger.isFinestEnabled()) {
                logger.finest("Migration is paused! Cannot process request. partitionId=" + this.getPartitionId() + ", replicaIndex=" + this.getReplicaIndex() + ", namespaces=" + this.namespaces);
            }
            this.sendRetryResponse();
            return;
        }
        if (!this.checkPartitionOwner()) {
            this.sendRetryResponse();
            return;
        }
        int permits = partitionService.getReplicaManager().tryAcquireReplicaSyncPermits(this.namespaces.size());
        if (permits == 0) {
            this.logNotEnoughPermits();
            this.sendRetryResponse();
            return;
        }
        this.sendOperationsForNamespaces(permits);
        if (!this.namespaces.isEmpty()) {
            this.logNotEnoughPermits();
            this.sendRetryResponse();
        }
    }

    private void logNotEnoughPermits() {
        ILogger logger = this.getLogger();
        if (logger.isFinestEnabled()) {
            logger.finest("Not enough permits available! Cannot process request. partitionId=" + this.getPartitionId() + ", replicaIndex=" + this.getReplicaIndex() + ", namespaces=" + this.namespaces);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendOperationsForNamespaces(int permits) {
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        try {
            PartitionReplicationEvent event = new PartitionReplicationEvent(this.getPartitionId(), this.getReplicaIndex());
            Iterator<ServiceNamespace> iterator = this.namespaces.iterator();
            for (int i = 0; i < permits; ++i) {
                ServiceNamespace namespace = iterator.next();
                Collection<Operation> operations = NonFragmentedServiceNamespace.INSTANCE.equals(namespace) ? this.createNonFragmentedReplicationOperations(event) : this.createFragmentReplicationOperations(event, namespace);
                this.sendOperations(operations, namespace);
                iterator.remove();
            }
        }
        finally {
            partitionService.getReplicaManager().releaseReplicaSyncPermits(permits);
        }
    }

    private void sendOperations(Collection<Operation> operations, ServiceNamespace ns) {
        if (operations.isEmpty()) {
            this.logNoReplicaDataFound(this.getPartitionId(), ns, this.getReplicaIndex());
            this.sendResponse(null, ns);
        } else {
            this.sendResponse(operations, ns);
        }
    }

    private boolean checkPartitionOwner() {
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        PartitionStateManager partitionStateManager = partitionService.getPartitionStateManager();
        InternalPartitionImpl partition = partitionStateManager.getPartitionImpl(this.getPartitionId());
        PartitionReplica owner = partition.getOwnerReplicaOrNull();
        NodeEngine nodeEngine = this.getNodeEngine();
        if (owner == null || !owner.isIdentical(nodeEngine.getLocalMember())) {
            ILogger logger = this.getLogger();
            if (logger.isFinestEnabled()) {
                logger.finest("This node is not owner partition. Cannot process request. partitionId=" + this.getPartitionId() + ", replicaIndex=" + this.getReplicaIndex() + ", namespaces=" + this.namespaces);
            }
            return false;
        }
        return true;
    }

    private void sendRetryResponse() {
        NodeEngine nodeEngine = this.getNodeEngine();
        int partitionId = this.getPartitionId();
        int replicaIndex = this.getReplicaIndex();
        PartitionReplicaSyncRetryResponse response = new PartitionReplicaSyncRetryResponse(this.namespaces);
        response.setPartitionId(partitionId).setReplicaIndex(replicaIndex);
        Address target = this.getCallerAddress();
        OperationService operationService = nodeEngine.getOperationService();
        operationService.send(response, target);
    }

    private void sendResponse(Collection<Operation> operations, ServiceNamespace ns) {
        NodeEngine nodeEngine = this.getNodeEngine();
        PartitionReplicaSyncResponse syncResponse = this.createResponse(operations, ns);
        Address target = this.getCallerAddress();
        ILogger logger = this.getLogger();
        if (logger.isFinestEnabled()) {
            logger.finest("Sending sync response to -> " + target + " for partitionId=" + this.getPartitionId() + ", replicaIndex=" + this.getReplicaIndex() + ", namespaces=" + ns);
        }
        syncResponse.setTarget(target);
        OperationService operationService = nodeEngine.getOperationService();
        operationService.send(syncResponse, target);
    }

    private PartitionReplicaSyncResponse createResponse(Collection<Operation> operations, ServiceNamespace ns) {
        int partitionId = this.getPartitionId();
        int replicaIndex = this.getReplicaIndex();
        InternalPartitionService partitionService = (InternalPartitionService)this.getService();
        PartitionReplicaVersionManager versionManager = partitionService.getPartitionReplicaVersionManager();
        long[] versions = versionManager.getPartitionReplicaVersions(partitionId, ns);
        PartitionReplicaSyncResponse syncResponse = new PartitionReplicaSyncResponse(operations, ns, versions);
        syncResponse.setPartitionId(partitionId).setReplicaIndex(replicaIndex);
        return syncResponse;
    }

    private void logNoReplicaDataFound(int partitionId, ServiceNamespace namespace, int replicaIndex) {
        ILogger logger = this.getLogger();
        if (logger.isFinestEnabled()) {
            logger.finest("No replica data is found for partitionId=" + partitionId + ", replicaIndex=" + replicaIndex + ", namespace= " + namespace);
        }
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    public boolean validatesTarget() {
        return false;
    }

    @Override
    public void logError(Throwable e) {
        ReplicaErrorLogger.log(e, this.getLogger());
    }

    @Override
    public String getServiceName() {
        return "hz:core:partitionService";
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
        return 11;
    }
}

