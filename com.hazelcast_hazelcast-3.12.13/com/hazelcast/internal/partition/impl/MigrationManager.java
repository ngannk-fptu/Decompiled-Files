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
import com.hazelcast.core.MigrationEvent;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.PartitionRuntimeState;
import com.hazelcast.internal.partition.PartitionStateVersionMismatchException;
import com.hazelcast.internal.partition.impl.InternalMigrationListener;
import com.hazelcast.internal.partition.impl.InternalPartitionImpl;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.impl.MigrationPlanner;
import com.hazelcast.internal.partition.impl.MigrationQueue;
import com.hazelcast.internal.partition.impl.MigrationRunnable;
import com.hazelcast.internal.partition.impl.MigrationStats;
import com.hazelcast.internal.partition.impl.MigrationThread;
import com.hazelcast.internal.partition.impl.PartitionEventManager;
import com.hazelcast.internal.partition.impl.PartitionStateManager;
import com.hazelcast.internal.partition.operation.FinalizeMigrationOperation;
import com.hazelcast.internal.partition.operation.MigrationCommitOperation;
import com.hazelcast.internal.partition.operation.MigrationRequestOperation;
import com.hazelcast.internal.partition.operation.PromotionCommitOperation;
import com.hazelcast.internal.partition.operation.PublishCompletedMigrationsOperation;
import com.hazelcast.internal.partition.operation.ShutdownResponseOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.MutableInteger;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.scheduler.CoalescingDelayedTrigger;
import com.hazelcast.version.Version;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;

public class MigrationManager {
    private static final int MIGRATION_PAUSE_DURATION_SECONDS_ON_MIGRATION_FAILURE = 3;
    private static final int PUBLISH_COMPLETED_MIGRATIONS_BATCH_SIZE = 10;
    final long partitionMigrationInterval;
    private final Node node;
    private final NodeEngineImpl nodeEngine;
    private final InternalPartitionServiceImpl partitionService;
    private final ILogger logger;
    private final PartitionStateManager partitionStateManager;
    private final MigrationQueue migrationQueue = new MigrationQueue();
    private final MigrationThread migrationThread;
    private final AtomicBoolean migrationTasksAllowed = new AtomicBoolean(true);
    private final long partitionMigrationTimeout;
    private final CoalescingDelayedTrigger delayedResumeMigrationTrigger;
    private final Set<Member> shutdownRequestedMembers = new HashSet<Member>();
    private volatile MigrationInfo activeMigrationInfo;
    private final LinkedHashSet<MigrationInfo> completedMigrations = new LinkedHashSet();
    private final AtomicBoolean promotionPermit = new AtomicBoolean(false);
    private final MigrationStats stats = new MigrationStats();
    private volatile InternalMigrationListener internalMigrationListener = new InternalMigrationListener.NopInternalMigrationListener();
    private final Lock partitionServiceLock;
    private final MigrationPlanner migrationPlanner;
    private final boolean fragmentedMigrationEnabled;
    private final long memberHeartbeatTimeoutMillis;
    private boolean triggerRepartitioningWhenClusterStateAllowsMigration;
    private final Set<MigrationInfo> finalizingMigrationsRegistry = Collections.newSetFromMap(new ConcurrentHashMap());

    MigrationManager(Node node, InternalPartitionServiceImpl service, Lock partitionServiceLock) {
        this.node = node;
        this.nodeEngine = node.getNodeEngine();
        this.partitionService = service;
        this.logger = node.getLogger(this.getClass());
        this.partitionServiceLock = partitionServiceLock;
        this.migrationPlanner = new MigrationPlanner(node.getLogger(MigrationPlanner.class));
        HazelcastProperties properties = node.getProperties();
        this.partitionMigrationInterval = properties.getPositiveMillisOrDefault(GroupProperty.PARTITION_MIGRATION_INTERVAL, 0L);
        this.partitionMigrationTimeout = properties.getMillis(GroupProperty.PARTITION_MIGRATION_TIMEOUT);
        this.fragmentedMigrationEnabled = properties.getBoolean(GroupProperty.PARTITION_FRAGMENTED_MIGRATION_ENABLED);
        this.partitionStateManager = this.partitionService.getPartitionStateManager();
        ILogger migrationThreadLogger = node.getLogger(MigrationThread.class);
        String hzName = this.nodeEngine.getHazelcastInstance().getName();
        this.migrationThread = new MigrationThread(this, hzName, migrationThreadLogger, this.migrationQueue);
        long migrationPauseDelayMs = TimeUnit.SECONDS.toMillis(3L);
        InternalExecutionService executionService = this.nodeEngine.getExecutionService();
        this.delayedResumeMigrationTrigger = new CoalescingDelayedTrigger(executionService, migrationPauseDelayMs, 2L * migrationPauseDelayMs, new Runnable(){

            @Override
            public void run() {
                MigrationManager.this.resumeMigration();
            }
        });
        this.memberHeartbeatTimeoutMillis = properties.getMillis(GroupProperty.MAX_NO_HEARTBEAT_SECONDS);
        this.nodeEngine.getMetricsRegistry().scanAndRegister(this.stats, "partitions");
    }

    @Probe(name="migrationActive")
    private int migrationActiveProbe() {
        return this.migrationTasksAllowed.get() ? 1 : 0;
    }

    void pauseMigration() {
        this.migrationTasksAllowed.set(false);
    }

    void resumeMigration() {
        this.migrationTasksAllowed.set(true);
    }

    private void resumeMigrationEventually() {
        this.delayedResumeMigrationTrigger.executeWithDelay();
    }

