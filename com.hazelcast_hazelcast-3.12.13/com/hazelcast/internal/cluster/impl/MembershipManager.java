/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.cluster.Joiner;
import com.hazelcast.cluster.impl.TcpIpJoiner;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.hotrestart.InternalHotRestartService;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.MemberInfo;
import com.hazelcast.internal.cluster.impl.ClusterHeartbeatManager;
import com.hazelcast.internal.cluster.impl.ClusterJoinManager;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.MemberMap;
import com.hazelcast.internal.cluster.impl.MembersView;
import com.hazelcast.internal.cluster.impl.MembersViewMetadata;
import com.hazelcast.internal.cluster.impl.SplitBrainJoinMessage;
import com.hazelcast.internal.cluster.impl.operations.FetchMembersViewOp;
import com.hazelcast.internal.cluster.impl.operations.MembersUpdateOp;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.MembershipAwareService;
import com.hazelcast.spi.MembershipServiceEvent;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.eventservice.InternalEventService;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.executor.ExecutorType;
import com.hazelcast.util.executor.ManagedExecutorService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;

public class MembershipManager {
    private static final long FETCH_MEMBER_LIST_MILLIS = 5000L;
    private static final String MASTERSHIP_CLAIM_EXECUTOR_NAME = "hz:cluster:mastership";
    private final Node node;
    private final NodeEngineImpl nodeEngine;
    private final ClusterServiceImpl clusterService;
    private final Lock clusterServiceLock;
    private final ILogger logger;
    private final AtomicReference<MemberMap> memberMapRef = new AtomicReference<MemberMap>(MemberMap.empty());
    private final AtomicReference<Map<Object, MemberImpl>> missingMembersRef = new AtomicReference(Collections.emptyMap());
    private final Set<Address> suspectedMembers = Collections.newSetFromMap(new ConcurrentHashMap());
    private final int mastershipClaimTimeoutSeconds;

    MembershipManager(Node node, ClusterServiceImpl clusterService, Lock clusterServiceLock) {
        this.node = node;
        this.clusterService = clusterService;
        this.clusterServiceLock = clusterServiceLock;
        this.nodeEngine = node.getNodeEngine();
        this.logger = node.getLogger(this.getClass());
        this.mastershipClaimTimeoutSeconds = node.getProperties().getInteger(GroupProperty.MASTERSHIP_CLAIM_TIMEOUT_SECONDS);
        this.registerThisMember();
    }

    void init() {
        InternalExecutionService executionService = this.nodeEngine.getExecutionService();
        HazelcastProperties hazelcastProperties = this.node.getProperties();
        executionService.register(MASTERSHIP_CLAIM_EXECUTOR_NAME, 1, Integer.MAX_VALUE, ExecutorType.CACHED);
        long memberListPublishInterval = hazelcastProperties.getSeconds(GroupProperty.MEMBER_LIST_PUBLISH_INTERVAL_SECONDS);
        memberListPublishInterval = memberListPublishInterval > 0L ? memberListPublishInterval : 1L;
        executionService.scheduleWithRepetition("hz:cluster", new Runnable(){

            @Override
            public void run() {
                MembershipManager.this.publishMemberList();
            }
        }, memberListPublishInterval, memberListPublishInterval, TimeUnit.SECONDS);
    }

    private void registerThisMember() {
        MemberImpl thisMember = this.clusterService.getLocalMember();
        this.memberMapRef.set(MemberMap.singleton(thisMember));
    }

    public MemberImpl getMember(Address address) {
        assert (address != null) : "Address required!";
        MemberMap memberMap = this.memberMapRef.get();
        return memberMap.getMember(address);
    }

    public MemberImpl getMember(String uuid) {
        assert (uuid != null) : "UUID required!";
        MemberMap memberMap = this.memberMapRef.get();
        return memberMap.getMember(uuid);
    }

    public MemberImpl getMember(Address address, String uuid) {
        assert (address != null) : "Address required!";
        assert (uuid != null) : "UUID required!";
        MemberMap memberMap = this.memberMapRef.get();
        return memberMap.getMember(address, uuid);
    }

    public Collection<MemberImpl> getMembers() {
        return this.memberMapRef.get().getMembers();
    }

    public Set<Member> getMemberSet() {
        return this.memberMapRef.get().getMembers();
    }

    MemberMap getMemberMap() {
        return this.memberMapRef.get();
    }

    public MembersView getMembersView() {
        return this.memberMapRef.get().toMembersView();
    }

