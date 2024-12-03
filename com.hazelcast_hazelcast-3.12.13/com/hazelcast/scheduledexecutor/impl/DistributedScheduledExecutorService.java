/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.partition.PartitionLostEvent;
import com.hazelcast.partition.PartitionLostListener;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainer;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainerCollector;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainerHolder;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorMemberBin;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorPartition;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorServiceProxy;
import com.hazelcast.scheduledexecutor.impl.ScheduledFutureProxy;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskDescriptor;
import com.hazelcast.scheduledexecutor.impl.operations.MergeOperation;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.MemberAttributeServiceEvent;
import com.hazelcast.spi.MembershipAwareService;
import com.hazelcast.spi.MembershipServiceEvent;
import com.hazelcast.spi.MigrationAwareService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.QuorumAwareService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.SplitBrainHandlerService;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.impl.merge.AbstractContainerMerger;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import com.hazelcast.util.ExceptionUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class DistributedScheduledExecutorService
implements ManagedService,
RemoteService,
MigrationAwareService,
QuorumAwareService,
SplitBrainHandlerService,
MembershipAwareService {
    public static final String SERVICE_NAME = "hz:impl:scheduledExecutorService";
    public static final int MEMBER_BIN = -1;
    private static final Object NULL_OBJECT = new Object();
    private final ConcurrentMap<String, Boolean> shutdownExecutors = new ConcurrentHashMap<String, Boolean>();
    private final Set<ScheduledFutureProxy> lossListeners = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private final AtomicBoolean migrationMode = new AtomicBoolean();
    private final ConcurrentMap<String, Object> quorumConfigCache = new ConcurrentHashMap<String, Object>();
    private final ContextMutexFactory quorumConfigCacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<String, Object> quorumConfigConstructor = new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            ScheduledExecutorConfig executorConfig = DistributedScheduledExecutorService.this.nodeEngine.getConfig().findScheduledExecutorConfig(name);
            String quorumName = executorConfig.getQuorumName();
            return quorumName == null ? NULL_OBJECT : quorumName;
        }
    };
    private NodeEngine nodeEngine;
    private ScheduledExecutorPartition[] partitions;
    private ScheduledExecutorMemberBin memberBin;
    private String partitionLostRegistration;

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        int partitionCount = nodeEngine.getPartitionService().getPartitionCount();
        this.nodeEngine = nodeEngine;
        this.partitions = new ScheduledExecutorPartition[partitionCount];
        this.reset();
    }

    public ScheduledExecutorPartition getPartition(int partitionId) {
        return this.partitions[partitionId];
    }

    public ScheduledExecutorContainerHolder getPartitionOrMemberBin(int id) {
        if (id == -1) {
            return this.memberBin;
        }
        return this.getPartition(id);
    }

    public NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    @Override
    public void reset() {
        this.shutdown(true);
        this.memberBin = new ScheduledExecutorMemberBin(this.nodeEngine);
        if (this.partitionLostRegistration == null) {
            this.registerPartitionListener();
        }
        for (int partitionId = 0; partitionId < this.partitions.length; ++partitionId) {
            if (this.partitions[partitionId] != null) {
                this.partitions[partitionId].destroy();
            }
            this.partitions[partitionId] = new ScheduledExecutorPartition(this.nodeEngine, partitionId);
        }
    }

    @Override
    public void shutdown(boolean terminate) {
        this.shutdownExecutors.clear();
        if (this.memberBin != null) {
            this.memberBin.destroy();
        }
        this.lossListeners.clear();
        this.unRegisterPartitionListenerIfExists();
        for (ScheduledExecutorPartition partition : this.partitions) {
            if (partition == null) continue;
            partition.destroy();
        }
    }

    void addLossListener(ScheduledFutureProxy future) {
        this.lossListeners.add(future);
    }

    @Override
    public DistributedObject createDistributedObject(String name) {
        ScheduledExecutorConfig executorConfig = this.nodeEngine.getConfig().findScheduledExecutorConfig(name);
        ConfigValidator.checkScheduledExecutorConfig(executorConfig, this.nodeEngine.getSplitBrainMergePolicyProvider());
        return new ScheduledExecutorServiceProxy(name, this.nodeEngine, this);
    }

    @Override
    public void destroyDistributedObject(String name) {
        if (this.shutdownExecutors.remove(name) == null) {
            ((InternalExecutionService)this.nodeEngine.getExecutionService()).shutdownScheduledDurableExecutor(name);
        }
        this.resetPartitionOrMemberBinContainer(name);
        this.quorumConfigCache.remove(name);
    }

    public void shutdownExecutor(String name) {
        if (this.shutdownExecutors.putIfAbsent(name, Boolean.TRUE) == null) {
            ((InternalExecutionService)this.nodeEngine.getExecutionService()).shutdownScheduledDurableExecutor(name);
        }
    }

    public boolean isShutdown(String name) {
        return this.shutdownExecutors.containsKey(name);
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        int partitionId = event.getPartitionId();
        ScheduledExecutorPartition partition = this.partitions[partitionId];
        return partition.prepareReplicationOperation(event.getReplicaIndex(), this.migrationMode.get());
    }

    @Override
    public Runnable prepareMergeRunnable() {
        ScheduledExecutorContainerCollector collector = new ScheduledExecutorContainerCollector(this.nodeEngine, this.partitions);
        collector.run();
        return new Merger(collector);
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent event) {
        this.migrationMode.compareAndSet(false, true);
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        int partitionId = event.getPartitionId();
        if (event.getMigrationEndpoint() == MigrationEndpoint.SOURCE) {
            this.discardReserved(partitionId, event.getNewReplicaIndex());
        } else if (event.getNewReplicaIndex() == 0) {
            ScheduledExecutorPartition partition = this.partitions[partitionId];
            partition.promoteSuspended();
        }
        this.migrationMode.set(false);
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        int partitionId = event.getPartitionId();
        if (event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION) {
            this.discardReserved(event.getPartitionId(), event.getCurrentReplicaIndex());
        } else if (event.getCurrentReplicaIndex() == 0) {
            ScheduledExecutorPartition partition = this.partitions[partitionId];
            partition.promoteSuspended();
        }
        this.migrationMode.set(false);
    }

    private void discardReserved(int partitionId, int thresholdReplicaIndex) {
        ScheduledExecutorPartition partition = this.partitions[partitionId];
        partition.disposeObsoleteReplicas(thresholdReplicaIndex);
    }

    private void resetPartitionOrMemberBinContainer(String name) {
        if (this.memberBin != null) {
            this.memberBin.destroyContainer(name);
        }
        for (ScheduledExecutorPartition partition : this.partitions) {
            partition.destroyContainer(name);
        }
    }

    private void registerPartitionListener() {
        this.partitionLostRegistration = this.getNodeEngine().getPartitionService().addPartitionLostListener(new PartitionLostListener(){

            @Override
            public void partitionLost(PartitionLostEvent event) {
                ScheduledFutureProxy[] futures;
                for (ScheduledFutureProxy future : futures = DistributedScheduledExecutorService.this.lossListeners.toArray(new ScheduledFutureProxy[0])) {
                    future.notifyPartitionLost(event);
                }
            }
        });
    }

    private void unRegisterPartitionListenerIfExists() {
        block3: {
            if (this.partitionLostRegistration == null) {
                return;
            }
            try {
                this.getNodeEngine().getPartitionService().removePartitionLostListener(this.partitionLostRegistration);
            }
            catch (Exception ex) {
                if (!(ExceptionUtil.peel(ex, HazelcastInstanceNotActiveException.class, null) instanceof HazelcastInstanceNotActiveException)) break block3;
                throw ExceptionUtil.rethrow(ex);
            }
        }
        this.partitionLostRegistration = null;
    }

    @Override
    public void memberAdded(MembershipServiceEvent event) {
    }

    @Override
    public void memberRemoved(MembershipServiceEvent event) {
        ScheduledFutureProxy[] futures;
        for (ScheduledFutureProxy future : futures = this.lossListeners.toArray(new ScheduledFutureProxy[0])) {
            future.notifyMemberLost(event);
        }
    }

    @Override
    public void memberAttributeChanged(MemberAttributeServiceEvent event) {
    }

    @Override
    public String getQuorumName(String name) {
        Object quorumName = ConcurrencyUtil.getOrPutSynchronized(this.quorumConfigCache, name, this.quorumConfigCacheMutexFactory, this.quorumConfigConstructor);
        return quorumName == NULL_OBJECT ? null : (String)quorumName;
    }

    private class Merger
    extends AbstractContainerMerger<ScheduledExecutorContainer, ScheduledTaskDescriptor, SplitBrainMergeTypes.ScheduledExecutorMergeTypes> {
        Merger(ScheduledExecutorContainerCollector collector) {
            super(collector, DistributedScheduledExecutorService.this.nodeEngine);
        }

        @Override
        protected String getLabel() {
            return "scheduled executors";
        }

        @Override
        public void runInternal() {
            ScheduledExecutorContainerCollector collector = (ScheduledExecutorContainerCollector)this.collector;
            SerializationService serializationService = DistributedScheduledExecutorService.this.nodeEngine.getSerializationService();
            ConcurrentMap containerMap = collector.getCollectedContainers();
            for (Map.Entry entry : containerMap.entrySet()) {
                int partitionId = (Integer)entry.getKey();
                Collection containers = (Collection)entry.getValue();
                for (ScheduledExecutorContainer container : containers) {
                    String name = container.getName();
                    MergePolicyConfig mergePolicyConfig = collector.getMergePolicyConfig(container);
                    SplitBrainMergePolicy<ScheduledTaskDescriptor, SplitBrainMergeTypes.ScheduledExecutorMergeTypes> mergePolicy = this.getMergePolicy(mergePolicyConfig);
                    int batchSize = mergePolicyConfig.getBatchSize();
                    ArrayList<SplitBrainMergeTypes.ScheduledExecutorMergeTypes> mergingEntries = new ArrayList<SplitBrainMergeTypes.ScheduledExecutorMergeTypes>(batchSize);
                    for (ScheduledTaskDescriptor descriptor : container.prepareForReplication(true).values()) {
                        SplitBrainMergeTypes.ScheduledExecutorMergeTypes mergingEntry = MergingValueFactory.createMergingEntry(serializationService, descriptor);
                        mergingEntries.add(mergingEntry);
                    }
                    if (mergingEntries.size() == batchSize) {
                        this.sendBatch(partitionId, name, mergingEntries, mergePolicy);
                        mergingEntries = new ArrayList(batchSize);
                    }
                    if (mergingEntries.isEmpty()) continue;
                    this.sendBatch(partitionId, name, mergingEntries, mergePolicy);
                }
            }
        }

        private void sendBatch(int partitionId, String name, List<SplitBrainMergeTypes.ScheduledExecutorMergeTypes> mergingEntries, SplitBrainMergePolicy<ScheduledTaskDescriptor, SplitBrainMergeTypes.ScheduledExecutorMergeTypes> mergePolicy) {
            MergeOperation operation = new MergeOperation(name, mergingEntries, mergePolicy);
            this.invoke(DistributedScheduledExecutorService.SERVICE_NAME, operation, partitionId);
        }
    }
}

