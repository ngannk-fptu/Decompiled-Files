/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.core.MigrationListener;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.ClusterStateListener;
import com.hazelcast.internal.cluster.ClusterVersionListener;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.operations.TriggerMemberListPublishOp;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.partition.PartitionListener;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.PartitionReplicaVersionManager;
import com.hazelcast.internal.partition.PartitionRuntimeState;
import com.hazelcast.internal.partition.PartitionServiceProxy;
import com.hazelcast.internal.partition.PartitionTableView;
import com.hazelcast.internal.partition.impl.InternalMigrationListener;
import com.hazelcast.internal.partition.impl.InternalPartitionImpl;
import com.hazelcast.internal.partition.impl.InternalPartitionListener;
import com.hazelcast.internal.partition.impl.MigrationManager;
import com.hazelcast.internal.partition.impl.MigrationRunnable;
import com.hazelcast.internal.partition.impl.PartitionEventManager;
import com.hazelcast.internal.partition.impl.PartitionReplicaManager;
import com.hazelcast.internal.partition.impl.PartitionReplicaStateChecker;
import com.hazelcast.internal.partition.impl.PartitionServiceState;
import com.hazelcast.internal.partition.impl.PartitionStateManager;
import com.hazelcast.internal.partition.impl.PublishPartitionRuntimeStateTask;
import com.hazelcast.internal.partition.impl.ReplicaFragmentSyncInfo;
import com.hazelcast.internal.partition.operation.AssignPartitions;
import com.hazelcast.internal.partition.operation.FetchPartitionStateOperation;
import com.hazelcast.internal.partition.operation.PartitionStateOperation;
import com.hazelcast.internal.partition.operation.PartitionStateVersionCheckOperation;
import com.hazelcast.internal.partition.operation.ShutdownRequestOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.partition.NoDataMemberInClusterException;
import com.hazelcast.partition.PartitionEvent;
import com.hazelcast.partition.PartitionEventListener;
import com.hazelcast.partition.PartitionLostListener;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.PartitionAwareService;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationexecutor.OperationExecutor;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.partition.IPartitionLostEvent;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.HashUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.scheduler.CoalescingDelayedTrigger;
import com.hazelcast.util.scheduler.ScheduledEntry;
import com.hazelcast.version.Version;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class InternalPartitionServiceImpl
implements InternalPartitionService,
EventPublishingService<PartitionEvent, PartitionEventListener<PartitionEvent>>,
PartitionAwareService,
ClusterStateListener,
ClusterVersionListener {
    private static final int PARTITION_OWNERSHIP_WAIT_MILLIS = 10;
    private static final String EXCEPTION_MSG_PARTITION_STATE_SYNC_TIMEOUT = "Partition state sync invocation timed out";
    private static final int PTABLE_SYNC_TIMEOUT_SECONDS = 10;
    private static final int SAFE_SHUTDOWN_MAX_AWAIT_STEP_MILLIS = 1000;
    private static final long FETCH_PARTITION_STATE_SECONDS = 5L;
    private static final long TRIGGER_MASTER_DELAY_MILLIS = 1000L;
    private final Node node;
    private final NodeEngineImpl nodeEngine;
    private final ILogger logger;
    private final int partitionCount;
    private final long partitionMigrationTimeout;
    private final PartitionServiceProxy proxy;
    private final Lock lock = new ReentrantLock();
    private final InternalPartitionListener partitionListener;
    private final PartitionStateManager partitionStateManager;
    private final MigrationManager migrationManager;
    private final PartitionReplicaManager replicaManager;
    private final PartitionReplicaStateChecker partitionReplicaStateChecker;
    private final PartitionEventManager partitionEventManager;
    private final FutureUtil.ExceptionHandler partitionStateSyncTimeoutHandler;
    private final AtomicBoolean masterTriggered = new AtomicBoolean(false);
    private final CoalescingDelayedTrigger masterTrigger;
    private final AtomicReference<CountDownLatch> shutdownLatchRef = new AtomicReference();
    private volatile Address latestMaster;
    private volatile boolean shouldFetchPartitionTables;

    public InternalPartitionServiceImpl(Node node) {
        HazelcastProperties properties = node.getProperties();
        this.partitionCount = properties.getInteger(GroupProperty.PARTITION_COUNT);
        this.node = node;
        this.nodeEngine = node.nodeEngine;
        this.logger = node.getLogger(InternalPartitionService.class);
        this.partitionListener = new InternalPartitionListener(node, this);
        this.partitionStateManager = new PartitionStateManager(node, this, this.partitionListener);
        this.migrationManager = new MigrationManager(node, this, this.lock);
        this.replicaManager = new PartitionReplicaManager(node, this);
        this.partitionReplicaStateChecker = new PartitionReplicaStateChecker(node, this);
        this.partitionEventManager = new PartitionEventManager(node);
        this.masterTrigger = new CoalescingDelayedTrigger(this.nodeEngine.getExecutionService(), 1000L, 2000L, new Runnable(){

            @Override
            public void run() {
                InternalPartitionServiceImpl.this.resetMasterTriggeredFlag();
            }
        });
        this.partitionStateSyncTimeoutHandler = FutureUtil.logAllExceptions(this.logger, EXCEPTION_MSG_PARTITION_STATE_SYNC_TIMEOUT, Level.FINEST);
        this.partitionMigrationTimeout = properties.getMillis(GroupProperty.PARTITION_MIGRATION_TIMEOUT);
        this.proxy = new PartitionServiceProxy(this.nodeEngine, this);
        MetricsRegistry metricsRegistry = this.nodeEngine.getMetricsRegistry();
        metricsRegistry.scanAndRegister(this, "partitions");
        metricsRegistry.scanAndRegister(this.partitionStateManager, "partitions");
        metricsRegistry.scanAndRegister(this.migrationManager, "partitions");
        metricsRegistry.scanAndRegister(this.replicaManager, "partitions");
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        int partitionTableSendInterval = this.node.getProperties().getSeconds(GroupProperty.PARTITION_TABLE_SEND_INTERVAL);
        if (partitionTableSendInterval <= 0) {
            partitionTableSendInterval = 1;
        }
        ExecutionService executionService = nodeEngine.getExecutionService();
        executionService.scheduleWithRepetition(new PublishPartitionRuntimeStateTask(this.node, this), partitionTableSendInterval, partitionTableSendInterval, TimeUnit.SECONDS);
        this.migrationManager.start();
        this.replicaManager.scheduleReplicaVersionSync(executionService);
    }

    @Override
    public Address getPartitionOwner(int partitionId) {
        InternalPartitionImpl partition;
        if (!this.partitionStateManager.isInitialized()) {
            this.firstArrangement();
        }
        if ((partition = this.partitionStateManager.getPartitionImpl(partitionId)).getOwnerReplicaOrNull() == null && !this.node.isMaster() && !this.isClusterFormedByOnlyLiteMembers()) {
            this.triggerMasterToAssignPartitions();
        }
        return partition.getOwnerOrNull();
    }

    @Override
    public Address getPartitionOwnerOrWait(int partitionId) {
        Address owner;
        while ((owner = this.getPartitionOwner(partitionId)) == null) {
            if (!this.nodeEngine.isRunning()) {
                throw new HazelcastInstanceNotActiveException();
            }
            ClusterState clusterState = this.node.getClusterService().getClusterState();
            if (!clusterState.isMigrationAllowed()) {
                throw new IllegalStateException("Partitions can't be assigned since cluster-state: " + (Object)((Object)clusterState));
            }
            if (this.isClusterFormedByOnlyLiteMembers()) {
                throw new NoDataMemberInClusterException("Partitions can't be assigned since all nodes in the cluster are lite members");
            }
            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw ExceptionUtil.rethrow(e);
            }
        }
        return owner;
    }

    @Override
    public PartitionRuntimeState firstArrangement() {
        if (!this.isLocalMemberMaster()) {
            this.triggerMasterToAssignPartitions();
            return null;
        }
        this.lock.lock();
        try {
            Set<Member> excludedMembers;
            if (!this.partitionStateManager.isInitialized() && this.partitionStateManager.initializePartitionAssignments(excludedMembers = this.migrationManager.getShutdownRequestedMembers())) {
                this.publishPartitionRuntimeState();
            }
            PartitionRuntimeState partitionRuntimeState = this.createPartitionStateInternal();
            return partitionRuntimeState;
        }
        finally {
            this.lock.unlock();
        }
    }

    private void triggerMasterToAssignPartitions() {
        if (!this.shouldTriggerMasterToAssignPartitions()) {
            return;
        }
        ClusterServiceImpl clusterService = this.node.getClusterService();
        ClusterState clusterState = clusterService.getClusterState();
        if (!clusterState.isMigrationAllowed()) {
            this.logger.warning("Partitions can't be assigned since cluster-state=" + (Object)((Object)clusterState));
            return;
        }
        final Address masterAddress = this.latestMaster;
        if (masterAddress == null || masterAddress.equals(this.node.getThisAddress())) {
            return;
        }
        if (this.masterTriggered.compareAndSet(false, true)) {
            InternalOperationService operationService = this.nodeEngine.getOperationService();
            InternalCompletableFuture future = operationService.invokeOnTarget("hz:core:partitionService", new AssignPartitions(), masterAddress);
            future.andThen(new ExecutionCallback<PartitionRuntimeState>(){

                @Override
                public void onResponse(PartitionRuntimeState partitionState) {
                    InternalPartitionServiceImpl.this.resetMasterTriggeredFlag();
                    if (partitionState != null) {
                        partitionState.setMaster(masterAddress);
                        InternalPartitionServiceImpl.this.processPartitionRuntimeState(partitionState);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    InternalPartitionServiceImpl.this.resetMasterTriggeredFlag();
                    InternalPartitionServiceImpl.this.logger.severe(t);
                }
            });
            this.masterTrigger.executeWithDelay();
        }
    }

    private boolean shouldTriggerMasterToAssignPartitions() {
        ClusterServiceImpl clusterService = this.node.getClusterService();
        return !this.partitionStateManager.isInitialized() && clusterService.isJoined() && this.node.getNodeExtension().isStartCompleted();
    }

    private void resetMasterTriggeredFlag() {
        this.masterTriggered.set(false);
    }

    private boolean isClusterFormedByOnlyLiteMembers() {
        ClusterServiceImpl clusterService = this.node.getClusterService();
        return clusterService.getMembers(MemberSelectors.DATA_MEMBER_SELECTOR).isEmpty();
    }

    public void setInitialState(PartitionTableView partitionTable) {
        this.lock.lock();
        try {
            this.partitionStateManager.setInitialState(partitionTable);
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public int getMemberGroupsSize() {
        return this.partitionStateManager.getMemberGroupsSize();
    }

    @Override
    @Probe(name="maxBackupCount")
    public int getMaxAllowedBackupCount() {
        return Math.max(Math.min(this.getMemberGroupsSize() - 1, 6), 0);
    }

    public void updateMemberGroupSize() {
        this.partitionStateManager.updateMemberGroupsSize();
    }

    @Override
    public void memberAdded(Member member) {
        this.logger.fine("Adding " + member);
        this.lock.lock();
        try {
            this.latestMaster = this.node.getClusterService().getMasterAddress();
            if (!member.localMember()) {
                this.partitionStateManager.updateMemberGroupsSize();
            }
            if (this.isLocalMemberMaster() && this.partitionStateManager.isInitialized()) {
                this.migrationManager.triggerControlTask();
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void memberRemoved(Member member) {
        this.logger.fine("Removing " + member);
        this.lock.lock();
        try {
            this.migrationManager.onMemberRemove(member);
            this.replicaManager.cancelReplicaSyncRequestsTo(member);
            Address formerMaster = this.latestMaster;
            this.latestMaster = this.node.getClusterService().getMasterAddress();
            ClusterState clusterState = this.node.getClusterService().getClusterState();
            if (clusterState.isMigrationAllowed() || clusterState.isPartitionPromotionAllowed()) {
                boolean isThisNodeNewMaster;
                this.partitionStateManager.updateMemberGroupsSize();
                boolean isMaster = this.node.isMaster();
                boolean bl = isThisNodeNewMaster = isMaster && !this.node.getThisAddress().equals(formerMaster);
                if (isThisNodeNewMaster) {
                    assert (!this.shouldFetchPartitionTables);
                    this.shouldFetchPartitionTables = true;
                }
                if (isMaster) {
                    this.migrationManager.triggerControlTask();
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public void onClusterStateChange(ClusterState newState) {
        if (!newState.isMigrationAllowed()) {
            return;
        }
        if (!this.partitionStateManager.isInitialized()) {
            return;
        }
        if (!this.isLocalMemberMaster()) {
            return;
        }
        this.lock.lock();
        try {
            if (this.partitionStateManager.isInitialized() && this.migrationManager.shouldTriggerRepartitioningWhenClusterStateAllowsMigration()) {
                this.migrationManager.triggerControlTask();
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onClusterVersionChange(Version newVersion) {
        if (newVersion.isEqualTo(Versions.V3_12)) {
            this.lock.lock();
            try {
                if (!this.partitionStateManager.isInitialized()) {
                    return;
                }
                for (int pid = 0; pid < this.getPartitionCount(); ++pid) {
                    PartitionReplica[] replicaMembers;
                    InternalPartitionImpl partition = this.partitionStateManager.getPartitionImpl(pid);
                    for (PartitionReplica member : replicaMembers = partition.getReplicas()) {
                        if (member == null || !"<unknown-uuid>".equals(member.uuid())) continue;
                        throw new IllegalStateException("Unknown uuid: " + member);
                    }
                }
                this.migrationManager.onClusterVersionChange(newVersion);
            }
            finally {
                this.lock.unlock();
            }
        }
    }

    @Override
    public PartitionRuntimeState createPartitionState() {
        if (!this.isFetchMostRecentPartitionTableTaskRequired()) {
            return this.createPartitionStateInternal();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PartitionRuntimeState createPartitionStateInternal() {
        this.lock.lock();
        try {
            if (!this.partitionStateManager.isInitialized()) {
                PartitionRuntimeState partitionRuntimeState = null;
                return partitionRuntimeState;
            }
            List<MigrationInfo> completedMigrations = this.migrationManager.getCompletedMigrationsCopy();
            InternalPartition[] partitions = this.partitionStateManager.getPartitions();
            PartitionRuntimeState state = new PartitionRuntimeState(partitions, completedMigrations, this.getPartitionStateVersion());
            state.setActiveMigration(this.migrationManager.getActiveMigration());
            PartitionRuntimeState partitionRuntimeState = state;
            return partitionRuntimeState;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    PartitionRuntimeState createMigrationCommitPartitionState(MigrationInfo migrationInfo) {
        this.lock.lock();
        try {
            if (!this.partitionStateManager.isInitialized()) {
                PartitionRuntimeState partitionRuntimeState = null;
                return partitionRuntimeState;
            }
            List<MigrationInfo> completedMigrations = this.migrationManager.getCompletedMigrationsCopy();
            InternalPartition[] partitions = this.partitionStateManager.getPartitionsCopy();
            int partitionId = migrationInfo.getPartitionId();
            InternalPartitionImpl partition = (InternalPartitionImpl)partitions[partitionId];
            MigrationManager.applyMigration(partition, migrationInfo);
            migrationInfo.setStatus(MigrationInfo.MigrationStatus.SUCCESS);
            completedMigrations.add(migrationInfo);
            int committedVersion = this.getPartitionStateVersion() + 1;
            PartitionRuntimeState partitionRuntimeState = new PartitionRuntimeState(partitions, completedMigrations, committedVersion);
            return partitionRuntimeState;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    PartitionRuntimeState createPromotionCommitPartitionState(Collection<MigrationInfo> migrationInfos) {
        this.lock.lock();
        try {
            if (!this.partitionStateManager.isInitialized()) {
                PartitionRuntimeState partitionRuntimeState = null;
                return partitionRuntimeState;
            }
            List<MigrationInfo> completedMigrations = this.migrationManager.getCompletedMigrationsCopy();
            InternalPartition[] partitions = this.partitionStateManager.getPartitionsCopy();
            for (MigrationInfo migrationInfo : migrationInfos) {
                int partitionId = migrationInfo.getPartitionId();
                InternalPartitionImpl partition = (InternalPartitionImpl)partitions[partitionId];
                MigrationManager.applyMigration(partition, migrationInfo);
                migrationInfo.setStatus(MigrationInfo.MigrationStatus.SUCCESS);
            }
            int committedVersion = this.getPartitionStateVersion() + migrationInfos.size() * 2;
            PartitionRuntimeState partitionRuntimeState = new PartitionRuntimeState(partitions, completedMigrations, committedVersion);
            return partitionRuntimeState;
        }
        finally {
            this.lock.unlock();
        }
    }

    void publishPartitionRuntimeState() {
        if (!this.partitionStateManager.isInitialized()) {
            return;
        }
        if (!this.isLocalMemberMaster()) {
            return;
        }
        if (!this.areMigrationTasksAllowed()) {
            return;
        }
        PartitionRuntimeState partitionState = this.createPartitionStateInternal();
        if (partitionState == null) {
            return;
        }
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Publishing partition state, version: " + partitionState.getVersion());
        }
        PartitionStateOperation op = new PartitionStateOperation(partitionState, false);
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        Set<Member> members = this.node.clusterService.getMembers();
        for (Member member : members) {
            if (member.localMember()) continue;
            try {
                operationService.send(op, member.getAddress());
            }
            catch (Exception e) {
                this.logger.finest(e);
            }
        }
    }

    void sendPartitionRuntimeState(Address target) {
        if (!this.isLocalMemberMaster()) {
            return;
        }
        assert (this.partitionStateManager.isInitialized());
        assert (this.areMigrationTasksAllowed());
        PartitionRuntimeState partitionState = this.createPartitionStateInternal();
        assert (partitionState != null);
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Sending partition state, version: " + partitionState.getVersion() + ", to " + target);
        }
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        PartitionStateOperation op = new PartitionStateOperation(partitionState, true);
        operationService.invokeOnTarget("hz:core:partitionService", op, target);
    }

    void checkClusterPartitionRuntimeStates() {
        if (!this.partitionStateManager.isInitialized()) {
            return;
        }
        if (!this.isLocalMemberMaster()) {
            return;
        }
        if (!this.areMigrationTasksAllowed()) {
            return;
        }
        int partitionStateVersion = this.getPartitionStateVersion();
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Checking partition state, version: " + partitionStateVersion);
        }
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        Set<Member> members = this.node.clusterService.getMembers();
        for (final Member member : members) {
            if (member.localMember()) continue;
            PartitionStateVersionCheckOperation op = new PartitionStateVersionCheckOperation(partitionStateVersion);
            InternalCompletableFuture future = operationService.invokeOnTarget("hz:core:partitionService", op, member.getAddress());
            future.andThen(new ExecutionCallback<Boolean>(){

                @Override
                public void onResponse(Boolean response) {
                    if (!Boolean.TRUE.equals(response)) {
                        InternalPartitionServiceImpl.this.logger.fine(member + " has a stale partition state. Will send the most recent partition state now.");
                        InternalPartitionServiceImpl.this.sendPartitionRuntimeState(member.getAddress());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    InternalPartitionServiceImpl.this.logger.fine("Failure while checking partition state on " + member, t);
                    InternalPartitionServiceImpl.this.sendPartitionRuntimeState(member.getAddress());
                }
            });
        }
    }

    boolean syncPartitionRuntimeState() {
        assert (!((ReentrantLock)this.lock).isHeldByCurrentThread());
        assert (this.partitionStateManager.isInitialized());
        assert (this.node.isMaster());
        PartitionRuntimeState partitionState = this.createPartitionStateInternal();
        assert (partitionState != null);
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Sync'ing partition state, version: " + partitionState.getVersion());
        }
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        Set<Member> members = this.node.clusterService.getMembers();
        ArrayList futures = new ArrayList(members.size());
        for (Member member : members) {
            if (member.localMember()) continue;
            PartitionStateOperation op = new PartitionStateOperation(partitionState, true);
            InternalCompletableFuture future = operationService.invokeOnTarget("hz:core:partitionService", op, member.getAddress());
            futures.add(future);
        }
        Collection<Boolean> results = FutureUtil.returnWithDeadline(futures, 10L, TimeUnit.SECONDS, this.partitionStateSyncTimeoutHandler);
        if (futures.size() != results.size()) {
            return false;
        }
        for (Boolean result : results) {
            if (result.booleanValue()) continue;
            return false;
        }
        return true;
    }

    public boolean processPartitionRuntimeState(PartitionRuntimeState partitionState) {
        Address sender = partitionState.getMaster();
        if (!this.node.getNodeExtension().isStartCompleted()) {
            this.logger.warning("Ignoring received partition table, startup is not completed yet. Sender: " + sender);
            return false;
        }
        if (!this.validateSenderIsMaster(sender, "partition table update")) {
            return false;
        }
        return this.applyNewPartitionTable(partitionState.getPartitionTable(), partitionState.getVersion(), partitionState.getCompletedMigrations(), sender);
    }

    private boolean validateSenderIsMaster(Address sender, String messageType) {
        Address thisAddress = this.node.getThisAddress();
        if (thisAddress.equals(this.latestMaster) && !thisAddress.equals(sender)) {
            this.logger.warning("This is the master node and received " + messageType + " from " + sender + ". Ignoring incoming state! ");
            return false;
        }
        if (!this.isMemberMaster(sender)) {
            if (this.node.clusterService.getMember(sender) == null) {
                this.logger.severe("Received " + messageType + " from an unknown member! => Sender: " + sender + "! ");
            } else {
                this.logger.warning("Received " + messageType + ", but its sender doesn't seem to be master! => Sender: " + sender + "! (Ignore if master node has changed recently.)");
            }
            return false;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean applyNewPartitionTable(PartitionReplica[][] partitionTable, int newVersion, Collection<MigrationInfo> completedMigrations, Address sender) {
        try {
            if (!this.lock.tryLock(10L, TimeUnit.SECONDS)) {
                return false;
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        try {
            int currentVersion = this.partitionStateManager.getVersion();
            if (newVersion < currentVersion) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Already applied partition state change. Local version: " + currentVersion + ", Master version: " + newVersion + " Master: " + sender);
                }
                boolean bl = false;
                return bl;
            }
            if (newVersion == currentVersion) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Already applied partition state change. Version: " + currentVersion + ", Master: " + sender);
                }
                boolean bl = true;
                return bl;
            }
            this.updatePartitionTableReplicasForCompatibility(partitionTable);
            this.requestMemberListUpdateIfUnknownMembersFound(sender, partitionTable);
            this.updatePartitionsAndFinalizeMigrations(partitionTable, newVersion, completedMigrations);
            boolean bl = true;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    private void updatePartitionTableReplicasForCompatibility(PartitionReplica[][] partitionTable) {
        ClusterServiceImpl clusterService = this.node.getClusterService();
        Version version = clusterService.getClusterVersion();
        boolean compatibilityMode = version.isLessThan(Versions.V3_12);
        for (int partitionId = 0; partitionId < this.partitionCount; ++partitionId) {
            PartitionReplica[] replicas = partitionTable[partitionId];
            if (compatibilityMode) {
                for (int ix = 0; ix < replicas.length; ++ix) {
                    Address address;
                    MemberImpl member;
                    if (replicas[ix] == null || !"<unknown-uuid>".equals(replicas[ix].uuid()) || (member = clusterService.getMember(address = replicas[ix].address())) == null) continue;
                    replicas[ix] = PartitionReplica.from(member);
                }
                continue;
            }
            for (PartitionReplica replica : replicas) {
                assert (replica == null || !"<unknown-uuid>".equals(replica.uuid())) : "Invalid replica: " + replica;
            }
        }
    }

    private void requestMemberListUpdateIfUnknownMembersFound(Address sender, PartitionReplica[][] partitionTable) {
        ClusterServiceImpl clusterService = this.node.clusterService;
        ClusterState clusterState = clusterService.getClusterState();
        HashSet<PartitionReplica> unknownReplicas = new HashSet<PartitionReplica>();
        for (PartitionReplica[] replicas : partitionTable) {
            for (int index = 0; index < 7; ++index) {
                PartitionReplica replica = replicas[index];
                if (replica == null || this.node.clusterService.getMember(replica.address(), replica.uuid()) != null || !clusterState.isJoinAllowed() && clusterService.isMissingMember(replica.address(), replica.uuid())) continue;
                unknownReplicas.add(replica);
            }
        }
        if (!unknownReplicas.isEmpty()) {
            Address masterAddress;
            if (this.logger.isWarningEnabled()) {
                StringBuilder s = new StringBuilder("Following unknown addresses are found in partition table").append(" sent from master[").append(sender).append("].").append(" (Probably they have recently joined or left the cluster.)").append(" {");
                for (PartitionReplica replica : unknownReplicas) {
                    s.append("\n\t").append(replica);
                }
                s.append("\n}");
                this.logger.warning(s.toString());
            }
            if ((masterAddress = this.node.getClusterService().getMasterAddress()) != null && !masterAddress.equals(this.node.getThisAddress())) {
                this.nodeEngine.getOperationService().send(new TriggerMemberListPublishOp(), masterAddress);
            }
        }
    }

    private void updatePartitionsAndFinalizeMigrations(PartitionReplica[][] partitionTable, int version, Collection<MigrationInfo> completedMigrations) {
        for (int partitionId = 0; partitionId < this.partitionCount; ++partitionId) {
            PartitionReplica[] replicas = partitionTable[partitionId];
            this.partitionStateManager.updateReplicas(partitionId, replicas);
        }
        this.partitionStateManager.setVersion(version);
        for (MigrationInfo migration : completedMigrations) {
            boolean added = this.migrationManager.addCompletedMigration(migration);
            if (!added) continue;
            this.migrationManager.scheduleActiveMigrationFinalization(migration);
        }
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Applied partition state update with version: " + version);
        }
        this.migrationManager.retainCompletedMigrations(completedMigrations);
        if (!this.partitionStateManager.setInitialized()) {
            this.node.getNodeExtension().onPartitionStateChange();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean applyCompletedMigrations(Collection<MigrationInfo> migrations, Address sender) {
        if (!this.validateSenderIsMaster(sender, "completed migrations")) {
            return false;
        }
        this.lock.lock();
        try {
            if (!this.partitionStateManager.isInitialized()) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Cannot apply completed migrations until partition table is initialized. Completed migrations: " + migrations);
                }
                boolean bl = false;
                return bl;
            }
            boolean appliedAllMigrations = true;
            for (MigrationInfo migration : migrations) {
                int currentVersion = this.partitionStateManager.getVersion();
                if (migration.getFinalPartitionVersion() <= currentVersion) {
                    if (!this.logger.isFinestEnabled()) continue;
                    this.logger.finest("Already applied migration commit. Local version: " + currentVersion + ", Commit version: " + migration.getFinalPartitionVersion() + " Master: " + sender);
                    continue;
                }
                if (migration.getInitialPartitionVersion() != currentVersion) {
                    this.logger.fine("Cannot apply migration commit! Expected version: " + migration.getInitialPartitionVersion() + ", current version: " + currentVersion + ", final version: " + migration.getFinalPartitionVersion() + ", Master: " + sender);
                    appliedAllMigrations = false;
                    break;
                }
                boolean added = this.migrationManager.addCompletedMigration(migration);
                assert (added) : "Migration: " + migration;
                this.partitionStateManager.incrementVersion(migration.getPartitionVersionIncrement());
                if (migration.getStatus() == MigrationInfo.MigrationStatus.SUCCESS) {
                    if (this.logger.isFineEnabled()) {
                        this.logger.fine("Applying completed migration " + migration);
                    }
                    InternalPartitionImpl partition = this.partitionStateManager.getPartitionImpl(migration.getPartitionId());
                    MigrationManager.applyMigration(partition, migration);
                }
                this.migrationManager.scheduleActiveMigrationFinalization(migration);
            }
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Applied completed migrations with partition state version: " + this.partitionStateManager.getVersion());
            }
            this.migrationManager.retainCompletedMigrations(migrations);
            this.node.getNodeExtension().onPartitionStateChange();
            boolean bl = appliedAllMigrations;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public IPartition[] getPartitions() {
        IPartition[] result = new IPartition[this.partitionCount];
        System.arraycopy(this.partitionStateManager.getPartitions(), 0, result, 0, this.partitionCount);
        return result;
    }

    @Override
    public InternalPartition[] getInternalPartitions() {
        return this.partitionStateManager.getPartitions();
    }

    @Override
    public InternalPartition getPartition(int partitionId) {
        return this.getPartition(partitionId, true);
    }

    @Override
    public InternalPartition getPartition(int partitionId, boolean triggerOwnerAssignment) {
        InternalPartitionImpl p = this.partitionStateManager.getPartitionImpl(partitionId);
        if (triggerOwnerAssignment && p.getOwnerReplicaOrNull() == null) {
            this.getPartitionOwner(partitionId);
        }
        return p;
    }

    @Override
    public boolean onShutdown(long timeout, TimeUnit unit) {
        if (!this.node.getClusterService().isJoined()) {
            return true;
        }
        if (this.node.isLiteMember()) {
            return true;
        }
        CountDownLatch latch = this.getShutdownLatch();
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        long timeoutMillis = unit.toMillis(timeout);
        long awaitStep = Math.min(1000L, timeoutMillis);
        try {
            do {
                Address masterAddress;
                if ((masterAddress = this.nodeEngine.getMasterAddress()) == null) {
                    this.logger.warning("Safe shutdown failed, master member is not known!");
                    return false;
                }
                if (this.node.getThisAddress().equals(masterAddress)) {
                    this.onShutdownRequest(this.node.getLocalMember());
                } else {
                    operationService.send(new ShutdownRequestOperation(), masterAddress);
                }
                if (!latch.await(awaitStep, TimeUnit.MILLISECONDS)) continue;
                return true;
            } while ((timeoutMillis -= awaitStep) > 0L);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.logger.info("Safe shutdown is interrupted!");
        }
        return false;
    }

    private CountDownLatch getShutdownLatch() {
        CountDownLatch latch = this.shutdownLatchRef.get();
        if (latch == null && !this.shutdownLatchRef.compareAndSet(null, latch = new CountDownLatch(1))) {
            latch = this.shutdownLatchRef.get();
        }
        return latch;
    }

    public void onShutdownRequest(Member member) {
        if (this.lock.tryLock()) {
            try {
                this.migrationManager.onShutdownRequest(member);
            }
            finally {
                this.lock.unlock();
            }
        }
    }

    public void onShutdownResponse() {
        CountDownLatch latch = this.shutdownLatchRef.get();
        assert (latch != null);
        latch.countDown();
    }

    @Override
    public boolean isMemberStateSafe() {
        return this.partitionReplicaStateChecker.getPartitionServiceState() == PartitionServiceState.SAFE;
    }

    @Override
    public boolean hasOnGoingMigration() {
        return this.hasOnGoingMigrationLocal() || !this.isLocalMemberMaster() && this.partitionReplicaStateChecker.hasOnGoingMigrationMaster(Level.FINEST);
    }

    @Override
    public boolean hasOnGoingMigrationLocal() {
        return this.migrationManager.hasOnGoingMigration();
    }

    @Override
    public final int getPartitionId(Data key) {
        return HashUtil.hashToIndex(key.getPartitionHash(), this.partitionCount);
    }

    @Override
    public final int getPartitionId(Object key) {
        return this.getPartitionId(this.nodeEngine.toData(key));
    }

    @Override
    public final int getPartitionCount() {
        return this.partitionCount;
    }

    public long getPartitionMigrationTimeout() {
        return this.partitionMigrationTimeout;
    }

    @Override
    public PartitionReplicaVersionManager getPartitionReplicaVersionManager() {
        return this.replicaManager;
    }

    @Override
    public Map<Address, List<Integer>> getMemberPartitionsMap() {
        Collection<Member> dataMembers = this.node.getClusterService().getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
        int dataMembersSize = dataMembers.size();
        int partitionsPerMember = dataMembersSize > 0 ? (int)Math.ceil((float)this.partitionCount / (float)dataMembersSize) : 0;
        Map<Address, List<Integer>> memberPartitions = MapUtil.createHashMap(dataMembersSize);
        for (int partitionId = 0; partitionId < this.partitionCount; ++partitionId) {
            Address owner = this.getPartitionOwnerOrWait(partitionId);
            List<Integer> ownedPartitions = memberPartitions.get(owner);
            if (ownedPartitions == null) {
                ownedPartitions = new ArrayList<Integer>(partitionsPerMember);
                memberPartitions.put(owner, ownedPartitions);
            }
            ownedPartitions.add(partitionId);
        }
        return memberPartitions;
    }

    @Override
    public List<Integer> getMemberPartitions(Address target) {
        LinkedList<Integer> ownedPartitions = new LinkedList<Integer>();
        for (int i = 0; i < this.partitionCount; ++i) {
            Address owner = this.getPartitionOwner(i);
            if (!target.equals(owner)) continue;
            ownedPartitions.add(i);
        }
        return ownedPartitions;
    }

    @Override
    public List<Integer> getMemberPartitionsIfAssigned(Address target) {
        if (!this.partitionStateManager.isInitialized()) {
            return Collections.emptyList();
        }
        return this.getMemberPartitions(target);
    }

    @Override
    public void reset() {
        this.lock.lock();
        try {
            this.shouldFetchPartitionTables = false;
            this.replicaManager.reset();
            this.partitionStateManager.reset();
            this.migrationManager.reset();
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public void pauseMigration() {
        this.migrationManager.pauseMigration();
    }

    @Override
    public void resumeMigration() {
        this.migrationManager.resumeMigration();
    }

    public boolean areMigrationTasksAllowed() {
        return this.migrationManager.areMigrationTasksAllowed();
    }

    @Override
    public void shutdown(boolean terminate) {
        this.logger.finest("Shutting down the partition service");
        this.migrationManager.stop();
        this.reset();
    }

    @Override
    @Probe
    public long getMigrationQueueSize() {
        return this.migrationManager.getMigrationQueueSize();
    }

    @Override
    public PartitionServiceProxy getPartitionServiceProxy() {
        return this.proxy;
    }

    @Override
    public String addMigrationListener(MigrationListener listener) {
        return this.partitionEventManager.addMigrationListener(listener);
    }

    @Override
    public boolean removeMigrationListener(String registrationId) {
        return this.partitionEventManager.removeMigrationListener(registrationId);
    }

    @Override
    public String addPartitionLostListener(PartitionLostListener listener) {
        return this.partitionEventManager.addPartitionLostListener(listener);
    }

    @Override
    public String addLocalPartitionLostListener(PartitionLostListener listener) {
        return this.partitionEventManager.addLocalPartitionLostListener(listener);
    }

    @Override
    public boolean removePartitionLostListener(String registrationId) {
        return this.partitionEventManager.removePartitionLostListener(registrationId);
    }

    @Override
    public void dispatchEvent(PartitionEvent partitionEvent, PartitionEventListener partitionEventListener) {
        partitionEventListener.onEvent(partitionEvent);
    }

    public void addPartitionListener(PartitionListener listener) {
        this.lock.lock();
        try {
            this.partitionListener.addChildListener(listener);
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean isPartitionOwner(int partitionId) {
        InternalPartitionImpl partition = this.partitionStateManager.getPartitionImpl(partitionId);
        return partition.isLocal();
    }

    @Override
    public int getPartitionStateVersion() {
        return this.partitionStateManager.getVersion();
    }

    @Override
    public void onPartitionLost(IPartitionLostEvent event) {
        this.partitionEventManager.onPartitionLost(event);
    }

    public void setInternalMigrationListener(InternalMigrationListener listener) {
        this.migrationManager.setInternalMigrationListener(listener);
    }

    public InternalMigrationListener getInternalMigrationListener() {
        return this.migrationManager.getInternalMigrationListener();
    }

    public void resetInternalMigrationListener() {
        this.migrationManager.resetInternalMigrationListener();
    }

    public List<ReplicaFragmentSyncInfo> getOngoingReplicaSyncRequests() {
        return this.replicaManager.getOngoingReplicaSyncRequests();
    }

    public List<ScheduledEntry<ReplicaFragmentSyncInfo, Void>> getScheduledReplicaSyncRequests() {
        return this.replicaManager.getScheduledReplicaSyncRequests();
    }

    public PartitionStateManager getPartitionStateManager() {
        return this.partitionStateManager;
    }

    public MigrationManager getMigrationManager() {
        return this.migrationManager;
    }

    public PartitionReplicaManager getReplicaManager() {
        return this.replicaManager;
    }

    @Override
    public PartitionReplicaStateChecker getPartitionReplicaStateChecker() {
        return this.partitionReplicaStateChecker;
    }

    public PartitionEventManager getPartitionEventManager() {
        return this.partitionEventManager;
    }

    boolean isFetchMostRecentPartitionTableTaskRequired() {
        return this.shouldFetchPartitionTables;
    }

    boolean scheduleFetchMostRecentPartitionTableTaskIfRequired() {
        this.lock.lock();
        try {
            if (this.shouldFetchPartitionTables) {
                this.migrationManager.schedule(new FetchMostRecentPartitionTableTask());
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    public void replaceMember(Member oldMember, Member newMember) {
        this.lock.lock();
        try {
            this.partitionStateManager.replaceMember(oldMember, newMember);
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public PartitionTableView createPartitionTableView() {
        this.lock.lock();
        try {
            PartitionTableView partitionTableView = this.partitionStateManager.getPartitionTable();
            return partitionTableView;
        }
        finally {
            this.lock.unlock();
        }
    }

    public boolean isLocalMemberMaster() {
        return this.isMemberMaster(this.node.getThisAddress());
    }

    public boolean isMemberMaster(Address address) {
        if (address == null) {
            return false;
        }
        Address master = this.latestMaster;
        ClusterServiceImpl clusterService = this.node.getClusterService();
        if (master == null && clusterService.getSize() == 1) {
            master = clusterService.getMasterAddress();
        }
        return address.equals(master) && address.equals(clusterService.getMasterAddress());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean commitMigrationOnDestination(MigrationInfo migration, Address sender) {
        this.lock.lock();
        try {
            if (!this.validateSenderIsMaster(sender, "migration commit")) {
                boolean bl = false;
                return bl;
            }
            int currentVersion = this.partitionStateManager.getVersion();
            int initialVersion = migration.getInitialPartitionVersion();
            int finalVersion = migration.getFinalPartitionVersion();
            if (finalVersion == currentVersion) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Already applied migration commit. Version: " + currentVersion + ", Master: " + sender);
                }
                boolean bl = true;
                return bl;
            }
            if (finalVersion < currentVersion) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Already applied migration commit. Local version: " + currentVersion + ", Master version: " + finalVersion + " Master: " + sender);
                }
                boolean bl = false;
                return bl;
            }
            if (initialVersion != currentVersion) {
                throw new IllegalStateException("Invalid migration commit! Expected version: " + initialVersion + ", current version: " + currentVersion + ", Master: " + sender);
            }
            MigrationInfo activeMigration = this.migrationManager.getActiveMigration();
            assert (migration.equals(activeMigration)) : "Committed migration: " + migration + ", Active migration: " + activeMigration;
            InternalPartitionImpl partition = this.partitionStateManager.getPartitionImpl(migration.getPartitionId());
            boolean added = this.migrationManager.addCompletedMigration(migration);
            assert (added) : "Could not add completed migration on destination: " + migration;
            MigrationManager.applyMigration(partition, migration);
            this.partitionStateManager.setVersion(finalVersion);
            activeMigration.setStatus(migration.getStatus());
            this.migrationManager.finalizeMigration(migration);
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Committed " + migration + " on destination with partition state version: " + finalVersion);
            }
            boolean bl = true;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    public String toString() {
        return "InternalPartitionService {version: " + this.getPartitionStateVersion() + ", migrationQ: " + this.getMigrationQueueSize() + "}";
    }

    private class FetchMostRecentPartitionTableTask
    implements MigrationRunnable {
        private final Address thisAddress;
        private int maxVersion;
        private PartitionRuntimeState newState;

        private FetchMostRecentPartitionTableTask() {
            this.thisAddress = InternalPartitionServiceImpl.this.node.getThisAddress();
        }

        @Override
        public void run() {
            ClusterState clusterState = InternalPartitionServiceImpl.this.node.getClusterService().getClusterState();
            if (!clusterState.isMigrationAllowed() && !clusterState.isPartitionPromotionAllowed()) {
                InternalPartitionServiceImpl.this.logger.fine("No need to fetch the latest partition table. Cluster state does not allow to modify partition table.");
                InternalPartitionServiceImpl.this.shouldFetchPartitionTables = false;
                return;
            }
            this.syncWithPartitionThreads();
            this.maxVersion = InternalPartitionServiceImpl.this.partitionStateManager.getVersion();
            InternalPartitionServiceImpl.this.logger.info("Fetching most recent partition table! my version: " + this.maxVersion);
            HashSet<MigrationInfo> allCompletedMigrations = new HashSet<MigrationInfo>();
            HashSet<MigrationInfo> allActiveMigrations = new HashSet<MigrationInfo>();
            this.collectAndProcessResults(allCompletedMigrations, allActiveMigrations);
            InternalPartitionServiceImpl.this.logger.info("Most recent partition table version: " + this.maxVersion);
            this.processNewState(allCompletedMigrations, allActiveMigrations);
            InternalPartitionServiceImpl.this.publishPartitionRuntimeState();
        }

        private Future<PartitionRuntimeState> fetchPartitionState(Member m) {
            return InternalPartitionServiceImpl.this.nodeEngine.getOperationService().invokeOnTarget("hz:core:partitionService", new FetchPartitionStateOperation(), m.getAddress());
        }

        private void collectAndProcessResults(Collection<MigrationInfo> allCompletedMigrations, Collection<MigrationInfo> allActiveMigrations) {
            Collection<Member> members = ((InternalPartitionServiceImpl)InternalPartitionServiceImpl.this).node.clusterService.getMembers(MemberSelectors.NON_LOCAL_MEMBER_SELECTOR);
            HashMap<Member, Future<PartitionRuntimeState>> futures = new HashMap<Member, Future<PartitionRuntimeState>>();
            for (Member member : members) {
                Future<PartitionRuntimeState> future = this.fetchPartitionState(member);
                futures.put(member, future);
            }
            while (!futures.isEmpty()) {
                Iterator<Map.Entry<Member, Future<PartitionRuntimeState>>> iter = futures.entrySet().iterator();
                while (iter.hasNext()) {
                    PartitionRuntimeState state = this.collectNextPartitionState(iter);
                    if (state == null) continue;
                    if (this.maxVersion < state.getVersion()) {
                        this.newState = state;
                        this.maxVersion = state.getVersion();
                    }
                    allCompletedMigrations.addAll(state.getCompletedMigrations());
                    if (state.getActiveMigration() == null) continue;
                    allActiveMigrations.add(state.getActiveMigration());
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private PartitionRuntimeState collectNextPartitionState(Iterator<Map.Entry<Member, Future<PartitionRuntimeState>>> iter) {
            Map.Entry<Member, Future<PartitionRuntimeState>> next = iter.next();
            Member member = next.getKey();
            Future<PartitionRuntimeState> future = next.getValue();
            boolean collectedState = true;
            try {
                PartitionRuntimeState state = future.get(5L, TimeUnit.SECONDS);
                if (state == null) {
                    InternalPartitionServiceImpl.this.logger.fine("Received NULL partition state from " + member);
                } else {
                    InternalPartitionServiceImpl.this.logger.fine("Received partition state version: " + state.getVersion() + " from " + member);
                }
                PartitionRuntimeState partitionRuntimeState = state;
                return partitionRuntimeState;
            }
            catch (InterruptedException e) {
                InternalPartitionServiceImpl.this.logger.fine("FetchMostRecentPartitionTableTask is interrupted.");
                Thread.currentThread().interrupt();
            }
            catch (TimeoutException e) {
                collectedState = false;
                next.setValue(this.fetchPartitionState(member));
            }
            catch (Exception e) {
                Level level = Level.SEVERE;
                if (e instanceof MemberLeftException || e.getCause() instanceof TargetNotMemberException) {
                    level = Level.FINE;
                }
                InternalPartitionServiceImpl.this.logger.log(level, "Failed to fetch partition table from " + member, e);
            }
            finally {
                if (collectedState) {
                    iter.remove();
                }
            }
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void processNewState(Collection<MigrationInfo> allCompletedMigrations, Collection<MigrationInfo> allActiveMigrations) {
            InternalPartitionServiceImpl.this.lock.lock();
            try {
                this.processMigrations(allCompletedMigrations, allActiveMigrations);
                Version version = InternalPartitionServiceImpl.this.node.getClusterService().getClusterVersion();
                boolean version312plus = version.isGreaterOrEqual(Versions.V3_12);
                if (this.newState != null) {
                    this.maxVersion = Math.max(this.maxVersion, InternalPartitionServiceImpl.this.getPartitionStateVersion());
                    if (version312plus) {
                        for (MigrationInfo migration : allCompletedMigrations) {
                            this.maxVersion = Math.max(this.maxVersion, migration.getFinalPartitionVersion());
                        }
                    }
                    ++this.maxVersion;
                    InternalPartitionServiceImpl.this.logger.info("Applying the most recent of partition state with new version: " + this.maxVersion);
                    InternalPartitionServiceImpl.this.applyNewPartitionTable(this.newState.getPartitionTable(), this.maxVersion, allCompletedMigrations, this.thisAddress);
                } else if (InternalPartitionServiceImpl.this.partitionStateManager.isInitialized()) {
                    for (MigrationInfo migrationInfo : allCompletedMigrations) {
                        if (!InternalPartitionServiceImpl.this.migrationManager.addCompletedMigration(migrationInfo)) continue;
                        if (version312plus) {
                            InternalPartitionServiceImpl.this.partitionStateManager.incrementVersion(migrationInfo.getPartitionVersionIncrement());
                        }
                        if (InternalPartitionServiceImpl.this.logger.isFinestEnabled()) {
                            InternalPartitionServiceImpl.this.logger.finest("Scheduling migration finalization after finding most recent partition table: " + migrationInfo);
                        }
                        InternalPartitionServiceImpl.this.migrationManager.scheduleActiveMigrationFinalization(migrationInfo);
                    }
                    InternalPartitionServiceImpl.this.partitionStateManager.incrementVersion();
                    InternalPartitionServiceImpl.this.node.getNodeExtension().onPartitionStateChange();
                }
                InternalPartitionServiceImpl.this.shouldFetchPartitionTables = false;
            }
            finally {
                InternalPartitionServiceImpl.this.lock.unlock();
            }
        }

        private void processMigrations(Collection<MigrationInfo> allCompletedMigrations, Collection<MigrationInfo> allActiveMigrations) {
            allCompletedMigrations.addAll(InternalPartitionServiceImpl.this.migrationManager.getCompletedMigrationsCopy());
            if (InternalPartitionServiceImpl.this.migrationManager.getActiveMigration() != null) {
                allActiveMigrations.add(InternalPartitionServiceImpl.this.migrationManager.getActiveMigration());
            }
            for (MigrationInfo activeMigration : allActiveMigrations) {
                activeMigration.setStatus(MigrationInfo.MigrationStatus.FAILED);
                activeMigration.setPartitionVersionIncrement(activeMigration.getPartitionVersionIncrement() + 1);
                if (!allCompletedMigrations.add(activeMigration)) continue;
                InternalPartitionServiceImpl.this.logger.info("Marked active migration " + activeMigration + " as " + (Object)((Object)MigrationInfo.MigrationStatus.FAILED));
            }
        }

        private void syncWithPartitionThreads() {
            OperationServiceImpl operationService = (OperationServiceImpl)InternalPartitionServiceImpl.this.nodeEngine.getOperationService();
            OperationExecutor opExecutor = operationService.getOperationExecutor();
            CountDownLatch latch = new CountDownLatch(opExecutor.getPartitionThreadCount());
            opExecutor.executeOnPartitionThreads(new PartitionThreadBarrierTask(latch));
            try {
                latch.await();
            }
            catch (InterruptedException e) {
                InternalPartitionServiceImpl.this.logger.warning(e);
                Thread.currentThread().interrupt();
            }
        }

        private final class PartitionThreadBarrierTask
        implements Runnable,
        UrgentSystemOperation {
            private final CountDownLatch latch;

            private PartitionThreadBarrierTask(CountDownLatch latch) {
                this.latch = latch;
            }

            @Override
            public void run() {
                this.latch.countDown();
            }
        }
    }
}