    public int getMemberListVersion() {
        return this.memberMapRef.get().getVersion();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendMemberListToMember(Address target) {
        this.clusterServiceLock.lock();
        try {
            if (!this.clusterService.isMaster() || !this.clusterService.isJoined()) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Cannot publish member list to " + target + ". Is-master: " + this.clusterService.isMaster() + ", joined: " + this.clusterService.isJoined());
                }
                return;
            }
            if (this.clusterService.getThisAddress().equals(target)) {
                return;
            }
            MemberMap memberMap = this.memberMapRef.get();
            MemberImpl member = memberMap.getMember(target);
            if (member == null) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Not member: " + target + ", cannot send member list.");
                }
                return;
            }
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Sending member list to member: " + target + " " + this.memberListString());
            }
            MembersUpdateOp op = new MembersUpdateOp(member.getUuid(), memberMap.toMembersView(), this.clusterService.getClusterTime(), null, false);
            op.setCallerUuid(this.clusterService.getThisUuid());
            this.nodeEngine.getOperationService().send(op, target);
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    private void publishMemberList() {
        this.clusterServiceLock.lock();
        try {
            this.sendMemberListToOthers();
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    private void sendMemberListToOthers() {
        if (!this.clusterService.isMaster() || !this.clusterService.isJoined() || this.clusterService.getClusterJoinManager().isMastershipClaimInProgress()) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Cannot publish member list to cluster. Is-master: " + this.clusterService.isMaster() + ", joined: " + this.clusterService.isJoined() + " , mastership claim in progress: " + this.clusterService.getClusterJoinManager().isMastershipClaimInProgress());
            }
            return;
        }
        MemberMap memberMap = this.getMemberMap();
        MembersView membersView = memberMap.toMembersView();
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Sending member list to the non-master nodes: " + this.memberListString());
        }
        for (MemberImpl member : memberMap.getMembers()) {
            if (member.localMember()) continue;
            MembersUpdateOp op = new MembersUpdateOp(member.getUuid(), membersView, this.clusterService.getClusterTime(), null, false);
            op.setCallerUuid(this.clusterService.getThisUuid());
            this.nodeEngine.getOperationService().send(op, member.getAddress());
        }
    }

    String memberListString() {
        MemberMap memberMap = this.getMemberMap();
        Set<MemberImpl> members = memberMap.getMembers();
        StringBuilder sb = new StringBuilder("\n\nMembers {").append("size:").append(members.size()).append(", ").append("ver:").append(memberMap.getVersion()).append("} [");
        for (Member member : members) {
            sb.append("\n\t").append(member);
        }
        sb.append("\n]\n");
        return sb.toString();
    }

    void updateMembers(MembersView membersView) {
        MemberMap currentMemberMap = this.memberMapRef.get();
        LinkedList<MemberImpl> addedMembers = new LinkedList<MemberImpl>();
        LinkedList<MemberImpl> removedMembers = new LinkedList<MemberImpl>();
        ClusterHeartbeatManager clusterHeartbeatManager = this.clusterService.getClusterHeartbeatManager();
        MemberImpl[] members = new MemberImpl[membersView.size()];
        int memberIndex = 0;
        boolean updatedLiteMember = false;
        for (MemberInfo memberInfo : membersView.getMembers()) {
            Address address = memberInfo.getAddress();
            MemberImpl member = currentMemberMap.getMember(address);
            if (member != null && member.getUuid().equals(memberInfo.getUuid())) {
                if (member.isLiteMember()) {
                    updatedLiteMember = true;
                }
                member = this.createNewMemberImplIfChanged(memberInfo, member);
                members[memberIndex++] = member;
                continue;
            }
            if (member != null) {
                assert (!member.localMember() || !member.equals(this.clusterService.getLocalMember())) : "Local " + member + " cannot be replaced with " + memberInfo;
                removedMembers.add(member);
            }
            member = this.createMember(memberInfo, memberInfo.getAttributes());
            addedMembers.add(member);
            long now = this.clusterService.getClusterTime();
            clusterHeartbeatManager.onHeartbeat(member, now);
            this.repairPartitionTableIfReturningMember(member);
            members[memberIndex++] = member;
        }
        MemberMap newMemberMap = membersView.toMemberMap();
        for (MemberImpl member : currentMemberMap.getMembers()) {
            if (newMemberMap.contains(member.getAddress())) continue;
            removedMembers.add(member);
        }
        this.setMembers(MemberMap.createNew(membersView.getVersion(), members));
        if (updatedLiteMember) {
            this.node.partitionService.updateMemberGroupSize();
        }
        for (MemberImpl member : removedMembers) {
            this.closeConnection(member.getAddress(), "Member left event received from master");
            this.handleMemberRemove(this.memberMapRef.get(), member);
        }
        this.clusterService.getClusterJoinManager().insertIntoRecentlyJoinedMemberSet(addedMembers);
        this.sendMembershipEvents(currentMemberMap.getMembers(), addedMembers, !this.clusterService.isJoined());
        this.removeFromMissingMembers(members);
        clusterHeartbeatManager.heartbeat();
        this.clusterService.printMemberList();
        this.node.getNodeExtension().scheduleClusterVersionAutoUpgrade();
    }

    private MemberImpl createNewMemberImplIfChanged(MemberInfo newMemberInfo, MemberImpl member) {
        if (member.isLiteMember() && !newMemberInfo.isLiteMember()) {
            this.logger.info(member + " is promoted to normal member.");
            member = member.localMember() ? this.clusterService.promoteAndGetLocalMember() : this.createMember(newMemberInfo, member.getAttributes());
        } else if (member.getMemberListJoinVersion() != newMemberInfo.getMemberListJoinVersion()) {
            if (member.getMemberListJoinVersion() != -1 && this.logger.isFineEnabled()) {
                this.logger.fine("Member list join version of " + member + " is changed to " + newMemberInfo.getMemberListJoinVersion() + " from " + member.getMemberListJoinVersion());
            }
            if (member.localMember()) {
                this.setLocalMemberListJoinVersion(newMemberInfo.getMemberListJoinVersion());
                member = this.clusterService.getLocalMember();
            } else {
                member = this.createMember(newMemberInfo, member.getAttributes());
            }
        }
        return member;
    }

    private MemberImpl createMember(MemberInfo memberInfo, Map<String, Object> attributes) {
        Address address = memberInfo.getAddress();
        Address thisAddress = this.node.getThisAddress();
        String ipV6ScopeId = thisAddress.getScopeId();
        address.setScopeId(ipV6ScopeId);
        boolean localMember = thisAddress.equals(address);
        MemberImpl.Builder builder = memberInfo.getAddressMap() != null && memberInfo.getAddressMap().containsKey(EndpointQualifier.MEMBER) ? new MemberImpl.Builder(memberInfo.getAddressMap()) : new MemberImpl.Builder(memberInfo.getAddress());
        return builder.version(memberInfo.getVersion()).localMember(localMember).uuid(memberInfo.getUuid()).attributes(attributes).liteMember(memberInfo.isLiteMember()).memberListJoinVersion(memberInfo.getMemberListJoinVersion()).instance(this.node.hazelcastInstance).build();
    }

    private void repairPartitionTableIfReturningMember(MemberImpl member) {
        if (!this.clusterService.isMaster()) {
            return;
        }
        if (this.clusterService.getClusterState().isMigrationAllowed()) {
            return;
        }
        if (!this.node.getNodeExtension().isStartCompleted()) {
            return;
        }
        MemberImpl missingMember = this.getMissingMember(member.getAddress(), member.getUuid());
        if (missingMember != null) {
            Level level;
            boolean repair;
            if (this.isHotRestartEnabled()) {
                repair = !missingMember.getAddress().equals(member.getAddress());
                level = Level.INFO;
            } else {
                repair = !missingMember.getUuid().equals(member.getUuid());
                level = Level.FINE;
            }
            if (repair) {
                this.logger.log(level, member + " is returning with a new identity. Old one was: " + missingMember + ". Will update partition table with the new identity.");
                InternalPartitionServiceImpl partitionService = this.node.partitionService;
                partitionService.replaceMember(missingMember, member);
            }
        }
    }

    void setLocalMemberListJoinVersion(int memberListJoinVersion) {
        MemberImpl localMember = this.clusterService.getLocalMember();
        if (memberListJoinVersion != -1) {
            localMember.setMemberListJoinVersion(memberListJoinVersion);
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Local member list join version is set to " + memberListJoinVersion);
            }
        } else if (this.logger.isFineEnabled()) {
            this.logger.fine("No member list join version is available during join. Local member list join version: " + localMember.getMemberListJoinVersion());
        }
    }

    void setMembers(MemberMap memberMap) {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Setting members " + memberMap.getMembers() + ", version: " + memberMap.getVersion());
        }
        this.clusterServiceLock.lock();
        try {
            this.memberMapRef.set(memberMap);
            this.retainSuspectedMembers(memberMap);
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    private void retainSuspectedMembers(MemberMap memberMap) {
        Iterator<Address> it = this.suspectedMembers.iterator();
        while (it.hasNext()) {
            Address suspectedAddress = it.next();
            if (memberMap.contains(suspectedAddress)) continue;
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Removing suspected address " + suspectedAddress + ", it's no longer a member.");
            }
            it.remove();
        }
    }

    boolean isMemberSuspected(Address address) {
        return this.suspectedMembers.contains(address);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean clearMemberSuspicion(Address address, String reason) {
        this.clusterServiceLock.lock();
        try {
            Address masterAddress;
            if (!this.suspectedMembers.contains(address)) {
                boolean bl = true;
                return bl;
            }
            MemberMap memberMap = this.getMemberMap();
            if (memberMap.isBeforeThan(address, masterAddress = this.clusterService.getMasterAddress())) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Not removing suspicion of " + address + " since it is before than current master " + masterAddress + " in member list.");
                }
                boolean bl = false;
                return bl;
            }
            boolean removed = this.suspectedMembers.remove(address);
            if (removed && this.logger.isInfoEnabled()) {
                this.logger.info("Removed suspicion from " + address + ". Reason: " + reason);
            }
        }
        finally {
            this.clusterServiceLock.unlock();
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void handleExplicitSuspicionTrigger(Address caller, int callerMemberListVersion, MembersViewMetadata suspectedMembersViewMetadata) {
        this.clusterServiceLock.lock();
        try {
            Address masterAddress = this.clusterService.getMasterAddress();
            int memberListVersion = this.getMemberListVersion();
            if (!masterAddress.equals(caller) || memberListVersion != callerMemberListVersion) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Ignoring explicit suspicion trigger for " + suspectedMembersViewMetadata + ". Caller: " + caller + ", caller member list version: " + callerMemberListVersion + ", known master: " + masterAddress + ", local member list version: " + memberListVersion);
                }
                return;
            }
            this.clusterService.sendExplicitSuspicion(suspectedMembersViewMetadata);
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void handleExplicitSuspicion(MembersViewMetadata expectedMembersViewMetadata, Address suspectedAddress) {
        this.clusterServiceLock.lock();
        try {
            MembersViewMetadata localMembersViewMetadata = this.createLocalMembersViewMetadata();
            if (!localMembersViewMetadata.equals(expectedMembersViewMetadata)) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Ignoring explicit suspicion of " + suspectedAddress + ". Expected: " + expectedMembersViewMetadata + ", Local: " + localMembersViewMetadata);
                }
                return;
            }
            MemberImpl suspectedMember = this.getMember(suspectedAddress);
            if (suspectedMember == null) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("No need for explicit suspicion, " + suspectedAddress + " is not a member.");
                }
                return;
            }
            this.suspectMember(suspectedMember, "explicit suspicion", true);
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    MembersViewMetadata createLocalMembersViewMetadata() {
        return new MembersViewMetadata(this.node.getThisAddress(), this.clusterService.getThisUuid(), this.clusterService.getMasterAddress(), this.getMemberListVersion());
    }

    boolean validateMembersViewMetadata(MembersViewMetadata membersViewMetadata) {
        MemberImpl sender = this.getMember(membersViewMetadata.getMemberAddress(), membersViewMetadata.getMemberUuid());
        return sender != null && this.node.getThisAddress().equals(membersViewMetadata.getMasterAddress());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void suspectMember(MemberImpl suspectedMember, String reason, boolean shouldCloseConn) {
        Set<Member> membersToAsk;
        MemberMap localMemberMap;
        assert (!suspectedMember.equals(this.clusterService.getLocalMember())) : "Cannot suspect from myself!";
        assert (!suspectedMember.localMember()) : "Cannot be local member";
        this.clusterServiceLock.lock();
        try {
            if (!this.clusterService.isJoined()) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Cannot handle suspect of " + suspectedMember + " because this node is not joined...");
                }
                return;
            }
            ClusterJoinManager clusterJoinManager = this.clusterService.getClusterJoinManager();
            if (this.clusterService.isMaster() && !clusterJoinManager.isMastershipClaimInProgress()) {
                this.removeMember(suspectedMember, reason, shouldCloseConn);
                return;
            }
            if (!this.addSuspectedMember(suspectedMember, reason, shouldCloseConn)) {
                return;
            }
            if (!this.tryStartMastershipClaim()) {
                return;
            }
            localMemberMap = this.getMemberMap();
            membersToAsk = this.collectMembersToAsk(localMemberMap);
            this.logger.info("Local " + localMemberMap.toMembersView() + " with suspected members: " + this.suspectedMembers + " and initial addresses to ask: " + membersToAsk);
        }
        finally {
            this.clusterServiceLock.unlock();
        }
        ManagedExecutorService executor = this.nodeEngine.getExecutionService().getExecutor(MASTERSHIP_CLAIM_EXECUTOR_NAME);
        executor.submit(new DecideNewMembersViewTask(localMemberMap, membersToAsk));
    }

    private Set<Member> collectMembersToAsk(MemberMap localMemberMap) {
        HashSet<Member> membersToAsk = new HashSet<Member>();
        for (MemberImpl member : localMemberMap.getMembers()) {
            if (member.localMember() || this.suspectedMembers.contains(member.getAddress())) continue;
            membersToAsk.add(member);
        }
        return membersToAsk;
    }

    private boolean tryStartMastershipClaim() {
        ClusterJoinManager clusterJoinManager = this.clusterService.getClusterJoinManager();
        if (clusterJoinManager.isMastershipClaimInProgress()) {
            return false;
        }
        MemberMap memberMap = this.memberMapRef.get();
        if (!this.shouldClaimMastership(memberMap)) {
            return false;
        }
        this.logger.info("Starting mastership claim process...");
        clusterJoinManager.setMastershipClaimInProgress();
        this.node.getPartitionService().pauseMigration();
        this.clusterService.setMasterAddress(this.node.getThisAddress());
        return true;
    }

    private boolean addSuspectedMember(MemberImpl suspectedMember, String reason, boolean shouldCloseConn) {
        if (this.getMember(suspectedMember.getAddress(), suspectedMember.getUuid()) == null) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Cannot suspect " + suspectedMember + ", since it's not a member.");
            }
            return false;
        }
        if (this.suspectedMembers.add(suspectedMember.getAddress())) {
            if (reason != null) {
                this.logger.warning(suspectedMember + " is suspected to be dead for reason: " + reason);
            } else {
                this.logger.warning(suspectedMember + " is suspected to be dead");
            }
        }
        if (shouldCloseConn) {
            this.closeConnection(suspectedMember.getAddress(), reason);
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeMember(MemberImpl member, String reason, boolean shouldCloseConn) {
        this.clusterServiceLock.lock();
        try {
            MemberMap currentMembers;
            assert (this.clusterService.isMaster()) : "Master: " + this.clusterService.getMasterAddress();
            if (!this.clusterService.isJoined()) {
                this.logger.warning("Not removing " + member + " for reason: " + reason + ", because not joined!");
                return;
            }
            if (shouldCloseConn) {
                this.closeConnection(member.getAddress(), reason);
            }
            if ((currentMembers = this.memberMapRef.get()).getMember(member.getAddress(), member.getUuid()) == null) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("No need to remove " + member + ", not a member.");
                }
                return;
            }
            this.logger.info("Removing " + member);
            this.clusterService.getClusterJoinManager().removeJoin(member.getAddress());
            this.clusterService.getClusterHeartbeatManager().removeMember(member);
            MemberMap newMembers = MemberMap.cloneExcluding(currentMembers, member);
            this.setMembers(newMembers);
            if (this.logger.isFineEnabled()) {
                this.logger.fine(member + " is removed. Publishing new member list.");
            }
            this.sendMemberListToOthers();
            this.handleMemberRemove(newMembers, member);
            this.clusterService.printMemberList();
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    private void closeConnection(Address address, String reason) {
        Object conn = this.node.getEndpointManager(EndpointQualifier.MEMBER).getConnection(address);
        if (conn != null) {
            conn.close(reason, null);
        }
    }

    private void handleMemberRemove(MemberMap newMembers, MemberImpl removedMember) {
        ClusterState clusterState = this.clusterService.getClusterState();
        if (!clusterState.isJoinAllowed()) {
            InternalHotRestartService hotRestartService;
            if (this.logger.isFineEnabled()) {
                this.logger.fine(removedMember + " is removed, added to members left while cluster is " + (Object)((Object)clusterState) + " state");
            }
            if (!(hotRestartService = this.node.getNodeExtension().getInternalHotRestartService()).isMemberExcluded(removedMember.getAddress(), removedMember.getUuid())) {
                this.addToMissingMembers(removedMember);
            }
        }
        this.onMemberRemove(removedMember);
        this.sendMembershipEventNotifications(removedMember, Collections.unmodifiableSet(new LinkedHashSet<MemberImpl>(newMembers.getMembers())), false);
    }

    void onMemberRemove(MemberImpl deadMember) {
        this.node.getPartitionService().memberRemoved(deadMember);
        this.nodeEngine.onMemberLeft(deadMember);
        this.node.getNodeExtension().onMemberListChange();
        Joiner joiner = this.node.getJoiner();
        if (joiner != null && joiner.getClass() == TcpIpJoiner.class) {
            ((TcpIpJoiner)joiner).onMemberRemoved(deadMember);
        }
    }

    void sendMembershipEvents(Collection<MemberImpl> currentMembers, Collection<MemberImpl> newMembers, boolean sortMembers) {
        ArrayList<Member> eventMembers = new ArrayList<Member>(currentMembers);
        if (!newMembers.isEmpty()) {
            for (MemberImpl newMember : newMembers) {
                this.node.getPartitionService().memberAdded(newMember);
                this.node.getNodeExtension().onMemberListChange();
                Joiner joiner = this.node.getJoiner();
                if (joiner != null && joiner.getClass() == TcpIpJoiner.class) {
                    ((TcpIpJoiner)joiner).onMemberAdded(newMember);
                }
                eventMembers.add(newMember);
                if (sortMembers) {
                    this.sortMembersInMembershipOrder(eventMembers);
                }
                this.sendMembershipEventNotifications(newMember, Collections.unmodifiableSet(new LinkedHashSet<Member>(eventMembers)), true);
            }
        }
    }

    private void sortMembersInMembershipOrder(List<Member> members) {
        final MemberMap memberMap = this.getMemberMap();
        Collections.sort(members, new Comparator<Member>(){

            @Override
            public int compare(Member m1, Member m2) {
                if (m1.equals(m2)) {
                    return 0;
                }
                return memberMap.isBeforeThan(m1.getAddress(), m2.getAddress()) ? -1 : 1;
            }
        });
    }

    private void sendMembershipEventNotifications(MemberImpl member, Set<Member> members, final boolean added) {
        int eventType = added ? 1 : 2;
        MembershipEvent membershipEvent = new MembershipEvent(this.clusterService, member, eventType, members);
        Collection<MembershipAwareService> membershipAwareServices = this.nodeEngine.getServices(MembershipAwareService.class);
        if (membershipAwareServices != null && !membershipAwareServices.isEmpty()) {
            final MembershipServiceEvent event = new MembershipServiceEvent(membershipEvent);
            for (final MembershipAwareService service : membershipAwareServices) {
                this.nodeEngine.getExecutionService().execute("hz:cluster:event", new Runnable(){

                    @Override
                    public void run() {
                        if (added) {
                            service.memberAdded(event);
                        } else {
                            service.memberRemoved(event);
                        }
                    }
                });
            }
        }
        InternalEventService eventService = this.nodeEngine.getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations("hz:core:clusterService", "hz:core:clusterService");
        for (EventRegistration reg : registrations) {
            eventService.publishEvent("hz:core:clusterService", reg, (Object)membershipEvent, reg.getId().hashCode());
        }
    }

    private boolean shouldClaimMastership(MemberMap memberMap) {
        if (this.clusterService.isMaster()) {
            return false;
        }
        for (MemberImpl m : memberMap.headMemberSet(this.clusterService.getLocalMember(), false)) {
            if (this.isMemberSuspected(m.getAddress())) continue;
            return false;
        }
        return true;
    }

    private MembersView decideNewMembersView(MemberMap localMemberMap, Set<Member> members) {
        HashMap<Address, Future<MembersView>> futures = new HashMap<Address, Future<MembersView>>();
        MembersView latestMembersView = this.fetchLatestMembersView(localMemberMap, members, futures);
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Latest " + latestMembersView + " before final decision...");
        }
        ArrayList<MemberInfo> finalMembers = new ArrayList<MemberInfo>();
        for (MemberInfo memberInfo : latestMembersView.getMembers()) {
            Address address = memberInfo.getAddress();
            if (this.node.getThisAddress().equals(address)) {
                finalMembers.add(memberInfo);
                continue;
            }
            Future future = (Future)futures.get(address);
            if (this.isMemberSuspected(address)) {
                if (!this.logger.isFineEnabled()) continue;
                this.logger.fine(memberInfo + " is excluded because suspected");
                continue;
            }
            if (future == null || !future.isDone()) {
                if (!this.logger.isFineEnabled()) continue;
                this.logger.fine(memberInfo + " is excluded because I don't know its response");
                continue;
            }
            this.addAcceptedMemberInfo(finalMembers, memberInfo, future);
        }
        int finalVersion = latestMembersView.getVersion() + 1;
        return new MembersView(finalVersion, finalMembers);
    }

    private void addAcceptedMemberInfo(List<MemberInfo> finalMembers, MemberInfo memberInfo, Future<MembersView> future) {
        block3: {
            try {
                future.get();
                finalMembers.add(memberInfo);
            }
            catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            catch (ExecutionException e) {
                if (!this.logger.isFineEnabled()) break block3;
                this.logger.fine(memberInfo + " is excluded because I couldn't get its acceptance", e);
            }
        }
    }

    private MembersView fetchLatestMembersView(MemberMap localMemberMap, Set<Member> members, Map<Address, Future<MembersView>> futures) {
        MembersView latestMembersView = localMemberMap.toTailMembersView(this.node.getLocalMember(), true);
        for (Member member : members) {
            futures.put(member.getAddress(), this.invokeFetchMembersViewOp(member.getAddress(), member.getUuid()));
        }
        long mastershipClaimTimeout = TimeUnit.SECONDS.toMillis(this.mastershipClaimTimeoutSeconds);
        while (this.clusterService.isJoined()) {
            boolean done = true;
            for (Map.Entry<Address, Future<MembersView>> e : new ArrayList<Map.Entry<Address, Future<MembersView>>>(futures.entrySet())) {
                long start;
                block9: {
                    Address address = e.getKey();
                    Future<MembersView> future = e.getValue();
                    start = System.nanoTime();
                    try {
                        long timeout = Math.min(5000L, Math.max(mastershipClaimTimeout, 1L));
                        MembersView membersView = future.get(timeout, TimeUnit.MILLISECONDS);
                        if (membersView.isLaterThan(latestMembersView)) {
                            if (this.logger.isFineEnabled()) {
                                this.logger.fine("A more recent " + membersView + " is received from " + address);
                            }
                            latestMembersView = membersView;
                            done &= !this.fetchMembersViewFromNewMembers(membersView, futures);
                        }
                    }
                    catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                    catch (ExecutionException ignored) {
                        EmptyStatement.ignore(ignored);
                    }
                    catch (TimeoutException ignored) {
                        MemberInfo memberInfo = latestMembersView.getMember(address);
                        if (mastershipClaimTimeout <= 0L || this.isMemberSuspected(address) || memberInfo == null) break block9;
                        done = false;
                        futures.put(address, this.invokeFetchMembersViewOp(address, memberInfo.getUuid()));
                    }
                }
                mastershipClaimTimeout -= TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            }
            if (!done) continue;
            break;
        }
        return latestMembersView;
    }

    private boolean fetchMembersViewFromNewMembers(MembersView membersView, Map<Address, Future<MembersView>> futures) {
        boolean isNewMemberPresent = false;
        for (MemberInfo memberInfo : membersView.getMembers()) {
            Address memberAddress = memberInfo.getAddress();
            if (this.node.getThisAddress().equals(memberAddress) || this.isMemberSuspected(memberAddress) || futures.containsKey(memberAddress)) continue;
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Asking MembersView of " + memberAddress);
            }
            futures.put(memberAddress, this.invokeFetchMembersViewOp(memberAddress, memberInfo.getUuid()));
            isNewMemberPresent = true;
        }
        return isNewMemberPresent;
    }

    private Future<MembersView> invokeFetchMembersViewOp(Address target, String targetUuid) {
        Operation op = new FetchMembersViewOp(targetUuid).setCallerUuid(this.clusterService.getThisUuid());
        return this.nodeEngine.getOperationService().createInvocationBuilder("hz:core:clusterService", op, target).setTryCount(this.mastershipClaimTimeoutSeconds).setCallTimeout(TimeUnit.SECONDS.toMillis(this.mastershipClaimTimeoutSeconds)).invoke();
    }

    boolean isMissingMember(Address address, String uuid) {
        Map<Object, MemberImpl> m = this.missingMembersRef.get();
        return this.isHotRestartEnabled() ? m.containsKey(uuid) : m.containsKey(address);
    }

    MemberImpl getMissingMember(Address address, String uuid) {
        Map<Object, MemberImpl> m = this.missingMembersRef.get();
        return this.isHotRestartEnabled() ? m.get(uuid) : m.get(address);
    }

    Collection<MemberImpl> getMissingMembers() {
        return Collections.unmodifiableCollection(this.missingMembersRef.get().values());
    }

    private void addToMissingMembers(MemberImpl ... members) {
        HashMap<Object, MemberImpl> m = new HashMap<Object, MemberImpl>(this.missingMembersRef.get());
        if (this.isHotRestartEnabled()) {
            for (MemberImpl member : members) {
                m.put(member.getUuid(), member);
            }
        } else {
            for (MemberImpl member : members) {
                m.put(member.getAddress(), member);
            }
        }
        this.missingMembersRef.set(Collections.unmodifiableMap(m));
    }

    private void removeFromMissingMembers(MemberImpl ... members) {
        HashMap<Object, MemberImpl> m = new HashMap<Object, MemberImpl>(this.missingMembersRef.get());
        if (this.isHotRestartEnabled()) {
            for (MemberImpl member : members) {
                m.remove(member.getUuid());
            }
        } else {
            for (MemberImpl member : members) {
                m.remove(member.getAddress());
            }
        }
        this.missingMembersRef.set(Collections.unmodifiableMap(m));
    }

    private boolean isHotRestartEnabled() {
        InternalHotRestartService hotRestartService = this.node.getNodeExtension().getInternalHotRestartService();
        return hotRestartService.isEnabled();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Collection<Member> getActiveAndMissingMembers() {
        this.clusterServiceLock.lock();
        try {
            Map<Object, MemberImpl> m = this.missingMembersRef.get();
            if (m.isEmpty()) {
                Set<Member> set = this.getMemberSet();
                return set;
            }
            Collection<MemberImpl> removedMembers = m.values();
            Set<MemberImpl> members = this.memberMapRef.get().getMembers();
            ArrayList<Member> allMembers = new ArrayList<Member>(members.size() + removedMembers.size());
            allMembers.addAll(members);
            allMembers.addAll(removedMembers);
            ArrayList<Member> arrayList = allMembers;
            return arrayList;
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void setMissingMembers(Collection<MemberImpl> members) {
        this.clusterServiceLock.lock();
        try {
            HashMap<Object, MemberImpl> m = new HashMap<Object, MemberImpl>(members.size());
            if (this.isHotRestartEnabled()) {
                for (MemberImpl member : members) {
                    m.put(member.getUuid(), member);
                }
            } else {
                for (MemberImpl member : members) {
                    m.put(member.getAddress(), member);
                }
            }
            this.missingMembersRef.set(Collections.unmodifiableMap(m));
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void shrinkMissingMembers(Collection<String> memberUuidsToRemove) {
        this.clusterServiceLock.lock();
        try {
            HashMap<Object, MemberImpl> m = new HashMap<Object, MemberImpl>(this.missingMembersRef.get());
            Iterator it = m.values().iterator();
            while (it.hasNext()) {
                MemberImpl member = (MemberImpl)it.next();
                if (!memberUuidsToRemove.contains(member.getUuid())) continue;
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Removing " + member + " from members removed in not joinable state.");
                }
                it.remove();
            }
            this.missingMembersRef.set(Collections.unmodifiableMap(m));
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void removeAllMissingMembers() {
        this.clusterServiceLock.lock();
        try {
            Map<Object, MemberImpl> m = this.missingMembersRef.get();
            Collection<MemberImpl> members = m.values();
            this.missingMembersRef.set(Collections.emptyMap());
            for (MemberImpl member : members) {
                this.onMemberRemove(member);
            }
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MembersView promoteToDataMember(Address address, String uuid) {
        this.clusterServiceLock.lock();
        try {
            this.ensureLiteMemberPromotionIsAllowed();
            MemberMap memberMap = this.getMemberMap();
            MemberImpl member = memberMap.getMember(address, uuid);
            if (member == null) {
                throw new IllegalStateException(uuid + "/" + address + " is not a member!");
            }
            if (!member.isLiteMember()) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine(member + " is not lite member, no promotion is required.");
                }
                MembersView membersView = memberMap.toMembersView();
                return membersView;
            }
            this.logger.info("Promoting " + member + " to normal member.");
            MemberImpl[] members = memberMap.getMembers().toArray(new MemberImpl[0]);
            for (int i = 0; i < members.length; ++i) {
                if (!member.equals(members[i])) continue;
                member = member.localMember() ? this.clusterService.promoteAndGetLocalMember() : new MemberImpl.Builder(member.getAddressMap()).version(member.getVersion()).localMember(member.localMember()).uuid(member.getUuid()).attributes(member.getAttributes()).memberListJoinVersion(members[i].getMemberListJoinVersion()).instance(this.node.hazelcastInstance).build();
                members[i] = member;
                break;
            }
            MemberMap newMemberMap = MemberMap.createNew(memberMap.getVersion() + 1, members);
            this.setMembers(newMemberMap);
            this.sendMemberListToOthers();
            this.node.partitionService.memberAdded(member);
            this.clusterService.printMemberList();
            MembersView membersView = newMemberMap.toMembersView();
            return membersView;
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    private void ensureLiteMemberPromotionIsAllowed() {
        if (!this.clusterService.isMaster()) {
            throw new IllegalStateException("This node is not master!");
        }
        if (this.clusterService.getClusterJoinManager().isMastershipClaimInProgress()) {
            throw new IllegalStateException("Mastership claim is in progress!");
        }
        ClusterState state = this.clusterService.getClusterState();
        if (!state.isMigrationAllowed()) {
            throw new IllegalStateException("Lite member promotion is not allowed when cluster state is " + (Object)((Object)state));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean verifySplitBrainMergeMemberListVersion(SplitBrainJoinMessage joinMessage) {
        Address caller = joinMessage.getAddress();
        int callerMemberListVersion = joinMessage.getMemberListVersion();
        this.clusterServiceLock.lock();
        try {
            if (!this.clusterService.isMaster()) {
                this.logger.warning("Cannot verify member list version: " + callerMemberListVersion + " from " + caller + " because this node is not master");
                boolean bl = false;
                return bl;
            }
            if (this.clusterService.getClusterJoinManager().isMastershipClaimInProgress()) {
                this.logger.warning("Cannot verify member list version: " + callerMemberListVersion + " from " + caller + " because mastership claim is in progress");
                boolean bl = false;
                return bl;
            }
            MemberMap memberMap = this.getMemberMap();
            if (memberMap.getVersion() < callerMemberListVersion) {
                int newVersion = callerMemberListVersion + 1;
                this.logger.info("Updating local member list version: " + memberMap.getVersion() + " to " + newVersion + " because of split brain merge caller: " + caller + " with member list version: " + callerMemberListVersion);
                MemberImpl[] members = memberMap.getMembers().toArray(new MemberImpl[0]);
                MemberMap newMemberMap = MemberMap.createNew(newVersion, members);
                this.setMembers(newMemberMap);
                this.sendMemberListToOthers();
                this.clusterService.printMemberList();
            }
            boolean bl = true;
            return bl;
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    void reset() {
        this.clusterServiceLock.lock();
        try {
            this.memberMapRef.set(MemberMap.singleton(this.clusterService.getLocalMember()));
            this.missingMembersRef.set(Collections.emptyMap());
            this.suspectedMembers.clear();
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    private class DecideNewMembersViewTask
    implements Runnable {
        final MemberMap localMemberMap;
        final Set<Member> membersToAsk;

        DecideNewMembersViewTask(MemberMap localMemberMap, Set<Member> membersToAsk) {
            this.localMemberMap = localMemberMap;
            this.membersToAsk = membersToAsk;
        }

        @Override
        public void run() {
            try {
                this.innerRun();
            }
            catch (Throwable e) {
                MembershipManager.this.logger.warning("Exception thrown while running DecideNewMembersViewTask", e);
            }
            finally {
                MembershipManager.this.node.getPartitionService().resumeMigration();
            }
        }

        private void innerRun() {
            MembersView newMembersView = MembershipManager.this.decideNewMembersView(this.localMemberMap, this.membersToAsk);
            MembershipManager.this.clusterServiceLock.lock();
            try {
                if (!MembershipManager.this.clusterService.isJoined()) {
                    if (MembershipManager.this.logger.isFineEnabled()) {
                        MembershipManager.this.logger.fine("Ignoring decided members view after mastership claim: " + newMembersView + ", because not joined!");
                    }
                    return;
                }
                MemberImpl localMember = MembershipManager.this.clusterService.getLocalMember();
                if (!newMembersView.containsMember(localMember.getAddress(), localMember.getUuid())) {
                    if (MembershipManager.this.logger.isFineEnabled()) {
                        MembershipManager.this.logger.fine("Ignoring decided members view after mastership claim: " + newMembersView + ", because current local member: " + localMember + " not in decided members view.");
                    }
                    return;
                }
                MembershipManager.this.updateMembers(newMembersView);
                MembershipManager.this.clusterService.getClusterJoinManager().reset();
                MembershipManager.this.sendMemberListToOthers();
                MembershipManager.this.logger.info("Mastership is claimed with: " + newMembersView);
            }
            finally {
                MembershipManager.this.clusterServiceLock.unlock();
            }
        }
    }
}

