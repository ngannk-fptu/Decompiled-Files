/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.partition.NonFragmentedServiceNamespace;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.PartitionReplicaManager;
import com.hazelcast.internal.partition.operation.PartitionBackupReplicaAntiEntropyOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.FragmentedMigrationAwareService;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.PartitionSpecificRunnable;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public abstract class AbstractPartitionPrimaryReplicaAntiEntropyTask
implements PartitionSpecificRunnable,
UrgentSystemOperation {
    private static final int OPERATION_TRY_COUNT = 10;
    private static final int OPERATION_TRY_PAUSE_MILLIS = 250;
    protected final NodeEngineImpl nodeEngine;
    protected final InternalPartitionServiceImpl partitionService;
    protected final int partitionId;

    public AbstractPartitionPrimaryReplicaAntiEntropyTask(NodeEngineImpl nodeEngine, int partitionId) {
        this.nodeEngine = nodeEngine;
        this.partitionService = (InternalPartitionServiceImpl)nodeEngine.getPartitionService();
        this.partitionId = partitionId;
    }

    @Override
    public int getPartitionId() {
        return this.partitionId;
    }

    final Collection<ServiceNamespace> retainAndGetNamespaces() {
        PartitionReplicationEvent event = new PartitionReplicationEvent(this.partitionId, 0);
        Collection<FragmentedMigrationAwareService> services = this.nodeEngine.getServices(FragmentedMigrationAwareService.class);
        HashSet<ServiceNamespace> namespaces = new HashSet<ServiceNamespace>();
        for (FragmentedMigrationAwareService service : services) {
            Collection<ServiceNamespace> serviceNamespaces = service.getAllServiceNamespaces(event);
            if (serviceNamespaces == null) continue;
            namespaces.addAll(serviceNamespaces);
        }
        namespaces.add(NonFragmentedServiceNamespace.INSTANCE);
        PartitionReplicaManager replicaManager = this.partitionService.getReplicaManager();
        replicaManager.retainNamespaces(this.partitionId, namespaces);
        return replicaManager.getNamespaces(this.partitionId);
    }

    final void invokePartitionBackupReplicaAntiEntropyOp(int replicaIndex, PartitionReplica target, Collection<ServiceNamespace> namespaces, ExecutionCallback callback) {
        if (this.skipSendingToTarget(target)) {
            return;
        }
        PartitionReplicaManager replicaManager = this.partitionService.getReplicaManager();
        HashMap<ServiceNamespace, Long> versionMap = new HashMap<ServiceNamespace, Long>();
        for (ServiceNamespace ns : namespaces) {
            long[] versions = replicaManager.getPartitionReplicaVersions(this.partitionId, ns);
            long currentReplicaVersion = versions[replicaIndex - 1];
            if (currentReplicaVersion <= 0L) continue;
            versionMap.put(ns, currentReplicaVersion);
        }
        boolean hasCallback = callback != null;
        PartitionBackupReplicaAntiEntropyOperation op = new PartitionBackupReplicaAntiEntropyOperation(versionMap, hasCallback);
        op.setPartitionId(this.partitionId).setReplicaIndex(replicaIndex).setServiceName("hz:core:partitionService");
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        if (hasCallback) {
            operationService.createInvocationBuilder("hz:core:partitionService", (Operation)op, target.address()).setExecutionCallback(callback).setTryCount(10).setTryPauseMillis(250L).invoke();
        } else {
            operationService.send(op, target.address());
        }
    }

    private boolean skipSendingToTarget(PartitionReplica target) {
        ClusterServiceImpl clusterService = this.nodeEngine.getNode().getClusterService();
        assert (!target.isIdentical(this.nodeEngine.getLocalMember())) : "Could not send anti-entropy operation, because " + target + " is local member itself! Local-member: " + clusterService.getLocalMember() + ", " + this.partitionService.getPartition(this.partitionId);
        if (clusterService.getMember(target.address(), target.uuid()) == null) {
            ILogger logger = this.nodeEngine.getLogger(this.getClass());
            if (logger.isFinestEnabled()) {
                if (clusterService.isMissingMember(target.address(), target.uuid())) {
                    logger.finest("Could not send anti-entropy operation, because " + target + " is a missing member. " + this.partitionService.getPartition(this.partitionId));
                } else {
                    logger.finest("Could not send anti-entropy operation, because " + target + " is not a known member. " + this.partitionService.getPartition(this.partitionId));
                }
            }
            return true;
        }
        return false;
    }
}

