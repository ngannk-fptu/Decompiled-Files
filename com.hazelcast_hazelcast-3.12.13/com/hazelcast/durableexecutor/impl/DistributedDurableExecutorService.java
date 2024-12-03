/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl;

import com.hazelcast.config.DurableExecutorConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.durableexecutor.impl.DurableExecutorPartitionContainer;
import com.hazelcast.durableexecutor.impl.DurableExecutorServiceProxy;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.MigrationAwareService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.QuorumAwareService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DistributedDurableExecutorService
implements ManagedService,
RemoteService,
MigrationAwareService,
QuorumAwareService {
    public static final String SERVICE_NAME = "hz:impl:durableExecutorService";
    private static final Object NULL_OBJECT = new Object();
    private final NodeEngineImpl nodeEngine;
    private final DurableExecutorPartitionContainer[] partitionContainers;
    private final Set<String> shutdownExecutors = Collections.newSetFromMap(new ConcurrentHashMap());
    private final ConcurrentMap<String, Object> quorumConfigCache = new ConcurrentHashMap<String, Object>();
    private final ContextMutexFactory quorumConfigCacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<String, Object> quorumConfigConstructor = new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            DurableExecutorConfig executorConfig = DistributedDurableExecutorService.this.nodeEngine.getConfig().findDurableExecutorConfig(name);
            String quorumName = executorConfig.getQuorumName();
            return quorumName == null ? NULL_OBJECT : quorumName;
        }
    };

    public DistributedDurableExecutorService(NodeEngineImpl nodeEngine) {
        this.nodeEngine = nodeEngine;
        int partitionCount = nodeEngine.getPartitionService().getPartitionCount();
        this.partitionContainers = new DurableExecutorPartitionContainer[partitionCount];
        for (int partitionId = 0; partitionId < partitionCount; ++partitionId) {
            this.partitionContainers[partitionId] = new DurableExecutorPartitionContainer(nodeEngine, partitionId);
        }
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
    }

    public DurableExecutorPartitionContainer getPartitionContainer(int partitionId) {
        return this.partitionContainers[partitionId];
    }

    public NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    @Override
    public void reset() {
        this.shutdownExecutors.clear();
        for (int partitionId = 0; partitionId < this.partitionContainers.length; ++partitionId) {
            this.partitionContainers[partitionId] = new DurableExecutorPartitionContainer(this.nodeEngine, partitionId);
        }
    }

    @Override
    public void shutdown(boolean terminate) {
        this.reset();
    }

    @Override
    public DistributedObject createDistributedObject(String name) {
        return new DurableExecutorServiceProxy(this.nodeEngine, this, name);
    }

    @Override
    public void destroyDistributedObject(String name) {
        this.shutdownExecutors.remove(name);
        this.nodeEngine.getExecutionService().shutdownDurableExecutor(name);
        this.removeAllContainers(name);
        this.quorumConfigCache.remove(name);
    }

    public void shutdownExecutor(String name) {
        this.nodeEngine.getExecutionService().shutdownDurableExecutor(name);
        this.shutdownExecutors.add(name);
    }

    public boolean isShutdown(String name) {
        return this.shutdownExecutors.contains(name);
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        int partitionId = event.getPartitionId();
        DurableExecutorPartitionContainer partitionContainer = this.partitionContainers[partitionId];
        return partitionContainer.prepareReplicationOperation(event.getReplicaIndex());
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent event) {
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        int partitionId = event.getPartitionId();
        if (event.getMigrationEndpoint() == MigrationEndpoint.SOURCE) {
            this.clearRingBuffersHavingLesserBackupCountThan(partitionId, event.getNewReplicaIndex());
        } else if (event.getNewReplicaIndex() == 0) {
            DurableExecutorPartitionContainer partitionContainer = this.partitionContainers[partitionId];
            partitionContainer.executeAll();
        }
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION) {
            this.clearRingBuffersHavingLesserBackupCountThan(event.getPartitionId(), event.getCurrentReplicaIndex());
        }
    }

    private void clearRingBuffersHavingLesserBackupCountThan(int partitionId, int thresholdReplicaIndex) {
        DurableExecutorPartitionContainer partitionContainer = this.partitionContainers[partitionId];
        partitionContainer.clearRingBuffersHavingLesserBackupCountThan(thresholdReplicaIndex);
    }

    @Override
    public String getQuorumName(String name) {
        Object quorumName = ConcurrencyUtil.getOrPutSynchronized(this.quorumConfigCache, name, this.quorumConfigCacheMutexFactory, this.quorumConfigConstructor);
        return quorumName == NULL_OBJECT ? null : (String)quorumName;
    }

    private void removeAllContainers(String name) {
        for (int i = 0; i < this.partitionContainers.length; ++i) {
            this.getPartitionContainer(i).removeContainer(name);
        }
    }
}

