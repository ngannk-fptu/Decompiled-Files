/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.SemaphoreProxy;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreDetachMemberOperation;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreReplicationOperation;
import com.hazelcast.config.SemaphoreConfig;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.spi.ClientAwareService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.MemberAttributeServiceEvent;
import com.hazelcast.spi.MembershipAwareService;
import com.hazelcast.spi.MembershipServiceEvent;
import com.hazelcast.spi.MigrationAwareService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.QuorumAwareService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SemaphoreService
implements ManagedService,
MigrationAwareService,
MembershipAwareService,
RemoteService,
ClientAwareService,
QuorumAwareService {
    public static final String SERVICE_NAME = "hz:impl:semaphoreService";
    private static final Object NULL_OBJECT = new Object();
    private final ConcurrentMap<String, SemaphoreContainer> containers = new ConcurrentHashMap<String, SemaphoreContainer>();
    private final ConcurrentMap<String, Object> quorumConfigCache = new ConcurrentHashMap<String, Object>();
    private final ContextMutexFactory quorumConfigCacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<String, Object> quorumConfigConstructor = new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            SemaphoreConfig semaphoreConfig = SemaphoreService.this.nodeEngine.getConfig().findSemaphoreConfig(name);
            String quorumName = semaphoreConfig.getQuorumName();
            return quorumName == null ? NULL_OBJECT : quorumName;
        }
    };
    private final ConstructorFunction<String, SemaphoreContainer> containerConstructor = new ConstructorFunction<String, SemaphoreContainer>(){

        @Override
        public SemaphoreContainer createNew(String name) {
            SemaphoreConfig config = SemaphoreService.this.nodeEngine.getConfig().findSemaphoreConfig(name);
            IPartitionService partitionService = SemaphoreService.this.nodeEngine.getPartitionService();
            int partitionId = partitionService.getPartitionId(StringPartitioningStrategy.getPartitionKey(name));
            return new SemaphoreContainer(partitionId, new SemaphoreConfig(config));
        }
    };
    private final NodeEngine nodeEngine;

    public SemaphoreService(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
    }

    public SemaphoreContainer getSemaphoreContainer(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.containers, name, this.containerConstructor);
    }

    public boolean containsSemaphore(String name) {
        return this.containers.containsKey(name);
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
    }

    @Override
    public void reset() {
        this.containers.clear();
    }

    @Override
    public void shutdown(boolean terminate) {
        this.containers.clear();
    }

    @Override
    public void memberAdded(MembershipServiceEvent event) {
    }

    @Override
    public void memberRemoved(MembershipServiceEvent event) {
        this.onOwnerDisconnected(event.getMember().getUuid());
    }

    @Override
    public void memberAttributeChanged(MemberAttributeServiceEvent event) {
    }

    private void onOwnerDisconnected(String owner) {
        OperationService operationService = this.nodeEngine.getOperationService();
        for (Map.Entry entry : this.containers.entrySet()) {
            String name = (String)entry.getKey();
            SemaphoreContainer container = (SemaphoreContainer)entry.getValue();
            Operation op = new SemaphoreDetachMemberOperation(name, owner).setPartitionId(container.getPartitionId()).setValidateTarget(false).setService(this).setNodeEngine(this.nodeEngine).setServiceName(SERVICE_NAME);
            operationService.invokeOnTarget(SERVICE_NAME, op, this.nodeEngine.getThisAddress());
        }
    }

    @Override
    public SemaphoreProxy createDistributedObject(String objectId) {
        return new SemaphoreProxy(objectId, this, this.nodeEngine);
    }

    @Override
    public void destroyDistributedObject(String objectId) {
        this.containers.remove(objectId);
        this.quorumConfigCache.remove(objectId);
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent partitionMigrationEvent) {
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        HashMap<String, SemaphoreContainer> migrationData = new HashMap<String, SemaphoreContainer>();
        for (Map.Entry entry : this.containers.entrySet()) {
            String name = (String)entry.getKey();
            SemaphoreContainer semaphoreContainer = (SemaphoreContainer)entry.getValue();
            if (semaphoreContainer.getPartitionId() != event.getPartitionId() || semaphoreContainer.getTotalBackupCount() < event.getReplicaIndex()) continue;
            migrationData.put(name, semaphoreContainer);
        }
        if (migrationData.isEmpty()) {
            return null;
        }
        return new SemaphoreReplicationOperation(migrationData);
    }

    public void insertMigrationData(Map<String, SemaphoreContainer> migrationData) {
        this.containers.putAll(migrationData);
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.SOURCE) {
            this.clearSemaphoresHavingLesserBackupCountThan(event.getPartitionId(), event.getNewReplicaIndex());
        }
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION) {
            this.clearSemaphoresHavingLesserBackupCountThan(event.getPartitionId(), event.getCurrentReplicaIndex());
        }
    }

    private void clearSemaphoresHavingLesserBackupCountThan(int partitionId, int thresholdReplicaIndex) {
        Iterator it = this.containers.values().iterator();
        while (it.hasNext()) {
            SemaphoreContainer semaphoreContainer = (SemaphoreContainer)it.next();
            if (semaphoreContainer.getPartitionId() != partitionId || thresholdReplicaIndex >= 0 && thresholdReplicaIndex <= semaphoreContainer.getTotalBackupCount()) continue;
            it.remove();
        }
    }

    @Override
    public void clientDisconnected(String clientUuid) {
        this.onOwnerDisconnected(clientUuid);
    }

    @Override
    public String getQuorumName(String name) {
        Object quorumName = ConcurrencyUtil.getOrPutSynchronized(this.quorumConfigCache, name, this.quorumConfigCacheMutexFactory, this.quorumConfigConstructor);
        return quorumName == NULL_OBJECT ? null : (String)quorumName;
    }
}