    boolean areMigrationTasksAllowed() {
        return this.migrationTasksAllowed.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void finalizeMigration(MigrationInfo migrationInfo) {
        try {
            PartitionReplica localReplica = PartitionReplica.from(this.node.getLocalMember());
            int partitionId = migrationInfo.getPartitionId();
            boolean source = localReplica.equals(migrationInfo.getSource());
            boolean destination = localReplica.equals(migrationInfo.getDestination());
            assert (migrationInfo.getStatus() == MigrationInfo.MigrationStatus.SUCCESS || migrationInfo.getStatus() == MigrationInfo.MigrationStatus.FAILED) : "Invalid migration: " + migrationInfo;
            if (source || destination) {
                InternalMigrationListener.MigrationParticipant participant;
                boolean success = migrationInfo.getStatus() == MigrationInfo.MigrationStatus.SUCCESS;
                InternalMigrationListener.MigrationParticipant migrationParticipant = participant = source ? InternalMigrationListener.MigrationParticipant.SOURCE : InternalMigrationListener.MigrationParticipant.DESTINATION;
                if (success) {
                    this.internalMigrationListener.onMigrationCommit(participant, migrationInfo);
                } else {
                    this.internalMigrationListener.onMigrationRollback(participant, migrationInfo);
                }
                MigrationEndpoint endpoint = source ? MigrationEndpoint.SOURCE : MigrationEndpoint.DESTINATION;
                FinalizeMigrationOperation op = new FinalizeMigrationOperation(migrationInfo, endpoint, success);
                op.setPartitionId(partitionId).setNodeEngine(this.nodeEngine).setValidateTarget(false).setService(this.partitionService);
                this.registerFinalizingMigration(migrationInfo);
                InternalOperationService operationService = this.nodeEngine.getOperationService();
                if (operationService.isRunAllowed(op)) {
                    operationService.run(op);
                } else {
                    operationService.execute(op);
                }
                this.removeActiveMigration(partitionId);
            } else {
                PartitionReplica partitionOwner = this.partitionStateManager.getPartitionImpl(partitionId).getOwnerReplicaOrNull();
                if (localReplica.equals(partitionOwner)) {
                    this.removeActiveMigration(partitionId);
                    this.partitionStateManager.clearMigratingFlag(partitionId);
                } else {
                    this.logger.severe("Failed to finalize migration because " + localReplica + " is not a participant of the migration: " + migrationInfo);
                }
            }
        }
        catch (Exception e) {
            this.logger.warning(e);
        }
        finally {
            migrationInfo.doneProcessing();
        }
    }

    private void registerFinalizingMigration(MigrationInfo migration) {
        this.finalizingMigrationsRegistry.add(migration);
    }

    public boolean removeFinalizingMigration(MigrationInfo migration) {
        return this.finalizingMigrationsRegistry.remove(migration);
    }

    public boolean isFinalizingMigrationRegistered(int partitionId) {
        for (MigrationInfo migrationInfo : this.finalizingMigrationsRegistry) {
            if (partitionId != migrationInfo.getPartitionId()) continue;
            return true;
        }
        return false;
    }

    public MigrationInfo setActiveMigration(MigrationInfo migrationInfo) {
        this.partitionServiceLock.lock();
        try {
            if (this.activeMigrationInfo == null) {
                this.activeMigrationInfo = migrationInfo;
                MigrationInfo migrationInfo2 = null;
                return migrationInfo2;
            }
            if (!this.activeMigrationInfo.equals(migrationInfo) && this.logger.isFineEnabled()) {
                this.logger.fine("Active migration is not set: " + migrationInfo + ". Existing active migration: " + this.activeMigrationInfo);
            }
            MigrationInfo migrationInfo3 = this.activeMigrationInfo;
            return migrationInfo3;
        }
        finally {
            this.partitionServiceLock.unlock();
        }
    }

    public MigrationInfo getActiveMigration() {
        return this.activeMigrationInfo;
    }

    public boolean acquirePromotionPermit() {
        return this.promotionPermit.compareAndSet(false, true);
    }

    public void releasePromotionPermit() {
        this.promotionPermit.set(false);
    }

    private boolean removeActiveMigration(int partitionId) {
        this.partitionServiceLock.lock();
        try {
            if (this.activeMigrationInfo != null) {
                if (this.activeMigrationInfo.getPartitionId() == partitionId) {
                    this.activeMigrationInfo = null;
                    boolean bl = true;
                    return bl;
                }
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Active migration is not removed, because it has different partitionId! partitionId=" + partitionId + ", active migration=" + this.activeMigrationInfo);
                }
            }
        }
        finally {
            this.partitionServiceLock.unlock();
        }
        return false;
    }

    void scheduleActiveMigrationFinalization(final MigrationInfo migrationInfo) {
        this.partitionServiceLock.lock();
        try {
            if (migrationInfo.equals(this.activeMigrationInfo)) {
                if (this.activeMigrationInfo.startProcessing()) {
                    this.activeMigrationInfo.setStatus(migrationInfo.getStatus());
                    this.finalizeMigration(this.activeMigrationInfo);
                } else {
                    this.logger.info("Scheduling finalization of " + migrationInfo + ", because migration process is currently running.");
                    this.nodeEngine.getExecutionService().schedule(new Runnable(){

                        @Override
                        public void run() {
                            MigrationManager.this.scheduleActiveMigrationFinalization(migrationInfo);
                        }
                    }, 1L, TimeUnit.SECONDS);
                }
                return;
            }
            PartitionReplica source = migrationInfo.getSource();
            if (source != null && migrationInfo.getSourceCurrentReplicaIndex() > 0 && source.isIdentical(this.node.getLocalMember())) {
                this.finalizeMigration(migrationInfo);
            }
        }
        finally {
            this.partitionServiceLock.unlock();
        }
    }

    private boolean commitMigrationToDestination(MigrationInfo migration) {
        PartitionReplica destination = migration.getDestination();
        if (destination.isIdentical(this.node.getLocalMember())) {
            if (this.logger.isFinestEnabled()) {
                this.logger.finest("Shortcutting migration commit, since destination is master. -> " + migration);
            }
            return true;
        }
        MemberImpl member = this.node.getClusterService().getMember(destination.address(), destination.uuid());
        if (member == null) {
            this.logger.warning("Cannot commit " + migration + ". Destination " + destination + " is not a member anymore");
            return false;
        }
        try {
            MigrationCommitOperation operation;
            if (this.logger.isFinestEnabled()) {
                this.logger.finest("Sending migration commit operation to " + destination + " for " + migration);
            }
            migration.setStatus(MigrationInfo.MigrationStatus.SUCCESS);
            String destinationUuid = member.getUuid();
            if (this.nodeEngine.getClusterService().getClusterVersion().isGreaterOrEqual(Versions.V3_12)) {
                operation = new MigrationCommitOperation(migration, destinationUuid);
            } else {
                PartitionRuntimeState partitionState = this.partitionService.createMigrationCommitPartitionState(migration);
                operation = new MigrationCommitOperation(partitionState, destinationUuid);
            }
            InternalCompletableFuture future = this.nodeEngine.getOperationService().createInvocationBuilder("hz:core:partitionService", (Operation)operation, destination.address()).setTryCount(Integer.MAX_VALUE).setCallTimeout(this.memberHeartbeatTimeoutMillis).invoke();
            boolean result = (Boolean)future.get();
            if (this.logger.isFinestEnabled()) {
                this.logger.finest("Migration commit result " + result + " from " + destination + " for " + migration);
            }
            return result;
        }
        catch (Throwable t) {
            this.logMigrationCommitFailure(migration, t);
            if (t.getCause() instanceof OperationTimeoutException) {
                return this.commitMigrationToDestination(migration);
            }
            return false;
        }
    }

