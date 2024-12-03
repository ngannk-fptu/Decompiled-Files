/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.core.Member;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.NonFragmentedServiceNamespace;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.PartitionReplicaVersionManager;
import com.hazelcast.internal.partition.impl.InternalPartitionImpl;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.PartitionPrimaryReplicaAntiEntropyTask;
import com.hazelcast.internal.partition.impl.PartitionReplicaVersions;
import com.hazelcast.internal.partition.impl.PartitionStateManager;
import com.hazelcast.internal.partition.impl.ReplicaFragmentSyncInfo;
import com.hazelcast.internal.partition.operation.PartitionReplicaSyncRequest;
import com.hazelcast.internal.util.counters.MwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.ServiceNamespaceAware;
import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.impl.operationservice.PartitionTaskFactory;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.scheduler.EntryTaskScheduler;
import com.hazelcast.util.scheduler.EntryTaskSchedulerFactory;
import com.hazelcast.util.scheduler.ScheduleType;
import com.hazelcast.util.scheduler.ScheduledEntry;
import com.hazelcast.util.scheduler.ScheduledEntryProcessor;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class PartitionReplicaManager
implements PartitionReplicaVersionManager {
    private final Node node;
    private final NodeEngineImpl nodeEngine;
    private final ILogger logger;
    private final InternalPartitionServiceImpl partitionService;
    private final PartitionStateManager partitionStateManager;
    private final PartitionReplicaVersions[] replicaVersions;
    private final Set<ReplicaFragmentSyncInfo> replicaSyncRequests;
    private final EntryTaskScheduler<ReplicaFragmentSyncInfo, Void> replicaSyncTimeoutScheduler;
    @Probe
    private final Semaphore replicaSyncSemaphore;
    @Probe
    private final MwCounter replicaSyncRequestsCounter = MwCounter.newMwCounter();
    private final long partitionMigrationTimeout;
    private final int maxParallelReplications;

    PartitionReplicaManager(Node node, InternalPartitionServiceImpl partitionService) {
        this.node = node;
        this.nodeEngine = node.nodeEngine;
        this.logger = node.getLogger(this.getClass());
        this.partitionService = partitionService;
        int partitionCount = partitionService.getPartitionCount();
        this.partitionStateManager = partitionService.getPartitionStateManager();
        HazelcastProperties properties = node.getProperties();
        this.partitionMigrationTimeout = properties.getMillis(GroupProperty.PARTITION_MIGRATION_TIMEOUT);
        this.maxParallelReplications = properties.getInteger(GroupProperty.PARTITION_MAX_PARALLEL_REPLICATIONS);
        this.replicaSyncSemaphore = new Semaphore(this.maxParallelReplications);
        this.replicaVersions = new PartitionReplicaVersions[partitionCount];
        for (int i = 0; i < this.replicaVersions.length; ++i) {
            this.replicaVersions[i] = new PartitionReplicaVersions(i);
        }
        InternalExecutionService executionService = this.nodeEngine.getExecutionService();
        TaskScheduler globalScheduler = executionService.getGlobalTaskScheduler();
        this.replicaSyncTimeoutScheduler = EntryTaskSchedulerFactory.newScheduler(globalScheduler, new ReplicaSyncTimeoutProcessor(), ScheduleType.POSTPONE);
        this.replicaSyncRequests = Collections.newSetFromMap(new ConcurrentHashMap(partitionCount));
    }

    public void triggerPartitionReplicaSync(int partitionId, Collection<ServiceNamespace> namespaces, int replicaIndex) {
        assert (replicaIndex >= 0 && replicaIndex < 7) : "Invalid replica index! partitionId=" + partitionId + ", replicaIndex=" + replicaIndex;
        PartitionReplica target = this.checkAndGetPrimaryReplicaOwner(partitionId, replicaIndex);
        if (target == null) {
            return;
        }
        if (!this.partitionService.areMigrationTasksAllowed()) {
            this.logger.finest("Cannot send sync replica request for partitionId=" + partitionId + ", replicaIndex=" + replicaIndex + ", namespaces=" + namespaces + ". Sync is not allowed.");
            return;
        }
        InternalPartitionImpl partition = this.partitionStateManager.getPartitionImpl(partitionId);
        if (partition.isMigrating()) {
            this.logger.finest("Cannot send sync replica request for partitionId=" + partitionId + ", replicaIndex=" + replicaIndex + ", namespaces=" + namespaces + ". Partition is already migrating.");
            return;
        }
        this.sendSyncReplicaRequest(partitionId, namespaces, replicaIndex, target);
    }

    PartitionReplica checkAndGetPrimaryReplicaOwner(int partitionId, int replicaIndex) {
        InternalPartitionImpl partition = this.partitionStateManager.getPartitionImpl(partitionId);
        PartitionReplica owner = partition.getOwnerReplicaOrNull();
        if (owner == null) {
            this.logger.info("Sync replica target is null, no need to sync -> partitionId=" + partitionId + ", replicaIndex=" + replicaIndex);
            return null;
        }
        PartitionReplica localReplica = PartitionReplica.from(this.nodeEngine.getLocalMember());
        if (owner.equals(localReplica)) {
            if (this.logger.isFinestEnabled()) {
                this.logger.finest("This node is now owner of partition, cannot sync replica -> partitionId=" + partitionId + ", replicaIndex=" + replicaIndex + ", partition-info=" + this.partitionStateManager.getPartitionImpl(partitionId));
            }
            return null;
        }
        if (!partition.isOwnerOrBackup(localReplica)) {
            if (this.logger.isFinestEnabled()) {
                this.logger.finest("This node is not backup replica of partitionId=" + partitionId + ", replicaIndex=" + replicaIndex + " anymore.");
            }
            return null;
        }
        return owner;
    }

    private void sendSyncReplicaRequest(int partitionId, Collection<ServiceNamespace> requestedNamespaces, int replicaIndex, PartitionReplica target) {
        if (this.node.clusterService.isMissingMember(target.address(), target.uuid())) {
            return;
        }
        int permits = this.tryAcquireReplicaSyncPermits(requestedNamespaces.size());
        if (permits == 0) {
            if (this.logger.isFinestEnabled()) {
                this.logger.finest("Cannot send sync replica request for partitionId=" + partitionId + ", replicaIndex=" + replicaIndex + ", namespaces=" + requestedNamespaces + ". No permits available!");
            }
            return;
        }
        List<ServiceNamespace> namespaces = this.registerSyncInfoForNamespaces(partitionId, requestedNamespaces, replicaIndex, target, permits);
        if (namespaces.size() != permits) {
            this.releaseReplicaSyncPermits(permits - namespaces.size());
        }
        if (namespaces.isEmpty()) {
            return;
        }
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Sending sync replica request for partitionId=" + partitionId + ", replicaIndex=" + replicaIndex + ", namespaces=" + namespaces);
        }
        this.replicaSyncRequestsCounter.inc();
        PartitionReplicaSyncRequest syncRequest = new PartitionReplicaSyncRequest(partitionId, namespaces, replicaIndex);
        this.nodeEngine.getOperationService().send(syncRequest, target.address());
    }

    private List<ServiceNamespace> registerSyncInfoForNamespaces(int partitionId, Collection<ServiceNamespace> requestedNamespaces, int replicaIndex, PartitionReplica target, int permits) {
        ArrayList<ServiceNamespace> namespaces = new ArrayList<ServiceNamespace>(permits);
        for (ServiceNamespace namespace : requestedNamespaces) {
            if (namespaces.size() == permits) {
                if (!this.logger.isFinestEnabled()) break;
                this.logger.finest("Cannot send sync replica request for " + partitionId + ", replicaIndex=" + replicaIndex + ", namespace=" + namespace + ". No permits available!");
                continue;
            }
            if (!this.registerSyncInfoFor(partitionId, namespace, replicaIndex, target)) continue;
            namespaces.add(namespace);
        }
        return namespaces;
    }

    private boolean registerSyncInfoFor(int partitionId, ServiceNamespace namespace, int replicaIndex, PartitionReplica target) {
        ReplicaFragmentSyncInfo syncInfo = new ReplicaFragmentSyncInfo(partitionId, namespace, replicaIndex, target);
        if (!this.replicaSyncRequests.add(syncInfo)) {
            if (this.logger.isFinestEnabled()) {
                this.logger.finest("Cannot send sync replica request for " + syncInfo + ". Sync is already in progress!");
            }
            return false;
        }
        this.replicaSyncTimeoutScheduler.schedule(this.partitionMigrationTimeout, syncInfo, null);
        return true;
    }

    @Override
    public ServiceNamespace getServiceNamespace(Operation operation) {
        if (operation instanceof ServiceNamespaceAware) {
            return ((ServiceNamespaceAware)((Object)operation)).getServiceNamespace();
        }
        return NonFragmentedServiceNamespace.INSTANCE;
    }

    @Override
    public long[] incrementPartitionReplicaVersions(int partitionId, ServiceNamespace namespace, int backupCount) {
        PartitionReplicaVersions replicaVersion = this.replicaVersions[partitionId];
        return replicaVersion.incrementAndGet(namespace, backupCount);
    }

    @Override
    public void updatePartitionReplicaVersions(int partitionId, ServiceNamespace namespace, long[] versions, int replicaIndex) {
        PartitionReplicaVersions partitionVersion = this.replicaVersions[partitionId];
        if (!partitionVersion.update(namespace, versions, replicaIndex)) {
            this.triggerPartitionReplicaSync(partitionId, Collections.singleton(namespace), replicaIndex);
        }
    }

    @Override
    public boolean isPartitionReplicaVersionStale(int partitionId, ServiceNamespace namespace, long[] versions, int replicaIndex) {
        return this.replicaVersions[partitionId].isStale(namespace, versions, replicaIndex);
    }

    public boolean isPartitionReplicaVersionDirty(int partitionId, ServiceNamespace namespace) {
        return this.replicaVersions[partitionId].isDirty(namespace);
    }

    @Override
    public long[] getPartitionReplicaVersions(int partitionId, ServiceNamespace namespace) {
        return this.replicaVersions[partitionId].get(namespace);
    }

    public void setPartitionReplicaVersions(int partitionId, ServiceNamespace namespace, long[] versions, int replicaOffset) {
        this.replicaVersions[partitionId].set(namespace, versions, replicaOffset);
    }

    public void clearPartitionReplicaVersions(int partitionId, ServiceNamespace namespace) {
        this.replicaVersions[partitionId].clear(namespace);
    }

    public void finalizeReplicaSync(int partitionId, int replicaIndex, ServiceNamespace namespace, long[] versions) {
        PartitionReplicaVersions replicaVersion = this.replicaVersions[partitionId];
        replicaVersion.clear(namespace);
        replicaVersion.set(namespace, versions, replicaIndex);
        this.clearReplicaSyncRequest(partitionId, namespace, replicaIndex);
    }

    public void clearReplicaSyncRequest(int partitionId, ServiceNamespace namespace, int replicaIndex) {
        ReplicaFragmentSyncInfo syncInfo = new ReplicaFragmentSyncInfo(partitionId, namespace, replicaIndex, null);
        if (!this.replicaSyncRequests.remove(syncInfo)) {
            return;
        }
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Clearing sync replica request for partitionId=" + partitionId + ", replicaIndex=" + replicaIndex + ", namespace=" + namespace);
        }
        this.releaseReplicaSyncPermits(1);
        this.replicaSyncTimeoutScheduler.cancelIfExists(syncInfo, null);
    }

    void cancelReplicaSyncRequestsTo(Member member) {
        Iterator<ReplicaFragmentSyncInfo> iter = this.replicaSyncRequests.iterator();
        while (iter.hasNext()) {
            ReplicaFragmentSyncInfo syncInfo = iter.next();
            if (syncInfo.target == null || !syncInfo.target.isIdentical(member)) continue;
            iter.remove();
            this.replicaSyncTimeoutScheduler.cancel(syncInfo);
            this.releaseReplicaSyncPermits(1);
        }
    }

    void cancelReplicaSync(int partitionId) {
        Iterator<ReplicaFragmentSyncInfo> iter = this.replicaSyncRequests.iterator();
        while (iter.hasNext()) {
            ReplicaFragmentSyncInfo syncInfo = iter.next();
            if (syncInfo.partitionId != partitionId) continue;
            iter.remove();
            this.replicaSyncTimeoutScheduler.cancel(syncInfo);
            this.releaseReplicaSyncPermits(1);
        }
    }

    public int tryAcquireReplicaSyncPermits(int requestedPermits) {
        int permits;
        assert (requestedPermits > 0) : "Invalid permits: " + requestedPermits;
        for (permits = requestedPermits; permits > 0 && !this.replicaSyncSemaphore.tryAcquire(permits); --permits) {
        }
        if (permits > 0 && this.logger.isFinestEnabled()) {
            this.logger.finest("Acquired " + permits + " replica sync permits, requested permits was " + requestedPermits + ". Remaining permits: " + this.replicaSyncSemaphore.availablePermits());
        }
        return permits;
    }

    public void releaseReplicaSyncPermits(int permits) {
        assert (permits > 0) : "Invalid permits: " + permits;
        this.replicaSyncSemaphore.release(permits);
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Released " + permits + " replica sync permits. Available permits: " + this.replicaSyncSemaphore.availablePermits());
        }
        assert (this.availableReplicaSyncPermits() <= this.maxParallelReplications) : "Number of replica sync permits exceeded the configured number!";
    }

    public int availableReplicaSyncPermits() {
        return this.replicaSyncSemaphore.availablePermits();
    }

    List<ReplicaFragmentSyncInfo> getOngoingReplicaSyncRequests() {
        return new ArrayList<ReplicaFragmentSyncInfo>(this.replicaSyncRequests);
    }

    List<ScheduledEntry<ReplicaFragmentSyncInfo, Void>> getScheduledReplicaSyncRequests() {
        ArrayList<ScheduledEntry<ReplicaFragmentSyncInfo, Void>> entries = new ArrayList<ScheduledEntry<ReplicaFragmentSyncInfo, Void>>();
        for (ReplicaFragmentSyncInfo syncInfo : this.replicaSyncRequests) {
            ScheduledEntry<ReplicaFragmentSyncInfo, Void> entry = this.replicaSyncTimeoutScheduler.get(syncInfo);
            if (entry == null) continue;
            entries.add(entry);
        }
        return entries;
    }

    void reset() {
        this.replicaSyncRequests.clear();
        this.replicaSyncTimeoutScheduler.cancelAll();
        this.replicaSyncSemaphore.drainPermits();
        this.replicaSyncSemaphore.release(this.maxParallelReplications);
    }

    void scheduleReplicaVersionSync(ExecutionService executionService) {
        long definedBackupSyncCheckInterval = this.node.getProperties().getSeconds(GroupProperty.PARTITION_BACKUP_SYNC_INTERVAL);
        long backupSyncCheckInterval = definedBackupSyncCheckInterval > 0L ? definedBackupSyncCheckInterval : 1L;
        executionService.scheduleWithRepetition(new AntiEntropyTask(), backupSyncCheckInterval, backupSyncCheckInterval, TimeUnit.SECONDS);
    }

    @Override
    public Collection<ServiceNamespace> getNamespaces(int partitionId) {
        return this.replicaVersions[partitionId].getNamespaces();
    }

    public void retainNamespaces(int partitionId, Set<ServiceNamespace> namespaces) {
        PartitionReplicaVersions versions = this.replicaVersions[partitionId];
        versions.retainNamespaces(namespaces);
    }

    private class PartitionAntiEntropyTaskFactory
    implements PartitionTaskFactory<Runnable> {
        private PartitionAntiEntropyTaskFactory() {
        }

        @Override
        public Runnable create(int partitionId) {
            return new PartitionPrimaryReplicaAntiEntropyTask(PartitionReplicaManager.this.nodeEngine, partitionId);
        }
    }

    private class AntiEntropyTask
    implements Runnable {
        private AntiEntropyTask() {
        }

        @Override
        public void run() {
            if (!(PartitionReplicaManager.this.node.isRunning() && PartitionReplicaManager.this.node.getNodeExtension().isStartCompleted() && PartitionReplicaManager.this.partitionService.areMigrationTasksAllowed())) {
                return;
            }
            PartitionReplicaManager.this.nodeEngine.getOperationService().executeOnPartitions(new PartitionAntiEntropyTaskFactory(), this.getLocalPartitions());
        }

        private BitSet getLocalPartitions() {
            BitSet localPartitions = new BitSet(PartitionReplicaManager.this.partitionService.getPartitionCount());
            for (InternalPartition partition : PartitionReplicaManager.this.partitionService.getInternalPartitions()) {
                if (!partition.isLocal()) continue;
                localPartitions.set(partition.getPartitionId());
            }
            return localPartitions;
        }
    }

    private class ReplicaSyncTimeoutProcessor
    implements ScheduledEntryProcessor<ReplicaFragmentSyncInfo, Void> {
        private ReplicaSyncTimeoutProcessor() {
        }

        @Override
        public void process(EntryTaskScheduler<ReplicaFragmentSyncInfo, Void> scheduler, Collection<ScheduledEntry<ReplicaFragmentSyncInfo, Void>> entries) {
            for (ScheduledEntry<ReplicaFragmentSyncInfo, Void> entry : entries) {
                ReplicaFragmentSyncInfo syncInfo = entry.getKey();
                if (!PartitionReplicaManager.this.replicaSyncRequests.remove(syncInfo)) continue;
                PartitionReplicaManager.this.releaseReplicaSyncPermits(1);
            }
        }
    }
}

