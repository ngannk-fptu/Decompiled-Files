/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterStateChange;
import com.hazelcast.internal.cluster.impl.ClusterStateTransactionLogRecord;
import com.hazelcast.internal.cluster.impl.MemberMap;
import com.hazelcast.internal.cluster.impl.VersionMismatchException;
import com.hazelcast.internal.cluster.impl.operations.LockClusterStateOp;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.util.LockGuard;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.transaction.impl.TransactionManagerServiceImpl;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.version.Version;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

public class ClusterStateManager {
    private static final TransactionOptions DEFAULT_TX_OPTIONS = new TransactionOptions().setDurability(1).setTimeout(1L, TimeUnit.MINUTES).setTransactionType(TransactionOptions.TransactionType.TWO_PHASE);
    private static final long LOCK_LEASE_EXTENSION_MILLIS = TimeUnit.SECONDS.toMillis(20L);
    volatile Version clusterVersion = Version.UNKNOWN;
    private final Node node;
    private final ILogger logger;
    private final Lock clusterServiceLock;
    private final AtomicReference<LockGuard> stateLockRef = new AtomicReference<LockGuard>(LockGuard.NOT_LOCKED);
    private volatile ClusterState state = ClusterState.ACTIVE;

    ClusterStateManager(Node node, Lock clusterServiceLock) {
        this.node = node;
        this.clusterServiceLock = clusterServiceLock;
        this.logger = node.getLogger(this.getClass());
    }

    public ClusterState getState() {
        LockGuard stateLock = this.getStateLock();
        return stateLock.isLocked() ? ClusterState.IN_TRANSITION : this.state;
    }

    public Version getClusterVersion() {
        return this.clusterVersion;
    }