    private void logMigrationCommitFailure(MigrationInfo migration, Throwable t) {
        boolean memberLeft = t instanceof MemberLeftException || t.getCause() instanceof TargetNotMemberException || t.getCause() instanceof HazelcastInstanceNotActiveException;
        PartitionReplica destination = migration.getDestination();
        if (memberLeft) {
            if (destination.isIdentical(this.node.getLocalMember())) {
                this.logger.fine("Migration commit failed for " + migration + " since this node is shutting down.");
                return;
            }
            this.logger.warning("Migration commit failed for " + migration + " since destination " + destination + " left the cluster");
        } else {
            this.logger.severe("Migration commit to " + destination + " failed for " + migration, t);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean addCompletedMigration(MigrationInfo migrationInfo) {
        if (migrationInfo.getStatus() != MigrationInfo.MigrationStatus.SUCCESS && migrationInfo.getStatus() != MigrationInfo.MigrationStatus.FAILED) {
            throw new IllegalArgumentException("Migration doesn't seem completed: " + migrationInfo);
        }
        if (this.node.getClusterService().getClusterVersion().isGreaterOrEqual(Versions.V3_12) && (migrationInfo.getInitialPartitionVersion() <= 0 || migrationInfo.getPartitionVersionIncrement() <= 0)) {
            throw new IllegalArgumentException("Partition state versions are not set: " + migrationInfo);
        }
        this.partitionServiceLock.lock();
        try {
            boolean added = this.completedMigrations.add(migrationInfo);
            if (added) {
                this.stats.incrementCompletedMigrations();
            }
            boolean bl = added;
            return bl;
        }
        finally {
            this.partitionServiceLock.unlock();
        }
    }

    void retainCompletedMigrations(Collection<MigrationInfo> migrations) {
        this.partitionServiceLock.lock();
        try {
            this.completedMigrations.retainAll(migrations);
        }
        finally {
            this.partitionServiceLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void evictCompletedMigrations(MigrationInfo currentMigration) {
        this.partitionServiceLock.lock();
        try {
            assert (this.completedMigrations.contains(currentMigration)) : currentMigration + " to evict is not in completed migrations";
            Iterator iter = this.completedMigrations.iterator();
            while (iter.hasNext()) {
                MigrationInfo migration = (MigrationInfo)iter.next();
                iter.remove();
                if (!migration.equals(currentMigration)) continue;
                return;
            }
        }
        finally {
            this.partitionServiceLock.unlock();
        }
    }

    private void evictCompletedMigrations(Collection<MigrationInfo> migrations) {
        this.partitionServiceLock.lock();
        try {
            this.completedMigrations.removeAll(migrations);
        }
        finally {
            this.partitionServiceLock.unlock();
        }
    }

    void triggerControlTask() {
        this.migrationQueue.clear();
        if (!this.node.getClusterService().isJoined()) {
            this.logger.fine("Node is not joined, will not trigger ControlTask");
            return;
        }
        if (!this.partitionService.isLocalMemberMaster()) {
            this.logger.fine("Node is not master, will not trigger ControlTask");
            return;
        }
        this.migrationQueue.add(new ControlTask());
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Migration queue is cleared and control task is scheduled");
        }
    }

    InternalMigrationListener getInternalMigrationListener() {
        return this.internalMigrationListener;
    }

    void setInternalMigrationListener(InternalMigrationListener listener) {
        Preconditions.checkNotNull(listener);
        this.internalMigrationListener = listener;
    }

    void resetInternalMigrationListener() {
        this.internalMigrationListener = new InternalMigrationListener.NopInternalMigrationListener();
    }

    void onShutdownRequest(Member member) {
        if (!this.partitionStateManager.isInitialized()) {
            this.sendShutdownOperation(member.getAddress());
            return;
        }
        ClusterState clusterState = this.node.getClusterService().getClusterState();
        if (!clusterState.isMigrationAllowed() && clusterState != ClusterState.IN_TRANSITION) {
            this.sendShutdownOperation(member.getAddress());
            return;
        }
        if (this.shutdownRequestedMembers.add(member)) {
            this.logger.info("Shutdown request of " + member + " is handled");
            this.triggerControlTask();
        }
    }

    void onMemberRemove(Member member) {
        PartitionReplica replica;
        this.shutdownRequestedMembers.remove(member);
        MigrationInfo activeMigration = this.activeMigrationInfo;
        if (activeMigration != null && ((replica = PartitionReplica.from(member)).equals(activeMigration.getSource()) || replica.equals(activeMigration.getDestination()))) {
            activeMigration.setStatus(MigrationInfo.MigrationStatus.INVALID);
        }
    }

    void schedule(MigrationRunnable runnable) {
        this.migrationQueue.add(runnable);
    }

    List<MigrationInfo> getCompletedMigrationsCopy() {
        this.partitionServiceLock.lock();
        try {
            ArrayList<MigrationInfo> arrayList = new ArrayList<MigrationInfo>(this.completedMigrations);
            return arrayList;
        }
        finally {
            this.partitionServiceLock.unlock();
        }
    }

    boolean hasOnGoingMigration() {
        return this.activeMigrationInfo != null || this.migrationQueue.hasMigrationTasks();
    }

    int getMigrationQueueSize() {
        return this.migrationQueue.migrationTaskCount();
    }

    void reset() {
        this.migrationQueue.clear();
        this.activeMigrationInfo = null;
        this.completedMigrations.clear();
        this.shutdownRequestedMembers.clear();
        this.migrationTasksAllowed.set(true);
    }

    void start() {
        this.migrationThread.start();
    }

    void stop() {
        this.migrationThread.stopNow();
    }

    void scheduleMigration(MigrationInfo migrationInfo) {
        this.migrationQueue.add(new MigrateTask(migrationInfo));
    }

    static void applyMigration(InternalPartitionImpl partition, MigrationInfo migrationInfo) {
        PartitionReplica[] members = Arrays.copyOf(partition.getReplicas(), 7);
        if (migrationInfo.getSourceCurrentReplicaIndex() > -1) {
            members[migrationInfo.getSourceCurrentReplicaIndex()] = null;
        }
        if (migrationInfo.getDestinationCurrentReplicaIndex() > -1) {
            members[migrationInfo.getDestinationCurrentReplicaIndex()] = null;
        }
        members[migrationInfo.getDestinationNewReplicaIndex()] = migrationInfo.getDestination();
        if (migrationInfo.getSourceNewReplicaIndex() > -1) {
            members[migrationInfo.getSourceNewReplicaIndex()] = migrationInfo.getSource();
        }
        partition.setReplicas(members);
    }

    Set<Member> getShutdownRequestedMembers() {
        return this.shutdownRequestedMembers;
    }

    private void sendShutdownOperation(Address address) {
        if (this.node.getThisAddress().equals(address)) {
            assert (!this.node.isRunning()) : "Node state: " + (Object)((Object)this.node.getState());
            this.partitionService.onShutdownResponse();
        } else {
            this.nodeEngine.getOperationService().send(new ShutdownResponseOperation(), address);
        }
    }

    boolean shouldTriggerRepartitioningWhenClusterStateAllowsMigration() {
        return this.triggerRepartitioningWhenClusterStateAllowsMigration;
    }

    private void publishCompletedMigrations() {
        assert (this.partitionService.isLocalMemberMaster());
        assert (this.partitionStateManager.isInitialized());
        final List<MigrationInfo> migrations = this.getCompletedMigrationsCopy();
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Publishing completed migrations [" + migrations.size() + "]: " + migrations);
        }
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        ClusterServiceImpl clusterService = this.node.clusterService;
        Set<Member> members = clusterService.getMembers();
        final AtomicInteger latch = new AtomicInteger(members.size() - 1);
        for (final Member member : members) {
            if (member.localMember()) continue;
            PublishCompletedMigrationsOperation operation = new PublishCompletedMigrationsOperation(migrations);
            InternalCompletableFuture f = operationService.invokeOnTarget("hz:core:partitionService", operation, member.getAddress());
            f.andThen(new ExecutionCallback<Boolean>(){

                @Override
                public void onResponse(Boolean response) {
                    if (!Boolean.TRUE.equals(response)) {
                        MigrationManager.this.logger.fine(member + " rejected completed migrations with response " + response);
                        MigrationManager.this.partitionService.sendPartitionRuntimeState(member.getAddress());
                        return;
                    }
                    if (latch.decrementAndGet() == 0) {
                        MigrationManager.this.logger.fine("Evicting " + migrations.size() + " completed migrations.");
                        MigrationManager.this.evictCompletedMigrations(migrations);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    MigrationManager.this.logger.fine("Failure while publishing completed migrations to " + member, t);
                    MigrationManager.this.partitionService.sendPartitionRuntimeState(member.getAddress());
                }
            });
        }
    }

    public MigrationStats getStats() {
        return this.stats;
    }

    void onClusterVersionChange(Version newVersion) {
        if (newVersion.isEqualTo(Versions.V3_12)) {
            this.partitionServiceLock.lock();
            try {
                assert (this.activeMigrationInfo == null) : "Active migration: " + this.activeMigrationInfo;
                this.completedMigrations.clear();
            }
            finally {
                this.partitionServiceLock.unlock();
            }
        }
    }

    private class PublishCompletedMigrationsTask
    implements MigrationRunnable {
        private PublishCompletedMigrationsTask() {
        }

        @Override
        public void run() {
            Version clusterVersion = MigrationManager.this.nodeEngine.getClusterService().getClusterVersion();
            if (clusterVersion.isGreaterOrEqual(Versions.V3_12)) {
                MigrationManager.this.publishCompletedMigrations();
            }
        }
    }

    private class ProcessShutdownRequestsTask
    implements MigrationRunnable {
        private ProcessShutdownRequestsTask() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            if (!MigrationManager.this.partitionService.isLocalMemberMaster()) {
                return;
            }
            MigrationManager.this.partitionServiceLock.lock();
            try {
                int shutdownRequestCount = MigrationManager.this.shutdownRequestedMembers.size();
                if (shutdownRequestCount > 0) {
                    if (shutdownRequestCount == MigrationManager.this.nodeEngine.getClusterService().getSize(MemberSelectors.DATA_MEMBER_SELECTOR)) {
                        for (Member member : MigrationManager.this.shutdownRequestedMembers) {
                            MigrationManager.this.sendShutdownOperation(member.getAddress());
                        }
                    } else {
                        boolean present = false;
                        for (Member member : MigrationManager.this.shutdownRequestedMembers) {
                            if (MigrationManager.this.partitionStateManager.isAbsentInPartitionTable(member)) {
                                MigrationManager.this.sendShutdownOperation(member.getAddress());
                                continue;
                            }
                            MigrationManager.this.logger.warning(member + " requested to shutdown but still in partition table");
                            present = true;
                        }
                        if (present) {
                            MigrationManager.this.triggerControlTask();
                        }
                    }
                }
            }
            finally {
                MigrationManager.this.partitionServiceLock.unlock();
            }
        }
    }

    private class ControlTask
    implements MigrationRunnable {
        private ControlTask() {
        }

        @Override
        public void run() {
            MigrationManager.this.partitionServiceLock.lock();
            try {
                MigrationManager.this.migrationQueue.clear();
                if (MigrationManager.this.partitionService.scheduleFetchMostRecentPartitionTableTaskIfRequired()) {
                    if (MigrationManager.this.logger.isFinestEnabled()) {
                        MigrationManager.this.logger.finest("FetchMostRecentPartitionTableTask scheduled");
                    }
                    MigrationManager.this.migrationQueue.add(new ControlTask());
                    return;
                }
                if (MigrationManager.this.logger.isFinestEnabled()) {
                    MigrationManager.this.logger.finest("RepairPartitionTableTask scheduled");
                }
                MigrationManager.this.migrationQueue.add(new RepairPartitionTableTask());
            }
            finally {
                MigrationManager.this.partitionServiceLock.unlock();
            }
        }
    }

    private class RepairPartitionTableTask
    implements MigrationRunnable {
        private RepairPartitionTableTask() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            if (!MigrationManager.this.partitionStateManager.isInitialized()) {
                return;
            }
            ClusterState clusterState = MigrationManager.this.node.getClusterService().getClusterState();
            if (!clusterState.isMigrationAllowed() && !clusterState.isPartitionPromotionAllowed()) {
                MigrationManager.this.logger.fine("Will not repair partition table at the moment. Cluster state does not allow to modify partition table.");
                return;
            }
            Map<PartitionReplica, Collection<MigrationInfo>> promotions = this.removeUnknownMembersAndCollectPromotions();
            boolean success = this.promoteBackupsForMissingOwners(promotions);
            MigrationManager.this.partitionServiceLock.lock();
            try {
                if (success) {
                    if (MigrationManager.this.logger.isFinestEnabled()) {
                        MigrationManager.this.logger.finest("RepartitioningTask scheduled");
                    }
                    MigrationManager.this.migrationQueue.add(new RepartitioningTask());
                } else {
                    MigrationManager.this.triggerControlTask();
                }
            }
            finally {
                MigrationManager.this.partitionServiceLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Map<PartitionReplica, Collection<MigrationInfo>> removeUnknownMembersAndCollectPromotions() {
            MigrationManager.this.partitionServiceLock.lock();
            try {
                MigrationManager.this.partitionStateManager.removeUnknownMembers();
                HashMap<PartitionReplica, Collection<MigrationInfo>> promotions = new HashMap<PartitionReplica, Collection<MigrationInfo>>();
                for (int partitionId = 0; partitionId < MigrationManager.this.partitionService.getPartitionCount(); ++partitionId) {
                    MigrationInfo migration = this.createPromotionMigrationIfOwnerIsNull(partitionId);
                    if (migration == null) continue;
                    ArrayList<MigrationInfo> migrations = (ArrayList<MigrationInfo>)promotions.get(migration.getDestination());
                    if (migrations == null) {
                        migrations = new ArrayList<MigrationInfo>();
                        promotions.put(migration.getDestination(), migrations);
                    }
                    migrations.add(migration);
                }
                HashMap<PartitionReplica, Collection<MigrationInfo>> hashMap = promotions;
                return hashMap;
            }
            finally {
                MigrationManager.this.partitionServiceLock.unlock();
            }
        }

        private boolean promoteBackupsForMissingOwners(Map<PartitionReplica, Collection<MigrationInfo>> promotions) {
            boolean allSucceeded = true;
            for (Map.Entry<PartitionReplica, Collection<MigrationInfo>> entry : promotions.entrySet()) {
                PartitionReplica destination = entry.getKey();
                Collection<MigrationInfo> migrations = entry.getValue();
                allSucceeded &= this.commitPromotionMigrations(destination, migrations);
            }
            return allSucceeded;
        }

        private boolean commitPromotionMigrations(PartitionReplica destination, Collection<MigrationInfo> migrations) {
            MigrationManager.this.internalMigrationListener.onPromotionStart(InternalMigrationListener.MigrationParticipant.MASTER, migrations);
            boolean success = this.commitPromotionsToDestination(destination, migrations);
            boolean local = destination.isIdentical(MigrationManager.this.node.getLocalMember());
            if (!local) {
                this.processPromotionCommitResult(destination, migrations, success);
            }
            MigrationManager.this.internalMigrationListener.onPromotionComplete(InternalMigrationListener.MigrationParticipant.MASTER, migrations, success);
            MigrationManager.this.partitionService.publishPartitionRuntimeState();
            return success;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void processPromotionCommitResult(PartitionReplica destination, Collection<MigrationInfo> migrations, boolean success) {
            MigrationManager.this.partitionServiceLock.lock();
            try {
                if (!MigrationManager.this.partitionStateManager.isInitialized()) {
                    return;
                }
                if (success) {
                    for (MigrationInfo migration : migrations) {
                        InternalPartitionImpl partition = MigrationManager.this.partitionStateManager.getPartitionImpl(migration.getPartitionId());
                        assert (partition.getOwnerReplicaOrNull() == null) : "Owner should be null: " + partition;
                        assert (destination.equals(partition.getReplica(migration.getDestinationCurrentReplicaIndex()))) : "Invalid replica! Destination: " + destination + ", index: " + migration.getDestinationCurrentReplicaIndex() + ", " + partition;
                        partition.swapReplicas(0, migration.getDestinationCurrentReplicaIndex());
                    }
                } else {
                    int delta = 2 * migrations.size() + 1;
                    MigrationManager.this.partitionService.getPartitionStateManager().incrementVersion(delta);
                }
            }
            finally {
                MigrationManager.this.partitionServiceLock.unlock();
            }
        }

        private MigrationInfo createPromotionMigrationIfOwnerIsNull(int partitionId) {
            InternalPartitionImpl partition = MigrationManager.this.partitionStateManager.getPartitionImpl(partitionId);
            if (partition.getOwnerReplicaOrNull() == null) {
                int index;
                PartitionReplica destination = null;
                for (int i = index = 1; i < 7; ++i) {
                    destination = partition.getReplica(i);
                    if (destination == null) continue;
                    index = i;
                    break;
                }
                if (MigrationManager.this.logger.isFinestEnabled()) {
                    if (destination != null) {
                        MigrationManager.this.logger.finest("partitionId=" + partition.getPartitionId() + " owner is removed. replicaIndex=" + index + " will be shifted up to 0. " + partition);
                    } else {
                        MigrationManager.this.logger.finest("partitionId=" + partition.getPartitionId() + " owner is removed. there is no other replica to shift up. " + partition);
                    }
                }
                if (destination != null) {
                    MigrationInfo migration = new MigrationInfo(partitionId, null, destination, -1, -1, index, 0);
                    migration.setMaster(MigrationManager.this.node.getThisAddress());
                    migration.setStatus(MigrationInfo.MigrationStatus.SUCCESS);
                    return migration;
                }
            }
            if (partition.getOwnerReplicaOrNull() == null) {
                MigrationManager.this.logger.warning("partitionId=" + partitionId + " is completely lost!");
                PartitionEventManager partitionEventManager = MigrationManager.this.partitionService.getPartitionEventManager();
                partitionEventManager.sendPartitionLostEvent(partitionId, 6);
            }
            return null;
        }

        private boolean commitPromotionsToDestination(PartitionReplica destination, Collection<MigrationInfo> migrations) {
            assert (migrations.size() > 0) : "No promotions to commit! destination=" + destination;
            MemberImpl member = MigrationManager.this.node.getClusterService().getMember(destination.address(), destination.uuid());
            if (member == null) {
                MigrationManager.this.logger.warning("Cannot commit promotions. Destination " + destination + " is not a member anymore");
                return false;
            }
            try {
                if (MigrationManager.this.logger.isFinestEnabled()) {
                    MigrationManager.this.logger.finest("Sending promotion commit operation to " + destination + " for " + migrations);
                }
                PartitionRuntimeState partitionState = MigrationManager.this.partitionService.createPromotionCommitPartitionState(migrations);
                String destinationUuid = member.getUuid();
                PromotionCommitOperation op = new PromotionCommitOperation(partitionState, migrations, destinationUuid);
                InternalCompletableFuture future = MigrationManager.this.nodeEngine.getOperationService().createInvocationBuilder("hz:core:partitionService", (Operation)op, destination.address()).setTryCount(Integer.MAX_VALUE).setCallTimeout(MigrationManager.this.memberHeartbeatTimeoutMillis).invoke();
                boolean result = (Boolean)future.get();
                if (MigrationManager.this.logger.isFinestEnabled()) {
                    MigrationManager.this.logger.finest("Promotion commit result " + result + " from " + destination + " for migrations " + migrations);
                }
                return result;
            }
            catch (Throwable t) {
                this.logPromotionCommitFailure(destination, migrations, t);
                if (t.getCause() instanceof OperationTimeoutException) {
                    return this.commitPromotionsToDestination(destination, migrations);
                }
                return false;
            }
        }

        private void logPromotionCommitFailure(PartitionReplica destination, Collection<MigrationInfo> migrations, Throwable t) {
            boolean memberLeft = t instanceof MemberLeftException || t.getCause() instanceof TargetNotMemberException || t.getCause() instanceof HazelcastInstanceNotActiveException;
            int migrationsSize = migrations.size();
            if (memberLeft) {
                if (destination.isIdentical(MigrationManager.this.node.getLocalMember())) {
                    MigrationManager.this.logger.fine("Promotion commit failed for " + migrationsSize + " migrations since this node is shutting down.");
                    return;
                }
                if (MigrationManager.this.logger.isFinestEnabled()) {
                    MigrationManager.this.logger.warning("Promotion commit failed for " + migrations + " since destination " + destination + " left the cluster");
                } else {
                    MigrationManager.this.logger.warning("Promotion commit failed for " + (migrationsSize == 1 ? migrations.iterator().next() : migrationsSize + " migrations") + " since destination " + destination + " left the cluster");
                }
                return;
            }
            if (MigrationManager.this.logger.isFinestEnabled()) {
                MigrationManager.this.logger.severe("Promotion commit to " + destination + " failed for " + migrations, t);
            } else {
                MigrationManager.this.logger.severe("Promotion commit to " + destination + " failed for " + (migrationsSize == 1 ? migrations.iterator().next() : migrationsSize + " migrations"), t);
            }
        }
    }

    class MigrateTask
    implements MigrationRunnable {
        private final MigrationInfo migrationInfo;

        MigrateTask(MigrationInfo migrationInfo) {
            this.migrationInfo = migrationInfo;
            migrationInfo.setMaster(MigrationManager.this.node.getThisAddress());
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            if (!MigrationManager.this.partitionService.isLocalMemberMaster()) {
                return;
            }
            if (this.migrationInfo.getSource() == null && this.migrationInfo.getDestinationCurrentReplicaIndex() > 0 && this.migrationInfo.getDestinationNewReplicaIndex() == 0) {
                throw new AssertionError((Object)("Promotion migrations should be handled by " + RepairPartitionTableTask.class.getSimpleName() + "! -> " + this.migrationInfo));
            }
            Member partitionOwner = this.checkMigrationParticipantsAndGetPartitionOwner();
            if (partitionOwner == null) {
                return;
            }
            long start = System.nanoTime();
            try {
                this.beforeMigration();
                Boolean result = this.executeMigrateOperation(partitionOwner);
                this.processMigrationResult(partitionOwner, result);
            }
            catch (Throwable t) {
                Level level = this.migrationInfo.isValid() ? Level.WARNING : Level.FINE;
                MigrationManager.this.logger.log(level, "Error during " + this.migrationInfo, t);
                this.migrationOperationFailed(partitionOwner);
            }
            finally {
                MigrationManager.this.stats.recordMigrationTaskTime(System.nanoTime() - start);
            }
        }

        private void beforeMigration() {
            this.migrationInfo.setInitialPartitionVersion(MigrationManager.this.partitionStateManager.getVersion());
            MigrationManager.this.internalMigrationListener.onMigrationStart(InternalMigrationListener.MigrationParticipant.MASTER, this.migrationInfo);
            MigrationManager.this.partitionService.getPartitionEventManager().sendMigrationEvent(this.migrationInfo, MigrationEvent.MigrationStatus.STARTED);
            if (MigrationManager.this.logger.isFineEnabled()) {
                MigrationManager.this.logger.fine("Starting Migration: " + this.migrationInfo);
            }
        }

        private Member checkMigrationParticipantsAndGetPartitionOwner() {
            Member partitionOwner = this.getPartitionOwner();
            if (partitionOwner == null) {
                MigrationManager.this.logger.fine("Partition owner is null. Ignoring " + this.migrationInfo);
                this.triggerRepartitioningAfterMigrationFailure();
                return null;
            }
            if (this.migrationInfo.getSource() != null) {
                PartitionReplica source = this.migrationInfo.getSource();
                if (MigrationManager.this.node.getClusterService().getMember(source.address(), source.uuid()) == null) {
                    MigrationManager.this.logger.fine("Source is not a member anymore. Ignoring " + this.migrationInfo);
                    this.triggerRepartitioningAfterMigrationFailure();
                    return null;
                }
            }
            PartitionReplica destination = this.migrationInfo.getDestination();
            if (MigrationManager.this.node.getClusterService().getMember(destination.address(), destination.uuid()) == null) {
                MigrationManager.this.logger.fine("Destination is not a member anymore. Ignoring " + this.migrationInfo);
                this.triggerRepartitioningAfterMigrationFailure();
                return null;
            }
            return partitionOwner;
        }

        private Member getPartitionOwner() {
            InternalPartitionImpl partition = MigrationManager.this.partitionStateManager.getPartitionImpl(this.migrationInfo.getPartitionId());
            PartitionReplica owner = partition.getOwnerReplicaOrNull();
            if (owner == null) {
                if (this.migrationInfo.isValid()) {
                    MigrationManager.this.logger.severe("Skipping migration! Partition owner is not set! -> partitionId=" + this.migrationInfo.getPartitionId() + ", " + partition + " -VS- " + this.migrationInfo);
                }
                return null;
            }
            return MigrationManager.this.node.getClusterService().getMember(owner.address(), owner.uuid());
        }

        private void processMigrationResult(Member partitionOwner, Boolean result) {
            if (Boolean.TRUE.equals(result)) {
                if (MigrationManager.this.logger.isFineEnabled()) {
                    MigrationManager.this.logger.fine("Finished Migration: " + this.migrationInfo);
                }
                this.migrationOperationSucceeded();
            } else {
                Level level;
                Level level2 = level = MigrationManager.this.nodeEngine.isRunning() && this.migrationInfo.isValid() ? Level.WARNING : Level.FINE;
                if (MigrationManager.this.logger.isLoggable(level)) {
                    MigrationManager.this.logger.log(level, "Migration failed: " + this.migrationInfo);
                }
                this.migrationOperationFailed(partitionOwner);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Boolean executeMigrateOperation(Member fromMember) {
            long start = System.nanoTime();
            List<MigrationInfo> completedMigrations = Collections.emptyList();
            if (MigrationManager.this.node.getClusterService().getClusterVersion().isGreaterOrEqual(Versions.V3_12)) {
                completedMigrations = MigrationManager.this.getCompletedMigrationsCopy();
            }
            int partitionStateVersion = MigrationManager.this.partitionStateManager.getVersion();
            MigrationRequestOperation op = new MigrationRequestOperation(this.migrationInfo, completedMigrations, partitionStateVersion, MigrationManager.this.fragmentedMigrationEnabled);
            InternalCompletableFuture future = MigrationManager.this.nodeEngine.getOperationService().createInvocationBuilder("hz:core:partitionService", (Operation)op, fromMember.getAddress()).setCallTimeout(MigrationManager.this.partitionMigrationTimeout).setTryCount(12).setTryPauseMillis(10000L).invoke();
            try {
                Object response = future.get();
                Boolean bl = (Boolean)MigrationManager.this.nodeEngine.toObject(response);
                return bl;
            }
            catch (Throwable e) {
                Level level;
                Level level2 = level = MigrationManager.this.nodeEngine.isRunning() && this.migrationInfo.isValid() ? Level.WARNING : Level.FINE;
                if (e instanceof ExecutionException && e.getCause() instanceof PartitionStateVersionMismatchException) {
                    level = Level.FINE;
                }
                if (MigrationManager.this.logger.isLoggable(level)) {
                    MigrationManager.this.logger.log(level, "Failed migration from " + fromMember + " for " + this.migrationInfo, e);
                }
            }
            finally {
                MigrationManager.this.stats.recordMigrationOperationTime(System.nanoTime() - start);
            }
            return Boolean.FALSE;
        }

        private void migrationOperationFailed(Member partitionOwner) {
            this.migrationInfo.setStatus(MigrationInfo.MigrationStatus.FAILED);
            MigrationManager.this.internalMigrationListener.onMigrationComplete(InternalMigrationListener.MigrationParticipant.MASTER, this.migrationInfo, false);
            MigrationManager.this.partitionServiceLock.lock();
            try {
                MigrationManager.this.internalMigrationListener.onMigrationRollback(InternalMigrationListener.MigrationParticipant.MASTER, this.migrationInfo);
                MigrationManager.this.scheduleActiveMigrationFinalization(this.migrationInfo);
                int delta = this.migrationInfo.getPartitionVersionIncrement() + 1;
                MigrationManager.this.partitionStateManager.incrementVersion(delta);
                this.migrationInfo.setPartitionVersionIncrement(delta);
                MigrationManager.this.node.getNodeExtension().onPartitionStateChange();
                MigrationManager.this.addCompletedMigration(this.migrationInfo);
                if (!partitionOwner.localMember()) {
                    MigrationManager.this.partitionService.sendPartitionRuntimeState(partitionOwner.getAddress());
                }
                if (!this.migrationInfo.getDestination().isIdentical(MigrationManager.this.node.getLocalMember())) {
                    MigrationManager.this.partitionService.sendPartitionRuntimeState(this.migrationInfo.getDestination().address());
                }
                this.triggerRepartitioningAfterMigrationFailure();
            }
            finally {
                MigrationManager.this.partitionServiceLock.unlock();
            }
            MigrationManager.this.partitionService.getPartitionEventManager().sendMigrationEvent(this.migrationInfo, MigrationEvent.MigrationStatus.FAILED);
        }

        private void triggerRepartitioningAfterMigrationFailure() {
            MigrationManager.this.partitionServiceLock.lock();
            try {
                MigrationManager.this.pauseMigration();
                MigrationManager.this.triggerControlTask();
                MigrationManager.this.resumeMigrationEventually();
            }
            finally {
                MigrationManager.this.partitionServiceLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void migrationOperationSucceeded() {
            MigrationManager.this.internalMigrationListener.onMigrationComplete(InternalMigrationListener.MigrationParticipant.MASTER, this.migrationInfo, true);
            long start = System.nanoTime();
            boolean commitSuccessful = MigrationManager.this.commitMigrationToDestination(this.migrationInfo);
            MigrationManager.this.stats.recordDestinationCommitTime(System.nanoTime() - start);
            Version clusterVersion = MigrationManager.this.nodeEngine.getClusterService().getClusterVersion();
            MigrationManager.this.partitionServiceLock.lock();
            try {
                if (commitSuccessful) {
                    this.migrationInfo.setStatus(MigrationInfo.MigrationStatus.SUCCESS);
                    MigrationManager.this.internalMigrationListener.onMigrationCommit(InternalMigrationListener.MigrationParticipant.MASTER, this.migrationInfo);
                    assert (this.migrationInfo.getInitialPartitionVersion() == MigrationManager.this.partitionStateManager.getVersion()) : "Migration initial version: " + this.migrationInfo.getInitialPartitionVersion() + ", Partition state version: " + MigrationManager.access$1100(MigrationManager.this).getVersion();
                    InternalPartitionImpl partition = MigrationManager.this.partitionStateManager.getPartitionImpl(this.migrationInfo.getPartitionId());
                    MigrationManager.applyMigration(partition, this.migrationInfo);
                    assert (this.migrationInfo.getFinalPartitionVersion() == MigrationManager.this.partitionStateManager.getVersion()) : "Migration final version: " + this.migrationInfo.getFinalPartitionVersion() + ", Partition state version: " + MigrationManager.access$1100(MigrationManager.this).getVersion();
                } else {
                    this.migrationInfo.setStatus(MigrationInfo.MigrationStatus.FAILED);
                    MigrationManager.this.internalMigrationListener.onMigrationRollback(InternalMigrationListener.MigrationParticipant.MASTER, this.migrationInfo);
                    int delta = this.migrationInfo.getPartitionVersionIncrement() + 1;
                    MigrationManager.this.partitionStateManager.incrementVersion(delta);
                    this.migrationInfo.setPartitionVersionIncrement(delta);
                    if (!this.migrationInfo.getDestination().isIdentical(MigrationManager.this.node.getLocalMember())) {
                        MigrationManager.this.partitionService.sendPartitionRuntimeState(this.migrationInfo.getDestination().address());
                    }
                    this.triggerRepartitioningAfterMigrationFailure();
                }
                MigrationManager.this.addCompletedMigration(this.migrationInfo);
                MigrationManager.this.scheduleActiveMigrationFinalization(this.migrationInfo);
                MigrationManager.this.node.getNodeExtension().onPartitionStateChange();
                if (clusterVersion.isGreaterOrEqual(Versions.V3_12) && MigrationManager.this.completedMigrations.size() >= 10) {
                    MigrationManager.this.publishCompletedMigrations();
                }
            }
            finally {
                MigrationManager.this.partitionServiceLock.unlock();
            }
            if (clusterVersion.isLessThan(Versions.V3_12) && MigrationManager.this.partitionService.syncPartitionRuntimeState()) {
                MigrationManager.this.evictCompletedMigrations(this.migrationInfo);
            }
            PartitionEventManager partitionEventManager = MigrationManager.this.partitionService.getPartitionEventManager();
            partitionEventManager.sendMigrationEvent(this.migrationInfo, MigrationEvent.MigrationStatus.COMPLETED);
        }

        public String toString() {
            return this.getClass().getSimpleName() + "{migrationInfo=" + this.migrationInfo + '}';
        }
    }

    private class RepartitioningTask
    implements MigrationRunnable {
        private RepartitioningTask() {
        }

        @Override
        public void run() {
            if (!MigrationManager.this.partitionService.isLocalMemberMaster()) {
                return;
            }
            MigrationManager.this.partitionServiceLock.lock();
            try {
                MigrationManager.this.triggerRepartitioningWhenClusterStateAllowsMigration = !MigrationManager.this.node.getClusterService().getClusterState().isMigrationAllowed();
                if (MigrationManager.this.triggerRepartitioningWhenClusterStateAllowsMigration) {
                    if (MigrationManager.this.logger.isFineEnabled()) {
                        MigrationManager.this.logger.fine("Migrations are not allowed yet, repartitioning will be triggered when cluster state allows migrations.");
                    }
                    this.assignCompletelyLostPartitions();
                    return;
                }
                PartitionReplica[][] newState = this.repartition();
                if (newState == null) {
                    return;
                }
                MigrationManager.this.stats.markNewRepartition();
                this.processNewPartitionState(newState);
                MigrationManager.this.migrationQueue.add(new ProcessShutdownRequestsTask());
            }
            finally {
                MigrationManager.this.partitionServiceLock.unlock();
            }
        }

        private PartitionReplica[][] repartition() {
            if (!this.migrationsTasksAllowed()) {
                return null;
            }
            PartitionReplica[][] newState = MigrationManager.this.partitionStateManager.repartition(MigrationManager.this.shutdownRequestedMembers, null);
            if (newState == null) {
                MigrationManager.this.migrationQueue.add(new ProcessShutdownRequestsTask());
                return null;
            }
            if (!this.migrationsTasksAllowed()) {
                return null;
            }
            return newState;
        }

        private void assignCompletelyLostPartitions() {
            if (!MigrationManager.this.node.getClusterService().getClusterState().isPartitionPromotionAllowed()) {
                return;
            }
            MigrationManager.this.logger.fine("Cluster state doesn't allow repartitioning. RepartitioningTask will only assign lost partitions.");
            ArrayList<Integer> partitions = new ArrayList<Integer>();
            for (InternalPartition partition : MigrationManager.this.partitionStateManager.getPartitions()) {
                boolean empty = true;
                for (int index = 0; index < 7; ++index) {
                    empty &= partition.getReplica(index) == null;
                }
                if (!empty) continue;
                partitions.add(partition.getPartitionId());
            }
            if (!partitions.isEmpty()) {
                PartitionReplica[][] state = MigrationManager.this.partitionStateManager.repartition(MigrationManager.this.shutdownRequestedMembers, partitions);
                if (state != null) {
                    MigrationManager.this.logger.warning("Assigning new owners for " + partitions.size() + " LOST partitions!");
                    Iterator iterator = partitions.iterator();
                    while (iterator.hasNext()) {
                        InternalPartition partition;
                        int partitionId = (Integer)iterator.next();
                        partition = MigrationManager.this.partitionStateManager.getPartitionImpl(partitionId);
                        PartitionReplica[] replicas = state[partitionId];
                        this.assignLostPartitionOwner((InternalPartitionImpl)partition, replicas[0]);
                        ((InternalPartitionImpl)partition).setReplicas(replicas);
                    }
                } else {
                    MigrationManager.this.logger.warning("Unable to assign LOST partitions");
                }
            }
        }

        private void processNewPartitionState(PartitionReplica[][] newState) {
            MutableInteger lostCount = new MutableInteger();
            MutableInteger migrationCount = new MutableInteger();
            ArrayList<Queue<MigrationInfo>> migrations = new ArrayList<Queue<MigrationInfo>>(newState.length);
            for (int partitionId = 0; partitionId < newState.length; ++partitionId) {
                InternalPartitionImpl currentPartition = MigrationManager.this.partitionStateManager.getPartitionImpl(partitionId);
                Object[] currentReplicas = currentPartition.getReplicas();
                Object[] newReplicas = newState[partitionId];
                MigrationCollector migrationCollector = new MigrationCollector(currentPartition, migrationCount, lostCount);
                if (MigrationManager.this.logger.isFinestEnabled()) {
                    MigrationManager.this.logger.finest("Planning migrations for partitionId=" + partitionId + ". Current replicas: " + Arrays.toString(currentReplicas) + ", New replicas: " + Arrays.toString(newReplicas));
                }
                MigrationManager.this.migrationPlanner.planMigrations(partitionId, (PartitionReplica[])currentReplicas, (PartitionReplica[])newReplicas, migrationCollector);
                MigrationManager.this.migrationPlanner.prioritizeCopiesAndShiftUps(migrationCollector.migrations);
                migrations.add(migrationCollector.migrations);
            }
            MigrationManager.this.partitionService.publishPartitionRuntimeState();
            if (migrationCount.value > 0) {
                this.scheduleMigrations(migrations);
                MigrationManager.this.migrationQueue.add(new PublishCompletedMigrationsTask());
            }
            this.logMigrationStatistics(migrationCount.value, lostCount.value);
        }

        private void scheduleMigrations(List<Queue<MigrationInfo>> migrations) {
            boolean migrationScheduled;
            do {
                migrationScheduled = false;
                for (Queue<MigrationInfo> queue : migrations) {
                    MigrationInfo migration = queue.poll();
                    if (migration == null) continue;
                    migrationScheduled = true;
                    MigrationManager.this.scheduleMigration(migration);
                }
            } while (migrationScheduled);
        }

        private void logMigrationStatistics(int migrationCount, int lostCount) {
            if (lostCount > 0) {
                MigrationManager.this.logger.warning("Assigning new owners for " + lostCount + " LOST partitions!");
            }
            if (migrationCount > 0) {
                MigrationManager.this.logger.info("Re-partitioning cluster data... Migration queue size: " + migrationCount);
            } else {
                MigrationManager.this.logger.info("Partition balance is ok, no need to re-partition cluster data... ");
            }
        }

        private void assignLostPartitionOwner(InternalPartitionImpl partition, PartitionReplica newOwner) {
            int partitionId = partition.getPartitionId();
            MigrationInfo migrationInfo = new MigrationInfo(partitionId, null, newOwner, -1, -1, -1, 0);
            PartitionEventManager partitionEventManager = MigrationManager.this.partitionService.getPartitionEventManager();
            partitionEventManager.sendMigrationEvent(migrationInfo, MigrationEvent.MigrationStatus.STARTED);
            partition.setReplica(0, newOwner);
            partitionEventManager.sendMigrationEvent(migrationInfo, MigrationEvent.MigrationStatus.COMPLETED);
        }

        private boolean migrationsTasksAllowed() {
            boolean hasMigrationTasks;
            boolean migrationTasksAllowed = MigrationManager.this.areMigrationTasksAllowed();
            boolean bl = hasMigrationTasks = MigrationManager.this.migrationQueue.migrationTaskCount() > 1;
            if (migrationTasksAllowed && !hasMigrationTasks) {
                return true;
            }
            MigrationManager.this.triggerControlTask();
            return false;
        }

        private class MigrationCollector
        implements MigrationPlanner.MigrationDecisionCallback {
            private final int partitionId;
            private final InternalPartitionImpl partition;
            private final MutableInteger migrationCount;
            private final MutableInteger lostCount;
            private final LinkedList<MigrationInfo> migrations = new LinkedList();

            MigrationCollector(InternalPartitionImpl partition, MutableInteger migrationCount, MutableInteger lostCount) {
                this.partitionId = partition.getPartitionId();
                this.partition = partition;
                this.migrationCount = migrationCount;
                this.lostCount = lostCount;
            }

            @Override
            public void migrate(PartitionReplica source, int sourceCurrentReplicaIndex, int sourceNewReplicaIndex, PartitionReplica destination, int destinationCurrentReplicaIndex, int destinationNewReplicaIndex) {
                if (MigrationManager.this.logger.isFineEnabled()) {
                    MigrationManager.this.logger.fine("Planned migration -> partitionId=" + this.partitionId + ", source=" + source + ", sourceCurrentReplicaIndex=" + sourceCurrentReplicaIndex + ", sourceNewReplicaIndex=" + sourceNewReplicaIndex + ", destination=" + destination + ", destinationCurrentReplicaIndex=" + destinationCurrentReplicaIndex + ", destinationNewReplicaIndex=" + destinationNewReplicaIndex);
                }
                if (source == null && destinationCurrentReplicaIndex == -1 && destinationNewReplicaIndex == 0) {
                    assert (destination != null) : "partitionId=" + this.partitionId + " destination is null";
                    assert (sourceCurrentReplicaIndex == -1) : "partitionId=" + this.partitionId + " invalid index: " + sourceCurrentReplicaIndex;
                    assert (sourceNewReplicaIndex == -1) : "partitionId=" + this.partitionId + " invalid index: " + sourceNewReplicaIndex;
                    ++this.lostCount.value;
                    RepartitioningTask.this.assignLostPartitionOwner(this.partition, destination);
                } else if (destination == null && sourceNewReplicaIndex == -1) {
                    assert (source != null) : "partitionId=" + this.partitionId + " source is null";
                    assert (sourceCurrentReplicaIndex != -1) : "partitionId=" + this.partitionId + " invalid index: " + sourceCurrentReplicaIndex;
                    assert (sourceCurrentReplicaIndex != 0) : "partitionId=" + this.partitionId + " invalid index: " + sourceCurrentReplicaIndex;
                    PartitionReplica currentSource = this.partition.getReplica(sourceCurrentReplicaIndex);
                    assert (source.equals(currentSource)) : "partitionId=" + this.partitionId + " current source=" + source + " is different than expected source=" + source;
                    this.partition.setReplica(sourceCurrentReplicaIndex, null);
                } else {
                    MigrationInfo migration = new MigrationInfo(this.partitionId, source, destination, sourceCurrentReplicaIndex, sourceNewReplicaIndex, destinationCurrentReplicaIndex, destinationNewReplicaIndex);
                    ++this.migrationCount.value;
                    this.migrations.add(migration);
                }
            }
        }
    }
}

