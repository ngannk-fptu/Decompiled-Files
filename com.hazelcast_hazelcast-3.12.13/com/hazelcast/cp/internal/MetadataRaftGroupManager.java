/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.cp.internal;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.core.Member;
import com.hazelcast.cp.CPGroup;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.CPMember;
import com.hazelcast.cp.exception.CPGroupDestroyedException;
import com.hazelcast.cp.internal.CPGroupInfo;
import com.hazelcast.cp.internal.CPMemberInfo;
import com.hazelcast.cp.internal.MembershipChangeSchedule;
import com.hazelcast.cp.internal.MetadataRaftGroupSnapshot;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftGroupMembershipManager;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.exception.CannotCreateRaftGroupException;
import com.hazelcast.cp.internal.exception.CannotRemoveCPMemberException;
import com.hazelcast.cp.internal.exception.MetadataRaftGroupInitInProgressException;
import com.hazelcast.cp.internal.raft.SnapshotAwareService;
import com.hazelcast.cp.internal.raft.impl.RaftNode;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raftop.metadata.CreateRaftNodeOp;
import com.hazelcast.cp.internal.raftop.metadata.DestroyRaftNodesOp;
import com.hazelcast.cp.internal.raftop.metadata.GetRaftGroupIdsOp;
import com.hazelcast.cp.internal.raftop.metadata.InitMetadataRaftGroupOp;
import com.hazelcast.cp.internal.raftop.metadata.PublishActiveCPMembersOp;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.impl.RaftInvocationContext;
import com.hazelcast.util.Clock;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.RandomPicker;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MetadataRaftGroupManager
implements SnapshotAwareService<MetadataRaftGroupSnapshot> {
    public static final RaftGroupId INITIAL_METADATA_GROUP_ID = new RaftGroupId("METADATA", 0L, 0L);
    private static final int RANDOM_COMMIT_RANGE = 20;
    private static final int RANDOM_COMMIT_TIMEOUT_SECS = 60;
    private static final long DISCOVER_INITIAL_CP_MEMBERS_TASK_DELAY_MILLIS = 1000L;
    private static final long DISCOVER_INITIAL_CP_MEMBERS_TASK_LOGGING_DELAY_MILLIS = 5000L;
    private static final long BROADCAST_ACTIVE_CP_MEMBERS_TASK_PERIOD_SECONDS = 10L;
    private final NodeEngine nodeEngine;
    private final RaftService raftService;
    private final ILogger logger;
    private final CPSubsystemConfig config;
    private final AtomicReference<CPMemberInfo> localCPMember = new AtomicReference();
    private final AtomicReference<RaftGroupId> metadataGroupIdRef = new AtomicReference<RaftGroupId>(INITIAL_METADATA_GROUP_ID);
    private final AtomicBoolean discoveryCompleted = new AtomicBoolean();
    private volatile DiscoverInitialCPMembersTask currentDiscoveryTask;
    private final ConcurrentMap<CPGroupId, CPGroupInfo> groups = new ConcurrentHashMap<CPGroupId, CPGroupInfo>();
    private volatile Collection<CPMemberInfo> activeMembers = Collections.emptySet();
    private volatile long activeMembersCommitIndex;
    private volatile List<CPMemberInfo> initialCPMembers;
    private volatile MembershipChangeSchedule membershipChangeSchedule;
    private volatile MetadataRaftGroupInitStatus initializationStatus = MetadataRaftGroupInitStatus.IN_PROGRESS;
    private final Set<CPMemberInfo> initializedCPMembers = Collections.newSetFromMap(new ConcurrentHashMap());
    private final Set<Long> initializationCommitIndices = Collections.newSetFromMap(new ConcurrentHashMap());

    MetadataRaftGroupManager(NodeEngine nodeEngine, RaftService raftService, CPSubsystemConfig config) {
        this.nodeEngine = nodeEngine;
        this.raftService = raftService;
        this.logger = nodeEngine.getLogger(this.getClass());
        this.config = config;
    }

    boolean init() {
        boolean cpSubsystemEnabled;
        boolean bl = cpSubsystemEnabled = this.config.getCPMemberCount() > 0;
        if (cpSubsystemEnabled) {
            this.scheduleDiscoverInitialCPMembersTask(true);
        } else {
            this.disableDiscovery();
        }
        return cpSubsystemEnabled;
    }

    void initPromotedCPMember(CPMemberInfo member) {
        if (!this.localCPMember.compareAndSet(null, member)) {
            return;
        }
        this.scheduleRaftGroupMembershipManagementTasks();
    }

    private void scheduleRaftGroupMembershipManagementTasks() {
        ExecutionService executionService = this.nodeEngine.getExecutionService();
        executionService.scheduleWithRepetition(new BroadcastActiveCPMembersTask(), 10L, 10L, TimeUnit.SECONDS);
        RaftGroupMembershipManager membershipManager = new RaftGroupMembershipManager(this.nodeEngine, this.raftService);
        membershipManager.init();
    }

    void restart(long seed) {
        this.activeMembers = Collections.emptySet();
        this.activeMembersCommitIndex = 0L;
        this.groups.clear();
        this.initialCPMembers = null;
        this.initializationStatus = MetadataRaftGroupInitStatus.IN_PROGRESS;
        this.initializedCPMembers.clear();
        this.initializationCommitIndices.clear();
        this.membershipChangeSchedule = null;
        this.metadataGroupIdRef.set(new RaftGroupId("METADATA", seed, 0L));
        this.localCPMember.set(null);
        DiscoverInitialCPMembersTask discoveryTask = this.currentDiscoveryTask;
        if (discoveryTask != null) {
            discoveryTask.cancelAndAwaitCompletion();
        }
        this.discoveryCompleted.set(false);
        this.scheduleDiscoverInitialCPMembersTask(false);
    }

    @Override
    public MetadataRaftGroupSnapshot takeSnapshot(CPGroupId groupId, long commitIndex) {
        if (!this.getMetadataGroupId().equals(groupId)) {
            return null;
        }
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Taking snapshot for commit-index: " + commitIndex);
        }
        MetadataRaftGroupSnapshot snapshot = new MetadataRaftGroupSnapshot();
        snapshot.setMembers(this.activeMembers);
        snapshot.setMembersCommitIndex(this.activeMembersCommitIndex);
        snapshot.setGroups(this.groups.values());
        snapshot.setMembershipChangeSchedule(this.membershipChangeSchedule);
        snapshot.setInitialCPMembers(this.initialCPMembers);
        snapshot.setInitializedCPMembers(this.initializedCPMembers);
        snapshot.setInitializationStatus(this.initializationStatus);
        snapshot.setInitializationCommitIndices(this.initializationCommitIndices);
        return snapshot;
    }

    @Override
    public void restoreSnapshot(CPGroupId groupId, long commitIndex, MetadataRaftGroupSnapshot snapshot) {
        this.ensureMetadataGroupId(groupId);
        Preconditions.checkNotNull(snapshot);
        HashSet<RaftGroupId> snapshotGroupIds = new HashSet<RaftGroupId>();
        for (CPGroupInfo group : snapshot.getGroups()) {
            this.groups.put(group.id(), group);
            snapshotGroupIds.add(group.id());
        }
        Iterator it = this.groups.keySet().iterator();
        while (it.hasNext()) {
            if (snapshotGroupIds.contains(it.next())) continue;
            it.remove();
        }
        this.doSetActiveMembers(snapshot.getMembersCommitIndex(), new LinkedHashSet<CPMemberInfo>(snapshot.getMembers()));
        this.membershipChangeSchedule = snapshot.getMembershipChangeSchedule();
        this.initialCPMembers = snapshot.getInitialCPMembers();
        this.initializedCPMembers.clear();
        this.initializedCPMembers.addAll(snapshot.getInitializedCPMembers());
        this.initializationStatus = snapshot.getInitializationStatus();
        this.initializationCommitIndices.clear();
        this.initializationCommitIndices.addAll(snapshot.getInitializationCommitIndices());
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Restored snapshot at commit-index: " + commitIndex);
        }
    }

    private void ensureMetadataGroupId(CPGroupId groupId) {
        RaftGroupId metadataGroupId = this.getMetadataGroupId();
        Preconditions.checkTrue(((Object)metadataGroupId).equals(groupId), "Invalid RaftGroupId! Expected: " + metadataGroupId + ", Actual: " + groupId);
    }

    CPMemberInfo getLocalCPMember() {
        return this.localCPMember.get();
    }

    public RaftGroupId getMetadataGroupId() {
        return this.metadataGroupIdRef.get();
    }

    long getGroupIdSeed() {
        return this.getMetadataGroupId().seed();
    }

    public Collection<CPGroupId> getGroupIds() {
        ArrayList<CPGroupId> groupIds = new ArrayList<CPGroupId>(this.groups.keySet());
        Collections.sort(groupIds, new CPGroupIdComparator());
        return groupIds;
    }

    public Collection<CPGroupId> getActiveGroupIds() {
        ArrayList<CPGroupId> activeGroupIds = new ArrayList<CPGroupId>(1);
        for (CPGroupInfo group : this.groups.values()) {
            if (group.status() != CPGroup.CPGroupStatus.ACTIVE) continue;
            activeGroupIds.add(group.id());
        }
        Collections.sort(activeGroupIds, new CPGroupIdComparator());
        return activeGroupIds;
    }

    public CPGroupInfo getGroup(CPGroupId groupId) {
        Preconditions.checkNotNull(groupId);
        if (groupId instanceof RaftGroupId && ((RaftGroupId)groupId).seed() < this.getGroupIdSeed()) {
            throw new CPGroupDestroyedException(groupId);
        }
        return (CPGroupInfo)this.groups.get(groupId);
    }

    public CPGroupInfo getActiveGroup(String groupName) {
        for (CPGroupInfo group : this.groups.values()) {
            if (group.status() != CPGroup.CPGroupStatus.ACTIVE || !group.name().equals(groupName)) continue;
            return group;
        }
        return null;
    }

    public boolean initMetadataGroup(long commitIndex, CPMemberInfo callerCPMember, List<CPMemberInfo> discoveredCPMembers, long expectedGroupIdSeed) {
        long groupIdSeed;
        String msg;
        Preconditions.checkNotNull(discoveredCPMembers);
        if (this.initializationStatus == MetadataRaftGroupInitStatus.FAILED) {
            String msg2 = callerCPMember + "committed CP member list: " + discoveredCPMembers + " after CP subsystem discovery has already failed.";
            this.logger.severe(msg2);
            throw new IllegalArgumentException(msg2);
        }
        if (discoveredCPMembers.size() != this.config.getCPMemberCount()) {
            msg = callerCPMember + "'s discovered CP member list: " + discoveredCPMembers + " must consist of " + this.config.getCPMemberCount() + " CP members";
            this.failMetadataRaftGroupInitializationIfNotCompletedAndThrow(msg);
        }
        if (!(this.initialCPMembers == null || this.initialCPMembers.size() == discoveredCPMembers.size() && this.initialCPMembers.containsAll(discoveredCPMembers))) {
            msg = "Invalid initial CP members! Expected: " + this.initialCPMembers + ", Actual: " + discoveredCPMembers;
            this.failMetadataRaftGroupInitializationIfNotCompletedAndThrow(msg);
        }
        if ((groupIdSeed = this.getGroupIdSeed()) != expectedGroupIdSeed) {
            String msg3 = "Cannot create METADATA CP group. Local groupId seed: " + groupIdSeed + ", expected groupId seed: " + expectedGroupIdSeed;
            this.failMetadataRaftGroupInitializationIfNotCompletedAndThrow(msg3);
        }
        List<CPMemberInfo> discoveredMetadataMembers = discoveredCPMembers.subList(0, this.config.getGroupSize());
        CPGroupInfo metadataGroup = new CPGroupInfo(this.getMetadataGroupId(), discoveredMetadataMembers);
        CPGroupInfo existingMetadataGroup = this.groups.putIfAbsent(this.getMetadataGroupId(), metadataGroup);
        if (existingMetadataGroup != null) {
            Collection<CPMember> metadataMembers = existingMetadataGroup.initialMembers();
            if (discoveredMetadataMembers.size() != metadataMembers.size() || !metadataMembers.containsAll(discoveredMetadataMembers)) {
                String msg4 = "Cannot create METADATA CP group with " + this.config.getCPMemberCount() + " because it already exists with a different member list: " + existingMetadataGroup;
                this.failMetadataRaftGroupInitializationIfNotCompletedAndThrow(msg4);
            }
        }
        if (this.initializationStatus == MetadataRaftGroupInitStatus.SUCCESSFUL) {
            return true;
        }
        this.initializationCommitIndices.add(commitIndex);
        if (!this.initializedCPMembers.add(callerCPMember)) {
            return false;
        }
        this.logger.fine("METADATA " + metadataGroup + " initialization is committed for " + callerCPMember + " with seed: " + expectedGroupIdSeed + " and discovered CP members: " + discoveredCPMembers);
        if (this.initializedCPMembers.size() == this.config.getCPMemberCount()) {
            this.initializationCommitIndices.remove(commitIndex);
            this.logger.fine("METADATA " + metadataGroup + " initialization is completed with: " + this.initializedCPMembers);
            this.initializationStatus = MetadataRaftGroupInitStatus.SUCCESSFUL;
            this.completeFutures(this.getMetadataGroupId(), this.initializationCommitIndices, null);
            this.initializedCPMembers.clear();
            this.initializationCommitIndices.clear();
            return true;
        }
        if (this.initialCPMembers != null) {
            return false;
        }
        LinkedHashSet<CPMemberInfo> cpMembers = new LinkedHashSet<CPMemberInfo>(discoveredCPMembers);
        this.initialCPMembers = Collections.unmodifiableList(new ArrayList<CPMemberInfo>(cpMembers));
        this.doSetActiveMembers(commitIndex, cpMembers);
        return false;
    }

    private void failMetadataRaftGroupInitializationIfNotCompletedAndThrow(String error) {
        this.logger.severe(error);
        IllegalArgumentException exception = new IllegalArgumentException(error);
        if (this.initializationStatus == MetadataRaftGroupInitStatus.IN_PROGRESS) {
            this.initializationStatus = MetadataRaftGroupInitStatus.FAILED;
            this.completeFutures(this.getMetadataGroupId(), this.initializationCommitIndices, exception);
            this.initializedCPMembers.clear();
            this.initializationCommitIndices.clear();
        }
        throw exception;
    }

    public CPGroupId createRaftGroup(String groupName, Collection<CPMemberInfo> members, long commitIndex) {
        Preconditions.checkFalse("METADATA".equalsIgnoreCase(groupName), groupName + " is reserved for internal usage!");
        this.checkMetadataGroupInitSuccessful();
        CPGroupInfo group = this.getRaftGroupByName(groupName);
        if (group != null) {
            if (group.memberCount() == members.size()) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("CP group " + groupName + " already exists.");
                }
                return group.id();
            }
            String msg = group.getId() + " already exists with a different size: " + group.memberCount();
            this.logger.severe(msg);
            throw new IllegalStateException(msg);
        }
        CPMemberInfo leavingMember = this.membershipChangeSchedule != null ? this.membershipChangeSchedule.getLeavingMember() : null;
        for (CPMemberInfo member : members) {
            if (!member.equals(leavingMember) && this.activeMembers.contains(member)) continue;
            String msg = "Cannot create CP group: " + groupName + " since " + member + " is not active";
            if (this.logger.isFineEnabled()) {
                this.logger.fine(msg);
            }
            throw new CannotCreateRaftGroupException(msg);
        }
        return this.createRaftGroup(new CPGroupInfo(new RaftGroupId(groupName, this.getGroupIdSeed(), commitIndex), members));
    }

    private CPGroupId createRaftGroup(CPGroupInfo group) {
        this.addRaftGroup(group);
        this.logger.info("New " + group.id() + " is created with " + group.members());
        RaftGroupId groupId = group.id();
        if (group.containsMember(this.getLocalCPMember())) {
            this.raftService.createRaftNode(groupId, group.memberImpls());
        } else {
            OperationService operationService = this.nodeEngine.getOperationService();
            CPGroupInfo metadataGroup = (CPGroupInfo)this.groups.get(this.getMetadataGroupId());
            for (CPMemberInfo member : group.memberImpls()) {
                if (metadataGroup.containsMember(member)) continue;
                CreateRaftNodeOp op = new CreateRaftNodeOp(group.id(), group.initialMembers());
                operationService.send(op, member.getAddress());
            }
        }
        return groupId;
    }

    private void addRaftGroup(CPGroupInfo group) {
        RaftGroupId groupId = group.id();
        if (this.groups.containsKey(groupId)) {
            String msg = group + " already exists!";
            if (this.logger.isFineEnabled()) {
                this.logger.warning(msg);
            }
            throw new IllegalStateException(msg);
        }
        this.groups.put(groupId, group);
    }

    private CPGroupInfo getRaftGroupByName(String name) {
        for (CPGroupInfo group : this.groups.values()) {
            if (group.status() == CPGroup.CPGroupStatus.DESTROYED || !group.name().equals(name)) continue;
            return group;
        }
        return null;
    }

    public void triggerDestroyRaftGroup(CPGroupId groupId) {
        Preconditions.checkNotNull(groupId);
        this.checkMetadataGroupInitSuccessful();
        if (this.membershipChangeSchedule != null) {
            String msg = "Cannot destroy " + groupId + " while there are ongoing CP membership changes!";
            if (this.logger.isFineEnabled()) {
                this.logger.warning(msg);
            }
            throw new IllegalStateException(msg);
        }
        CPGroupInfo group = (CPGroupInfo)this.groups.get(groupId);
        if (group == null) {
            String msg = "No CP group exists for " + groupId + " to destroy!";
            if (this.logger.isFineEnabled()) {
                this.logger.warning(msg);
            }
            throw new IllegalArgumentException(msg);
        }
        if (group.setDestroying()) {
            this.logger.info("Destroying " + groupId);
        } else if (this.logger.isFineEnabled()) {
            this.logger.fine(groupId + " is already " + (Object)((Object)group.status()));
        }
    }

    public void completeDestroyRaftGroups(Set<CPGroupId> groupIds) {
        Preconditions.checkNotNull(groupIds);
        for (CPGroupId groupId : groupIds) {
            Preconditions.checkNotNull(groupId);
            if (this.groups.containsKey(groupId)) continue;
            String msg = groupId + " does not exist to complete destroy";
            this.logger.warning(msg);
            throw new IllegalArgumentException(msg);
        }
        for (CPGroupId groupId : groupIds) {
            this.completeDestroyRaftGroup((CPGroupInfo)this.groups.get(groupId));
        }
    }

    private void completeDestroyRaftGroup(CPGroupInfo group) {
        RaftGroupId groupId = group.id();
        if (group.setDestroyed()) {
            this.logger.info(groupId + " is destroyed.");
            this.sendDestroyRaftNodeOps(group);
        } else if (this.logger.isFineEnabled()) {
            this.logger.fine(groupId + " is already destroyed.");
        }
    }

    public void forceDestroyRaftGroup(String groupName) {
        Preconditions.checkNotNull(groupName);
        Preconditions.checkFalse("METADATA".equalsIgnoreCase(groupName), "Cannot force-destroy the METADATA CP group!");
        this.checkMetadataGroupInitSuccessful();
        boolean found = false;
        for (CPGroupInfo group : this.groups.values()) {
            if (!group.name().equals(groupName)) continue;
            if (group.forceSetDestroyed()) {
                this.logger.info(group.id() + " is force-destroyed.");
                this.sendDestroyRaftNodeOps(group);
            } else if (this.logger.isFineEnabled()) {
                this.logger.fine(group.id() + " is already force-destroyed.");
            }
            found = true;
        }
        if (!found) {
            throw new IllegalArgumentException("CP group with name: " + groupName + " does not exist to force-destroy!");
        }
    }

    private void sendDestroyRaftNodeOps(CPGroupInfo group) {
        OperationService operationService = this.nodeEngine.getOperationService();
        DestroyRaftNodesOp op = new DestroyRaftNodesOp(Collections.singleton(group.id()));
        for (CPMemberInfo member : group.memberImpls()) {
            if (member.equals(this.getLocalCPMember())) {
                this.raftService.destroyRaftNode(group.id());
                continue;
            }
            operationService.send(op, member.getAddress());
        }
    }

    public boolean removeMember(long commitIndex, CPMemberInfo leavingMember) {
        Preconditions.checkNotNull(leavingMember);
        this.checkMetadataGroupInitSuccessful();
        if (!this.activeMembers.contains(leavingMember)) {
            this.logger.fine("Not removing " + leavingMember + " since it is not an active CP member");
            return true;
        }
        if (this.membershipChangeSchedule != null) {
            if (leavingMember.equals(this.membershipChangeSchedule.getLeavingMember())) {
                this.membershipChangeSchedule = this.membershipChangeSchedule.addRetriedCommitIndex(commitIndex);
                if (this.logger.isFineEnabled()) {
                    this.logger.fine(leavingMember + " is already marked as leaving.");
                }
                return false;
            }
            String msg = "There is already an ongoing CP membership change process. Cannot process remove request of " + leavingMember;
            if (this.logger.isFineEnabled()) {
                this.logger.fine(msg);
            }
            throw new CannotRemoveCPMemberException(msg);
        }
        if (this.activeMembers.size() == 2) {
            this.logger.warning(leavingMember + " is directly removed as there are only " + this.activeMembers.size() + " CP members.");
            this.removeActiveMember(commitIndex, leavingMember);
            throw new RetryableHazelcastException();
        }
        if (this.activeMembers.size() == 1) {
            this.logger.fine("Not removing the last active CP member: " + leavingMember + " to help it complete its shutdown");
            return true;
        }
        return this.initMembershipChangeScheduleForLeavingMember(commitIndex, leavingMember);
    }

    private boolean initMembershipChangeScheduleForLeavingMember(long commitIndex, CPMemberInfo leavingMember) {
        ArrayList<RaftGroupId> leavingGroupIds = new ArrayList<RaftGroupId>();
        ArrayList<MembershipChangeSchedule.CPGroupMembershipChange> changes = new ArrayList<MembershipChangeSchedule.CPGroupMembershipChange>();
        for (CPGroupInfo group : this.groups.values()) {
            RaftGroupId groupId = group.id();
            if (!group.containsMember(leavingMember) || group.status() == CPGroup.CPGroupStatus.DESTROYED) continue;
            CPMemberInfo substitute = this.findSubstitute(group);
            if (substitute != null) {
                leavingGroupIds.add(groupId);
                changes.add(new MembershipChangeSchedule.CPGroupMembershipChange(groupId, group.getMembersCommitIndex(), group.memberImpls(), substitute, leavingMember));
                continue;
            }
            leavingGroupIds.add(groupId);
            changes.add(new MembershipChangeSchedule.CPGroupMembershipChange(groupId, group.getMembersCommitIndex(), group.memberImpls(), null, leavingMember));
        }
        if (changes.isEmpty()) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Removing " + leavingMember + " directly since it is not present in any CP group.");
            }
            this.removeActiveMember(commitIndex, leavingMember);
            return true;
        }
        this.membershipChangeSchedule = MembershipChangeSchedule.forLeavingMember(Collections.singletonList(commitIndex), leavingMember, changes);
        if (this.logger.isFineEnabled()) {
            this.logger.info(leavingMember + " will be removed from " + changes);
        } else {
            this.logger.info(leavingMember + " will be removed from " + leavingGroupIds);
        }
        return false;
    }

    private CPMemberInfo findSubstitute(CPGroupInfo group) {
        for (CPMemberInfo substitute : this.activeMembers) {
            if (!this.activeMembers.contains(substitute) || group.containsMember(substitute)) continue;
            return substitute;
        }
        return null;
    }

    public MembershipChangeSchedule completeRaftGroupMembershipChanges(long commitIndex, Map<CPGroupId, Tuple2<Long, Long>> changedGroups) {
        Preconditions.checkNotNull(changedGroups);
        if (this.membershipChangeSchedule == null) {
            String msg = "Cannot apply CP membership changes: " + changedGroups + " since there is no membership change context!";
            this.logger.warning(msg);
            throw new IllegalStateException(msg);
        }
        for (MembershipChangeSchedule.CPGroupMembershipChange change : this.membershipChangeSchedule.getChanges()) {
            CPGroupId groupId = change.getGroupId();
            CPGroupInfo group = (CPGroupInfo)this.groups.get(groupId);
            Preconditions.checkState(group != null, groupId + "not found in CP groups: " + this.groups.keySet() + "to apply " + change);
            Tuple2<Long, Long> t = changedGroups.get(groupId);
            if (t != null) {
                if (this.applyMembershipChange(change, group, (Long)t.element1, (Long)t.element2)) continue;
                changedGroups.remove(groupId);
                continue;
            }
            if (group.status() != CPGroup.CPGroupStatus.DESTROYED || changedGroups.containsKey(groupId)) continue;
            if (this.logger.isFineEnabled()) {
                this.logger.warning(groupId + " is already destroyed so will skip: " + change);
            }
            changedGroups.put(groupId, Tuple2.of(0L, 0L));
        }
        this.membershipChangeSchedule = this.membershipChangeSchedule.excludeCompletedChanges(changedGroups.keySet());
        if (this.checkSafeToRemoveIfCPMemberLeaving(this.membershipChangeSchedule)) {
            CPMemberInfo leavingMember = this.membershipChangeSchedule.getLeavingMember();
            this.removeActiveMember(commitIndex, leavingMember);
            this.completeFutures(this.getMetadataGroupId(), this.membershipChangeSchedule.getMembershipChangeCommitIndices(), null);
            this.membershipChangeSchedule = null;
            this.logger.info(leavingMember + " is removed from the CP subsystem.");
        } else if (this.membershipChangeSchedule.getChanges().isEmpty()) {
            this.completeFutures(this.getMetadataGroupId(), this.membershipChangeSchedule.getMembershipChangeCommitIndices(), null);
            this.membershipChangeSchedule = null;
            this.logger.info("Rebalancing is completed.");
        }
        return this.membershipChangeSchedule;
    }

    private void completeFutures(CPGroupId groupId, Collection<Long> indices, Object result) {
        if (!indices.isEmpty()) {
            RaftNodeImpl raftNode = (RaftNodeImpl)this.raftService.getRaftNode(groupId);
            if (raftNode != null) {
                for (Long index : indices) {
                    raftNode.completeFuture(index, result);
                }
            } else {
                this.logger.severe("RaftNode not found for " + groupId + " to notify commit indices " + indices + " with " + result);
            }
        }
    }

    private boolean applyMembershipChange(MembershipChangeSchedule.CPGroupMembershipChange change, CPGroupInfo group, long expectedMembersCommitIndex, long newMembersCommitIndex) {
        CPMemberInfo addedMember = change.getMemberToAdd();
        CPMemberInfo removedMember = change.getMemberToRemove();
        if (group.applyMembershipChange(removedMember, addedMember, expectedMembersCommitIndex, newMembersCommitIndex)) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Applied add-member: " + (addedMember != null ? addedMember : "-") + " and remove-member: " + (removedMember != null ? removedMember : "-") + " in " + group.id() + " with new members commit index: " + newMembersCommitIndex);
            }
            if (this.getLocalCPMember().equals(addedMember)) {
                this.raftService.createRaftNode(group.id(), group.memberImpls());
            }
            return true;
        }
        this.logger.severe("Could not apply add-member: " + (addedMember != null ? addedMember : "-") + " and remove-member: " + (removedMember != null ? removedMember : "-") + " in " + group + " with new members commit index: " + newMembersCommitIndex + ", expected members commit index: " + expectedMembersCommitIndex + ", known members commit index: " + group.getMembersCommitIndex());
        return false;
    }

    private boolean checkSafeToRemoveIfCPMemberLeaving(MembershipChangeSchedule schedule) {
        CPMemberInfo leavingMember = schedule.getLeavingMember();
        if (leavingMember == null) {
            return false;
        }
        if (schedule.getChanges().size() > 0) {
            return false;
        }
        for (CPGroupInfo group : this.groups.values()) {
            if (!group.containsMember(leavingMember)) continue;
            if (group.status() != CPGroup.CPGroupStatus.DESTROYED) {
                return false;
            }
            if (!this.logger.isFineEnabled()) continue;
            this.logger.warning("Leaving " + leavingMember + " was in the destroyed " + group.id());
        }
        return true;
    }

    private List<MembershipChangeSchedule.CPGroupMembershipChange> getGroupMembershipChangesForNewMember(CPMemberInfo newMember) {
        ArrayList<MembershipChangeSchedule.CPGroupMembershipChange> changes = new ArrayList<MembershipChangeSchedule.CPGroupMembershipChange>();
        for (CPGroupInfo group : this.groups.values()) {
            if (group.status() != CPGroup.CPGroupStatus.ACTIVE || group.initialMemberCount() <= group.memberCount()) continue;
            Preconditions.checkState(!group.memberImpls().contains(newMember), group + " already contains: " + newMember);
            changes.add(new MembershipChangeSchedule.CPGroupMembershipChange(group.id(), group.getMembersCommitIndex(), group.memberImpls(), newMember, null));
        }
        return changes;
    }

    public Collection<CPMemberInfo> getActiveMembers() {
        return this.activeMembers;
    }

    public void handleMetadataGroupId(RaftGroupId newMetadataGroupId) {
        Preconditions.checkNotNull(newMetadataGroupId);
        RaftGroupId metadataGroupId = this.getMetadataGroupId();
        while (metadataGroupId.seed() < newMetadataGroupId.seed()) {
            if (this.metadataGroupIdRef.compareAndSet(metadataGroupId, newMetadataGroupId)) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Updated METADATA groupId: " + newMetadataGroupId);
                }
                return;
            }
            metadataGroupId = this.getMetadataGroupId();
        }
    }

    private void updateInvocationManagerMembers(long groupIdSeed, long membersCommitIndex, Collection<CPMemberInfo> members) {
        RaftInvocationContext context = this.raftService.getInvocationManager().getRaftInvocationContext();
        context.setMembers(groupIdSeed, membersCommitIndex, members);
    }

    public Collection<CPGroupId> getDestroyingGroupIds() {
        ArrayList<CPGroupId> groupIds = new ArrayList<CPGroupId>();
        for (CPGroupInfo group : this.groups.values()) {
            if (group.status() != CPGroup.CPGroupStatus.DESTROYING) continue;
            groupIds.add(group.id());
        }
        return groupIds;
    }

    public MembershipChangeSchedule getMembershipChangeSchedule() {
        return this.membershipChangeSchedule;
    }

    boolean isMetadataGroupLeader() {
        CPMemberInfo localCPMember = this.getLocalCPMember();
        if (localCPMember == null) {
            return false;
        }
        RaftNode raftNode = this.raftService.getRaftNode(this.getMetadataGroupId());
        return raftNode != null && !raftNode.isTerminatedOrSteppedDown() && localCPMember.equals(raftNode.getLeader());
    }

    public boolean addMember(long commitIndex, CPMemberInfo member) {
        Preconditions.checkNotNull(member);
        this.checkMetadataGroupInitSuccessful();
        for (CPMemberInfo existingMember : this.activeMembers) {
            if (!existingMember.getAddress().equals(member.getAddress())) continue;
            if (existingMember.getUuid().equals(member.getUuid())) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine(member + " already exists.");
                }
                if (this.membershipChangeSchedule != null && member.equals(this.membershipChangeSchedule.getAddedMember())) {
                    this.membershipChangeSchedule = this.membershipChangeSchedule.addRetriedCommitIndex(commitIndex);
                    this.logger.info("CP groups are already being rebalanced for " + member);
                    return false;
                }
                return true;
            }
            throw new IllegalStateException(member + " cannot be added to the CP subsystem because another " + existingMember + " exists with the same address!");
        }
        Preconditions.checkState(this.membershipChangeSchedule == null, "Cannot rebalance CP groups because there is ongoing " + this.membershipChangeSchedule);
        LinkedHashSet<CPMemberInfo> newMembers = new LinkedHashSet<CPMemberInfo>(this.activeMembers);
        newMembers.add(member);
        this.doSetActiveMembers(commitIndex, newMembers);
        this.logger.info("Added new " + member + ". New active CP members list: " + newMembers);
        List<MembershipChangeSchedule.CPGroupMembershipChange> changes = this.getGroupMembershipChangesForNewMember(member);
        if (changes.size() > 0) {
            this.membershipChangeSchedule = MembershipChangeSchedule.forJoiningMember(Collections.singletonList(commitIndex), member, changes);
            if (this.logger.isFineEnabled()) {
                this.logger.fine("CP group rebalancing is triggered for " + member + ", changes: " + this.membershipChangeSchedule);
            }
            return false;
        }
        return true;
    }

    private void removeActiveMember(long commitIndex, CPMemberInfo member) {
        LinkedHashSet<CPMemberInfo> newMembers = new LinkedHashSet<CPMemberInfo>(this.activeMembers);
        newMembers.remove(member);
        this.doSetActiveMembers(commitIndex, newMembers);
    }

    private void doSetActiveMembers(long commitIndex, Collection<CPMemberInfo> members) {
        this.activeMembers = Collections.unmodifiableCollection(members);
        this.activeMembersCommitIndex = commitIndex;
        this.updateInvocationManagerMembers(this.getMetadataGroupId().seed(), commitIndex, this.activeMembers);
        this.raftService.updateMissingMembers();
        this.broadcastActiveCPMembers();
    }

    public void checkMetadataGroupInitSuccessful() {
        switch (this.initializationStatus) {
            case SUCCESSFUL: {
                return;
            }
            case IN_PROGRESS: {
                throw new MetadataRaftGroupInitInProgressException();
            }
            case FAILED: {
                throw new IllegalStateException("CP subsystem initialization failed!");
            }
        }
        throw new IllegalStateException("Illegal initialization status: " + (Object)((Object)this.initializationStatus));
    }

    void broadcastActiveCPMembers() {
        if (!this.isDiscoveryCompleted() || !this.isMetadataGroupLeader()) {
            return;
        }
        RaftGroupId metadataGroupId = this.getMetadataGroupId();
        long commitIndex = this.activeMembersCommitIndex;
        Collection<CPMemberInfo> cpMembers = this.activeMembers;
        if (cpMembers.isEmpty()) {
            return;
        }
        Set<Member> clusterMembers = this.nodeEngine.getClusterService().getMembers();
        OperationService operationService = this.nodeEngine.getOperationService();
        PublishActiveCPMembersOp op = new PublishActiveCPMembersOp(metadataGroupId, commitIndex, cpMembers);
        for (Member member : clusterMembers) {
            if (member.localMember()) continue;
            operationService.send(op, member.getAddress());
        }
    }

    boolean isDiscoveryCompleted() {
        return this.discoveryCompleted.get();
    }

    List<CPMemberInfo> getInitialCPMembers() {
        return this.initialCPMembers;
    }

    MetadataRaftGroupInitStatus getInitializationStatus() {
        return this.initializationStatus;
    }

    Set<CPMemberInfo> getInitializedCPMembers() {
        return this.initializedCPMembers;
    }

    Set<Long> getInitializationCommitIndices() {
        return this.initializationCommitIndices;
    }

    public void disableDiscovery() {
        if (this.config.getCPMemberCount() > 0) {
            this.logger.info("Disabling discovery of initial CP members since it is already completed...");
        }
        this.discoveryCompleted.set(true);
    }

    private void scheduleDiscoverInitialCPMembersTask(boolean terminateOnDiscoveryFailure) {
        DiscoverInitialCPMembersTask task;
        this.currentDiscoveryTask = task = new DiscoverInitialCPMembersTask(terminateOnDiscoveryFailure);
        ExecutionService executionService = this.nodeEngine.getExecutionService();
        executionService.schedule(task, 1000L, TimeUnit.MILLISECONDS);
    }

    @SuppressFBWarnings(value={"SE_COMPARATOR_SHOULD_BE_SERIALIZABLE", "DM_BOXED_PRIMITIVE_FOR_COMPARE"})
    private static class CPGroupIdComparator
    implements Comparator<CPGroupId> {
        private CPGroupIdComparator() {
        }

        @Override
        public int compare(CPGroupId o1, CPGroupId o2) {
            return Long.valueOf(o1.id()).compareTo(o2.id());
        }
    }

    @SuppressFBWarnings(value={"SE_COMPARATOR_SHOULD_BE_SERIALIZABLE"})
    private static class CPMemberComparator
    implements Comparator<CPMemberInfo> {
        private CPMemberComparator() {
        }

        @Override
        public int compare(CPMemberInfo o1, CPMemberInfo o2) {
            return o1.getUuid().compareTo(o2.getUuid());
        }
    }

    private class DiscoverInitialCPMembersTask
    implements Runnable {
        private Collection<Member> latestMembers = Collections.emptySet();
        private final boolean terminateOnDiscoveryFailure;
        private long lastLoggingTime;
        private volatile boolean cancelled;
        private volatile DiscoveryTaskState state;

        DiscoverInitialCPMembersTask(boolean terminateOnDiscoveryFailure) {
            this.terminateOnDiscoveryFailure = terminateOnDiscoveryFailure;
            this.state = DiscoveryTaskState.SCHEDULED;
        }

        @Override
        public void run() {
            this.state = DiscoveryTaskState.RUNNING;
            try {
                this.doRun();
            }
            finally {
                if (this.state == DiscoveryTaskState.RUNNING) {
                    this.state = DiscoveryTaskState.COMPLETED;
                }
            }
        }

        private void doRun() {
            if (this.shouldRescheduleOrSkip()) {
                return;
            }
            Collection<Member> members = MetadataRaftGroupManager.this.nodeEngine.getClusterService().getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
            for (Member member : this.latestMembers) {
                if (members.contains(member)) continue;
                MetadataRaftGroupManager.this.logger.severe(member + " left the cluster while CP subsystem discovery in progress!");
                this.handleDiscoveryFailure();
                return;
            }
            this.latestMembers = members;
            if (this.rescheduleIfCPMemberCountNotSatisfied(members)) {
                return;
            }
            CPMemberInfo localMemberCandidate = new CPMemberInfo(MetadataRaftGroupManager.this.nodeEngine.getLocalMember());
            List<CPMemberInfo> discoveredCPMembers = this.getDiscoveredCPMembers(members);
            if (this.completeDiscoveryIfNotCPMember(discoveredCPMembers, localMemberCandidate)) {
                return;
            }
            MetadataRaftGroupManager.this.updateInvocationManagerMembers(MetadataRaftGroupManager.this.getMetadataGroupId().seed(), 0L, discoveredCPMembers);
            if (!this.commitMetadataRaftGroupInit(localMemberCandidate, discoveredCPMembers)) {
                this.handleDiscoveryFailure();
                return;
            }
            MetadataRaftGroupManager.this.logger.info("CP subsystem is initialized with: " + discoveredCPMembers);
            MetadataRaftGroupManager.this.discoveryCompleted.set(true);
            MetadataRaftGroupManager.this.broadcastActiveCPMembers();
            MetadataRaftGroupManager.this.scheduleRaftGroupMembershipManagementTasks();
        }

        private boolean shouldRescheduleOrSkip() {
            if (this.cancelled) {
                return true;
            }
            if (!MetadataRaftGroupManager.this.nodeEngine.getClusterService().isJoined()) {
                this.scheduleSelf();
                return true;
            }
            if (MetadataRaftGroupManager.this.nodeEngine.getClusterService().getClusterVersion().isLessThan(Versions.V3_12)) {
                MetadataRaftGroupManager.this.logger.fine("Cannot start initial CP members discovery since cluster version is less than 3.12.");
                this.scheduleSelf();
                return true;
            }
            return MetadataRaftGroupManager.this.isDiscoveryCompleted();
        }

        private boolean rescheduleIfCPMemberCountNotSatisfied(Collection<Member> members) {
            if (members.size() < MetadataRaftGroupManager.this.config.getCPMemberCount()) {
                long now = Clock.currentTimeMillis();
                if (now - this.lastLoggingTime >= 5000L) {
                    this.lastLoggingTime = now;
                    MetadataRaftGroupManager.this.logger.info("CP Subsystem is waiting for " + MetadataRaftGroupManager.this.config.getCPMemberCount() + " members to join the cluster. Current member count: " + members.size());
                }
                this.scheduleSelf();
                return true;
            }
            return false;
        }

        private void scheduleSelf() {
            this.state = DiscoveryTaskState.SCHEDULED;
            MetadataRaftGroupManager.this.nodeEngine.getExecutionService().schedule(this, 1000L, TimeUnit.MILLISECONDS);
        }

        private List<CPMemberInfo> getDiscoveredCPMembers(Collection<Member> members) {
            assert (members.size() >= MetadataRaftGroupManager.this.config.getCPMemberCount());
            List<Member> memberList = new ArrayList<Member>(members).subList(0, MetadataRaftGroupManager.this.config.getCPMemberCount());
            ArrayList<CPMemberInfo> cpMembers = new ArrayList<CPMemberInfo>(MetadataRaftGroupManager.this.config.getCPMemberCount());
            for (Member member : memberList) {
                cpMembers.add(new CPMemberInfo(member));
            }
            Collections.sort(cpMembers, new CPMemberComparator());
            return cpMembers;
        }

        private boolean completeDiscoveryIfNotCPMember(List<CPMemberInfo> cpMembers, CPMemberInfo localCPMemberCandidate) {
            if (!cpMembers.contains(localCPMemberCandidate)) {
                MetadataRaftGroupManager.this.logger.info("I am not a CP member! I'll serve as an AP member.");
                MetadataRaftGroupManager.this.discoveryCompleted.set(true);
                return true;
            }
            return false;
        }

        private boolean commitMetadataRaftGroupInit(CPMemberInfo localCPMemberCandidate, List<CPMemberInfo> discoveredCPMembers) {
            List<CPMemberInfo> metadataMembers = discoveredCPMembers.subList(0, MetadataRaftGroupManager.this.config.getGroupSize());
            RaftGroupId metadataGroupId = MetadataRaftGroupManager.this.getMetadataGroupId();
            try {
                if (metadataMembers.contains(localCPMemberCandidate)) {
                    MetadataRaftGroupManager.this.raftService.createRaftNode(metadataGroupId, metadataMembers, localCPMemberCandidate);
                }
                InitMetadataRaftGroupOp op = new InitMetadataRaftGroupOp(localCPMemberCandidate, discoveredCPMembers, metadataGroupId.seed());
                MetadataRaftGroupManager.this.raftService.getInvocationManager().invoke(metadataGroupId, op).get();
                MetadataRaftGroupManager.this.localCPMember.set(localCPMemberCandidate);
            }
            catch (Exception e) {
                MetadataRaftGroupManager.this.logger.severe("Could not initialize METADATA CP group with CP members: " + metadataMembers, e);
                MetadataRaftGroupManager.this.raftService.destroyRaftNode(metadataGroupId);
                return false;
            }
            ArrayList futures = new ArrayList();
            int j = RandomPicker.getInt(1, 20);
            for (int i = 0; i < j; ++i) {
                futures.add(MetadataRaftGroupManager.this.raftService.getInvocationManager().invoke(metadataGroupId, new GetRaftGroupIdsOp()));
            }
            FutureUtil.waitWithDeadline(futures, 60L, TimeUnit.SECONDS, FutureUtil.IGNORE_ALL_EXCEPTIONS);
            return true;
        }

        private void handleDiscoveryFailure() {
            if (this.terminateOnDiscoveryFailure) {
                MetadataRaftGroupManager.this.logger.warning("Terminating because of CP discovery failure...");
                this.terminateNode();
            } else {
                MetadataRaftGroupManager.this.logger.warning("Cancelling CP subsystem discovery...");
                MetadataRaftGroupManager.this.discoveryCompleted.set(true);
            }
        }

        private void terminateNode() {
            ((NodeEngineImpl)MetadataRaftGroupManager.this.nodeEngine).getNode().shutdown(true);
        }

        void cancelAndAwaitCompletion() {
            this.cancelled = true;
            while (this.state != DiscoveryTaskState.COMPLETED) {
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private static enum DiscoveryTaskState {
        RUNNING,
        SCHEDULED,
        COMPLETED;

    }

    private class BroadcastActiveCPMembersTask
    implements Runnable {
        private BroadcastActiveCPMembersTask() {
        }

        @Override
        public void run() {
            MetadataRaftGroupManager.this.broadcastActiveCPMembers();
        }
    }

    static enum MetadataRaftGroupInitStatus {
        IN_PROGRESS,
        FAILED,
        SUCCESSFUL;

    }
}