    LockGuard getStateLock() {
        LockGuard stateLock = this.stateLockRef.get();
        while (stateLock.isLeaseExpired()) {
            if (this.stateLockRef.compareAndSet(stateLock, LockGuard.NOT_LOCKED)) {
                this.logger.fine("Cluster state lock: " + stateLock + " is expired.");
                stateLock = LockGuard.NOT_LOCKED;
                break;
            }
            stateLock = this.stateLockRef.get();
        }
        return stateLock;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void initialClusterState(ClusterState initialState, Version version) {
        this.clusterServiceLock.lock();
        try {
            this.node.getNodeExtension().onInitialClusterState(initialState);
            ClusterState currentState = this.getState();
            if (currentState != ClusterState.ACTIVE && currentState != initialState) {
                this.logger.warning("Initial state is already set! Current state: " + (Object)((Object)currentState) + ", Given state: " + (Object)((Object)initialState));
                return;
            }
            this.logger.fine("Setting initial cluster state: " + (Object)((Object)initialState) + " and version: " + version);
            this.validateNodeCompatibleWith(version);
            this.setClusterStateAndVersion(initialState, version, true);
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    void setClusterState(ClusterState newState, boolean isTransient) {
        this.clusterServiceLock.lock();
        try {
            this.doSetClusterState(newState, isTransient);
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    public void setClusterVersion(Version newVersion) {
        this.clusterServiceLock.lock();
        try {
            this.doSetClusterVersion(newVersion);
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    private void setClusterStateAndVersion(ClusterState newState, Version newVersion, boolean isTransient) {
        this.state = newState;
        this.clusterVersion = newVersion;
        this.stateLockRef.set(LockGuard.NOT_LOCKED);
        this.changeNodeState(newState);
        this.node.getNodeExtension().onClusterStateChange(newState, isTransient);
        this.node.getNodeExtension().onClusterVersionChange(newVersion);
    }

    private void doSetClusterState(ClusterState newState, boolean isTransient) {
        this.state = newState;
        this.stateLockRef.set(LockGuard.NOT_LOCKED);
        this.changeNodeState(newState);
        this.node.getNodeExtension().onClusterStateChange(newState, isTransient);
    }

    private void doSetClusterVersion(Version newVersion) {
        this.clusterVersion = newVersion;
        this.stateLockRef.set(LockGuard.NOT_LOCKED);
        this.node.getNodeExtension().onClusterVersionChange(newVersion);
    }

    void reset() {
        this.clusterServiceLock.lock();
        try {
            this.state = ClusterState.ACTIVE;
            this.clusterVersion = Version.UNKNOWN;
            this.stateLockRef.set(LockGuard.NOT_LOCKED);
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void lockClusterState(ClusterStateChange stateChange, Address initiator, String txnId, long leaseTime, int memberListVersion, int partitionStateVersion) {
        Preconditions.checkNotNull(stateChange);
        this.clusterServiceLock.lock();
        try {
            if (!this.node.getNodeExtension().isStartCompleted()) {
                throw new IllegalStateException("Can not lock cluster state! Startup is not completed yet!");
            }
            if (this.node.getClusterService().getClusterJoinManager().isMastershipClaimInProgress()) {
                throw new IllegalStateException("Can not lock cluster state! Mastership claim is in progress!");
            }
            if (stateChange.isOfType(Version.class)) {
                this.validateNodeCompatibleWith((Version)stateChange.getNewState());
                this.validateClusterVersionChange((Version)stateChange.getNewState());
            }
            this.checkMemberListVersion(memberListVersion);
            this.checkMigrationsAndPartitionStateVersion(stateChange, partitionStateVersion);
            this.lockOrExtendClusterState(initiator, txnId, leaseTime);
            try {
                this.checkMigrationsAndPartitionStateVersion(stateChange, partitionStateVersion);
            }
            catch (IllegalStateException e) {
                this.stateLockRef.set(LockGuard.NOT_LOCKED);
                throw e;
            }
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    private void checkMemberListVersion(int memberListVersion) {
        int thisMemberListVersion = this.node.getClusterService().getMemberListVersion();
        if (memberListVersion != thisMemberListVersion) {
            throw new IllegalStateException("Can not lock cluster state! Member list versions are not matching! Expected version: " + memberListVersion + ", Current version: " + thisMemberListVersion);
        }
    }

    private void lockOrExtendClusterState(Address initiator, String txnId, long leaseTime) {
        Preconditions.checkPositive(leaseTime, "Lease time should be positive!");
        LockGuard currentLock = this.getStateLock();
        if (!currentLock.allowsLock(txnId)) {
            throw new TransactionException("Locking failed for " + initiator + ", tx: " + txnId + ", current state: " + this.toString());
        }
        long newLeaseTime = currentLock.getRemainingTime() + leaseTime;
        if (newLeaseTime < 0L) {
            newLeaseTime = Long.MAX_VALUE;
        }
        this.stateLockRef.set(new LockGuard(initiator, txnId, newLeaseTime));
    }

    private void validateNodeCompatibleWith(Version clusterVersion) {
        if (!this.node.getNodeExtension().isNodeVersionCompatibleWith(clusterVersion)) {
            throw new VersionMismatchException("Node's codebase version " + this.node.getVersion() + " is incompatible with the requested cluster version " + clusterVersion);
        }
    }

    private void validateClusterVersionChange(Version newClusterVersion) {
        if (!this.clusterVersion.isUnknown() && this.clusterVersion.getMajor() != newClusterVersion.getMajor()) {
            throw new IllegalArgumentException("Transition to requested version " + newClusterVersion + " not allowed for current cluster version " + this.clusterVersion);
        }
    }

    private void checkMigrationsAndPartitionStateVersion(ClusterStateChange stateChange, int partitionStateVersion) {
        InternalPartitionService partitionService = this.node.getPartitionService();
        int thisPartitionStateVersion = partitionService.getPartitionStateVersion();
        if (partitionService.hasOnGoingMigrationLocal()) {
            throw new IllegalStateException("Still have pending migration tasks, cannot lock cluster state! New state: " + stateChange + ", current state: " + (Object)((Object)this.getState()));
        }
        if (partitionStateVersion != thisPartitionStateVersion) {
            throw new IllegalStateException("Can not lock cluster state! Partition tables have different versions! Expected version: " + partitionStateVersion + " Current version: " + thisPartitionStateVersion);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean rollbackClusterState(String txnId) {
        this.clusterServiceLock.lock();
        try {
            LockGuard currentLock = this.getStateLock();
            if (!currentLock.allowsUnlock(txnId)) {
                boolean bl = false;
                return bl;
            }
            this.logger.fine("Rolling back cluster state transaction: " + txnId);
            this.stateLockRef.set(LockGuard.NOT_LOCKED);
            if (this.state.isJoinAllowed()) {
                this.node.getClusterService().getMembershipManager().removeAllMissingMembers();
            }
            boolean bl = true;
            return bl;
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    void commitClusterState(ClusterStateChange newState, Address initiator, String txnId) {
        this.commitClusterState(newState, initiator, txnId, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void commitClusterState(ClusterStateChange stateChange, Address initiator, String txnId, boolean isTransient) {
        block7: {
            Preconditions.checkNotNull(stateChange);
            stateChange.validate();
            this.clusterServiceLock.lock();
            try {
                LockGuard stateLock = this.getStateLock();
                if (!stateLock.allowsUnlock(txnId)) {
                    throw new TransactionException("Cluster state change [" + (Object)((Object)this.state) + " -> " + stateChange + "] failed for " + initiator + ", current state: " + this.stateToString());
                }
                if (stateChange.isOfType(ClusterState.class)) {
                    ClusterState newState = (ClusterState)((Object)stateChange.getNewState());
                    this.doSetClusterState(newState, isTransient);
                    if (newState.isJoinAllowed()) {
                        this.node.getClusterService().getMembershipManager().removeAllMissingMembers();
                    }
                    break block7;
                }
                if (stateChange.isOfType(Version.class)) {
                    Version newVersion = (Version)stateChange.getNewState();
                    this.logger.info("Cluster version set to " + newVersion);
                    this.doSetClusterVersion(newVersion);
                    break block7;
                }
                throw new IllegalArgumentException("Illegal ClusterStateChange of type " + stateChange.getType() + ".");
            }
            finally {
                this.clusterServiceLock.unlock();
            }
        }
    }

    private void changeNodeState(ClusterState newState) {
        if (newState == ClusterState.PASSIVE) {
            this.node.changeNodeStateToPassive();
        } else {
            this.node.changeNodeStateToActive();
        }
    }

    void changeClusterState(ClusterStateChange stateChange, MemberMap memberMap, int partitionStateVersion, boolean isTransient) {
        this.changeClusterState(stateChange, memberMap, DEFAULT_TX_OPTIONS, partitionStateVersion, isTransient);
    }

    void changeClusterState(ClusterStateChange stateChange, MemberMap memberMap, TransactionOptions options, int partitionStateVersion, boolean isTransient) {
        this.checkParameters(stateChange, options);
        if (this.isCurrentStateEqualToRequestedOne(stateChange)) {
            return;
        }
        ClusterState oldState = this.getState();
        ClusterState requestedState = stateChange.getClusterStateOrNull();
        NodeEngineImpl nodeEngine = this.node.getNodeEngine();
        TransactionManagerServiceImpl txManagerService = (TransactionManagerServiceImpl)nodeEngine.getTransactionManagerService();
        Transaction tx = txManagerService.newAllowedDuringPassiveStateTransaction(options);
        this.notifyBeforeStateChange(oldState, requestedState, isTransient);
        tx.begin();
        try {
            String txnId = tx.getTxnId();
            Set<MemberImpl> members = memberMap.getMembers();
            int memberListVersion = memberMap.getVersion();
            this.addTransactionRecords(stateChange, tx, members, memberListVersion, partitionStateVersion, isTransient);
            this.lockClusterStateOnAllMembers(stateChange, nodeEngine, options.getTimeoutMillis(), txnId, members, memberListVersion, partitionStateVersion);
            this.checkMemberListChange(memberListVersion);
            tx.prepare();
        }
        catch (Throwable e) {
            tx.rollback();
            this.notifyAfterStateChange(oldState, requestedState, isTransient);
            if (e instanceof TargetNotMemberException || e.getCause() instanceof MemberLeftException) {
                throw new IllegalStateException("Cluster members changed during state change!", e);
            }
            throw ExceptionUtil.rethrow(e);
        }
        try {
            tx.commit();
        }
        catch (Throwable e) {
            if (e instanceof TargetNotMemberException || e.getCause() instanceof MemberLeftException) {
                return;
            }
            throw ExceptionUtil.rethrow(e);
        }
        finally {
            this.notifyAfterStateChange(oldState, requestedState, isTransient);
        }
    }

    private void notifyBeforeStateChange(ClusterState oldState, ClusterState requestedState, boolean isTransient) {
        if (requestedState != null) {
            this.node.getNodeExtension().beforeClusterStateChange(oldState, requestedState, isTransient);
        }
    }

    private void notifyAfterStateChange(ClusterState oldState, ClusterState requestedState, boolean isTransient) {
        if (requestedState != null) {
            this.node.getNodeExtension().afterClusterStateChange(oldState, this.getState(), isTransient);
        }
    }

    private boolean isCurrentStateEqualToRequestedOne(ClusterStateChange change) {
        if (change.isOfType(ClusterState.class)) {
            return this.getState() == change.getNewState();
        }
        if (change.isOfType(Version.class)) {
            return this.clusterVersion != null && this.clusterVersion.equals(change.getNewState());
        }
        return false;
    }

    private void lockClusterStateOnAllMembers(ClusterStateChange stateChange, NodeEngineImpl nodeEngine, long leaseTime, String txnId, Collection<MemberImpl> members, int memberListVersion, int partitionStateVersion) {
        ArrayList futures = new ArrayList(members.size());
        Address thisAddress = this.node.getThisAddress();
        for (Member member : members) {
            LockClusterStateOp op = new LockClusterStateOp(stateChange, thisAddress, txnId, leaseTime, memberListVersion, partitionStateVersion);
            InternalCompletableFuture future = nodeEngine.getOperationService().invokeOnTarget("hz:core:clusterService", op, member.getAddress());
            futures.add(future);
        }
        StateManagerExceptionHandler exceptionHandler = new StateManagerExceptionHandler(this.logger);
        FutureUtil.waitWithDeadline(futures, leaseTime, TimeUnit.MILLISECONDS, exceptionHandler);
        exceptionHandler.rethrowIfFailed();
    }

    private void addTransactionRecords(ClusterStateChange stateChange, Transaction tx, Collection<MemberImpl> members, int memberListVersion, int partitionStateVersion, boolean isTransient) {
        long leaseTime = Math.min(tx.getTimeoutMillis(), LOCK_LEASE_EXTENSION_MILLIS);
        for (Member member : members) {
            tx.add(new ClusterStateTransactionLogRecord(stateChange, this.node.getThisAddress(), member.getAddress(), tx.getTxnId(), leaseTime, memberListVersion, partitionStateVersion, isTransient));
        }
    }

    private void checkMemberListChange(int initialMemberListVersion) {
        int currentMemberListVersion = this.node.getClusterService().getMembershipManager().getMemberListVersion();
        if (initialMemberListVersion != currentMemberListVersion) {
            throw new IllegalStateException("Cluster members changed during state change! Initial version: " + initialMemberListVersion + ", Current version: " + currentMemberListVersion);
        }
    }

    private void checkParameters(ClusterStateChange newState, TransactionOptions options) {
        Preconditions.checkNotNull(newState);
        Preconditions.checkNotNull(options);
        newState.validate();
        if (options.getTransactionType() != TransactionOptions.TransactionType.TWO_PHASE) {
            throw new IllegalArgumentException("Changing cluster state requires 2PC transaction!");
        }
    }

    public String stateToString() {
        return "ClusterState{state=" + (Object)((Object)this.state) + ", lock=" + this.stateLockRef.get() + '}';
    }

    public String toString() {
        return "ClusterStateManager{stateLockRef=" + this.stateLockRef + ", state=" + (Object)((Object)this.state) + '}';
    }

    private static final class StateManagerExceptionHandler
    implements FutureUtil.ExceptionHandler {
        private final ILogger logger;
        private Throwable error;

        private StateManagerExceptionHandler(ILogger logger) {
            this.logger = logger;
        }

        @Override
        public void handleException(Throwable throwable) {
            Throwable cause = throwable;
            if (throwable instanceof ExecutionException && throwable.getCause() != null) {
                cause = throwable.getCause();
            }
            if (this.error == null) {
                this.error = cause;
            }
            this.log(cause);
        }

        private void log(Throwable cause) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("failure during cluster state change", cause);
            }
        }

        void rethrowIfFailed() {
            if (this.error != null) {
                throw ExceptionUtil.rethrow(this.error);
            }
        }
    }
}

