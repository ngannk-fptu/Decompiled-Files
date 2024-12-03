/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.cluster.MemberAttributeOperationType;
import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.InitialMembershipEvent;
import com.hazelcast.core.InitialMembershipListener;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MemberSelector;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.hotrestart.HotRestartService;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.LifecycleServiceImpl;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.cluster.impl.ClusterClockImpl;
import com.hazelcast.internal.cluster.impl.ClusterHeartbeatManager;
import com.hazelcast.internal.cluster.impl.ClusterJoinManager;
import com.hazelcast.internal.cluster.impl.ClusterMergeTask;
import com.hazelcast.internal.cluster.impl.ClusterStateChange;
import com.hazelcast.internal.cluster.impl.ClusterStateManager;
import com.hazelcast.internal.cluster.impl.MemberMap;
import com.hazelcast.internal.cluster.impl.MemberSelectingCollection;
import com.hazelcast.internal.cluster.impl.MembersView;
import com.hazelcast.internal.cluster.impl.MembersViewMetadata;
import com.hazelcast.internal.cluster.impl.MembershipManager;
import com.hazelcast.internal.cluster.impl.SplitBrainHandler;
import com.hazelcast.internal.cluster.impl.VersionMismatchException;
import com.hazelcast.internal.cluster.impl.operations.ExplicitSuspicionOp;
import com.hazelcast.internal.cluster.impl.operations.OnJoinOp;
import com.hazelcast.internal.cluster.impl.operations.PromoteLiteMemberOp;
import com.hazelcast.internal.cluster.impl.operations.ShutdownNodeOp;
import com.hazelcast.internal.cluster.impl.operations.TriggerExplicitSuspicionOp;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.ConnectionListener;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.MemberAttributeServiceEvent;
import com.hazelcast.spi.MembershipAwareService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.TransactionalService;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.eventservice.InternalEventService;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionalObject;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.UuidUtil;
import com.hazelcast.util.executor.ExecutorType;
import com.hazelcast.version.Version;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class ClusterServiceImpl
implements ClusterService,
ConnectionListener,
ManagedService,
EventPublishingService<MembershipEvent, MembershipListener>,
TransactionalService {
    public static final String SERVICE_NAME = "hz:core:clusterService";
    public static final String SPLIT_BRAIN_HANDLER_EXECUTOR_NAME = "hz:cluster:splitbrain";
    static final String CLUSTER_EXECUTOR_NAME = "hz:cluster";
    static final String MEMBERSHIP_EVENT_EXECUTOR_NAME = "hz:cluster:event";
    static final String VERSION_AUTO_UPGRADE_EXECUTOR_NAME = "hz:cluster:version:auto:upgrade";
    private static final int DEFAULT_MERGE_RUN_DELAY_MILLIS = 100;
    private static final long CLUSTER_SHUTDOWN_SLEEP_DURATION_IN_MILLIS = 1000L;
    private static final boolean ASSERTION_ENABLED = ClusterServiceImpl.class.desiredAssertionStatus();
    private final boolean useLegacyMemberListFormat;
    private final Node node;
    private final ILogger logger;
    private final NodeEngineImpl nodeEngine;
    private final ClusterClockImpl clusterClock;
    private final MembershipManager membershipManager;
    private final ClusterJoinManager clusterJoinManager;
    private final ClusterStateManager clusterStateManager;
    private final ClusterHeartbeatManager clusterHeartbeatManager;
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicBoolean joined = new AtomicBoolean(false);
    private volatile String clusterId;
    private volatile Address masterAddress;
    private volatile MemberImpl localMember;

    public ClusterServiceImpl(Node node, MemberImpl localMember) {
        this.node = node;
        this.localMember = localMember;
        this.nodeEngine = node.nodeEngine;
        this.logger = node.getLogger(ClusterService.class.getName());
        this.clusterClock = new ClusterClockImpl(this.logger);
        this.useLegacyMemberListFormat = node.getProperties().getBoolean(GroupProperty.USE_LEGACY_MEMBER_LIST_FORMAT);
        this.membershipManager = new MembershipManager(node, this, this.lock);
        this.clusterStateManager = new ClusterStateManager(node, this.lock);
        this.clusterJoinManager = new ClusterJoinManager(node, this, this.lock);
        this.clusterHeartbeatManager = new ClusterHeartbeatManager(node, this, this.lock);
        node.networkingService.getEndpointManager(EndpointQualifier.MEMBER).addConnectionListener(this);
        InternalExecutionService executionService = this.nodeEngine.getExecutionService();
        executionService.register(CLUSTER_EXECUTOR_NAME, 2, Integer.MAX_VALUE, ExecutorType.CACHED);
        executionService.register(SPLIT_BRAIN_HANDLER_EXECUTOR_NAME, 2, Integer.MAX_VALUE, ExecutorType.CACHED);
        executionService.register(MEMBERSHIP_EVENT_EXECUTOR_NAME, 1, Integer.MAX_VALUE, ExecutorType.CACHED);
        executionService.register(VERSION_AUTO_UPGRADE_EXECUTOR_NAME, 1, Integer.MAX_VALUE, ExecutorType.CACHED);
        this.registerMetrics();
    }

    private void registerMetrics() {
        MetricsRegistry metricsRegistry = this.node.nodeEngine.getMetricsRegistry();
        metricsRegistry.scanAndRegister(this.clusterClock, "cluster.clock");
        metricsRegistry.scanAndRegister(this.clusterHeartbeatManager, "cluster.heartbeat");
        metricsRegistry.scanAndRegister(this, "cluster");
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        long mergeFirstRunDelayMs = this.node.getProperties().getPositiveMillisOrDefault(GroupProperty.MERGE_FIRST_RUN_DELAY_SECONDS, 100L);
        long mergeNextRunDelayMs = this.node.getProperties().getPositiveMillisOrDefault(GroupProperty.MERGE_NEXT_RUN_DELAY_SECONDS, 100L);
        ExecutionService executionService = nodeEngine.getExecutionService();
        executionService.scheduleWithRepetition(SPLIT_BRAIN_HANDLER_EXECUTOR_NAME, new SplitBrainHandler(this.node), mergeFirstRunDelayMs, mergeNextRunDelayMs, TimeUnit.MILLISECONDS);
        this.membershipManager.init();
        this.clusterHeartbeatManager.init();
    }

    public void sendLocalMembershipEvent() {
        this.membershipManager.sendMembershipEvents(Collections.emptySet(), Collections.singleton(this.getLocalMember()), false);
    }

    public void handleExplicitSuspicion(MembersViewMetadata expectedMembersViewMetadata, Address suspectedAddress) {
        this.membershipManager.handleExplicitSuspicion(expectedMembersViewMetadata, suspectedAddress);
    }

    public void handleExplicitSuspicionTrigger(Address caller, int callerMemberListVersion, MembersViewMetadata suspectedMembersViewMetadata) {
        this.membershipManager.handleExplicitSuspicionTrigger(caller, callerMemberListVersion, suspectedMembersViewMetadata);
    }

    public void suspectMember(Member suspectedMember, String reason, boolean destroyConnection) {
        this.membershipManager.suspectMember((MemberImpl)suspectedMember, reason, destroyConnection);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void suspectAddressIfNotConnected(Address address) {
        this.lock.lock();
        try {
            MemberImpl member = this.getMember(address);
            if (member == null) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Cannot suspect " + address + ", since it's not a member.");
                }
                return;
            }
            Object conn = this.node.getEndpointManager(EndpointQualifier.MEMBER).getConnection(address);
            if (conn != null && conn.isAlive()) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Cannot suspect " + member + ", since there's a live connection -> " + conn);
                }
                return;
            }
            this.suspectMember(member, "No connection", false);
        }
        finally {
            this.lock.unlock();
        }
    }

    void sendExplicitSuspicion(MembersViewMetadata endpointMembersViewMetadata) {
        Address endpoint = endpointMembersViewMetadata.getMemberAddress();
        if (endpoint.equals(this.node.getThisAddress())) {
            this.logger.warning("Cannot send explicit suspicion for " + endpointMembersViewMetadata + " to itself.");
            return;
        }
        if (!this.isJoined()) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Cannot send explicit suspicion, not joined yet!");
            }
            return;
        }
        Version clusterVersion = this.getClusterVersion();
        assert (!clusterVersion.isUnknown()) : "Cluster version should not be unknown after join!";
        ExplicitSuspicionOp op = new ExplicitSuspicionOp(endpointMembersViewMetadata);
        this.nodeEngine.getOperationService().send(op, endpoint);
    }

    void sendExplicitSuspicionTrigger(Address triggerTo, MembersViewMetadata endpointMembersViewMetadata) {
        if (triggerTo.equals(this.node.getThisAddress())) {
            this.logger.warning("Cannot send explicit suspicion trigger for " + endpointMembersViewMetadata + " to itself.");
            return;
        }
        int memberListVersion = this.membershipManager.getMemberListVersion();
        TriggerExplicitSuspicionOp op = new TriggerExplicitSuspicionOp(memberListVersion, endpointMembersViewMetadata);
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        operationService.send(op, triggerTo);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MembersView handleMastershipClaim(Address candidateAddress, String candidateUuid) {
        Preconditions.checkNotNull(candidateAddress);
        Preconditions.checkNotNull(candidateUuid);
        Preconditions.checkFalse(this.getThisAddress().equals(candidateAddress), "cannot accept my own mastership claim!");
        this.lock.lock();
        try {
            Preconditions.checkTrue(this.isJoined(), candidateAddress + " claims mastership but this node is not joined!");
            Preconditions.checkFalse(this.isMaster(), candidateAddress + " claims mastership but this node is master!");
            MemberImpl masterCandidate = this.membershipManager.getMember(candidateAddress, candidateUuid);
            Preconditions.checkTrue(masterCandidate != null, candidateAddress + " claims mastership but it is not a member!");
            MemberMap memberMap = this.membershipManager.getMemberMap();
            if (!this.shouldAcceptMastership(memberMap, masterCandidate)) {
                String message = "Cannot accept mastership claim of " + candidateAddress + " at the moment. There are more suitable master candidates in the member list.";
                this.logger.fine(message);
                throw new RetryableHazelcastException(message);
            }
            if (!this.membershipManager.clearMemberSuspicion(candidateAddress, "Mastership claim")) {
                throw new IllegalStateException("Cannot accept mastership claim of " + candidateAddress + ". " + this.getMasterAddress() + " is already master.");
            }
            this.setMasterAddress(masterCandidate.getAddress());
            MembersView response = memberMap.toTailMembersView(masterCandidate, true);
            this.logger.warning("Mastership of " + candidateAddress + " is accepted. Response: " + response);
            MembersView membersView = response;
            return membersView;
        }
        finally {
            this.lock.unlock();
        }
    }

    private boolean shouldAcceptMastership(MemberMap memberMap, MemberImpl candidate) {
        assert (this.lock.isHeldByCurrentThread()) : "Called without holding cluster service lock!";
        for (MemberImpl member : memberMap.headMemberSet(candidate, false)) {
            if (this.membershipManager.isMemberSuspected(member.getAddress())) continue;
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Should not accept mastership claim of " + candidate + ", because " + member + " is not suspected at the moment and is before than " + candidate + " in the member list.");
            }
            return false;
        }
        return true;
    }

    public void merge(Address newTargetAddress) {
        this.node.getJoiner().setTargetAddress(newTargetAddress);
        LifecycleServiceImpl lifecycleService = this.node.hazelcastInstance.getLifecycleService();
        lifecycleService.runUnderLifecycleLock(new ClusterMergeTask(this.node));
    }

    @Override
    public void reset() {
        this.lock.lock();
        try {
            this.resetJoinState();
            this.resetLocalMemberUuid();
            this.resetClusterId();
            this.clearInternalState();
        }
        finally {
            this.lock.unlock();
        }
    }

    private void resetLocalMemberUuid() {
        MemberImpl memberWithNewUuid;
        assert (this.lock.isHeldByCurrentThread()) : "Called without holding cluster service lock!";
        assert (!this.isJoined()) : "Cannot reset local member UUID when joined.";
        Address memberAddress = this.node.getThisAddress();
        Map<EndpointQualifier, Address> addressMap = this.localMember.getAddressMap();
        String newUuid = UuidUtil.createMemberUuid(memberAddress);
        this.logger.warning("Resetting local member UUID. Previous: " + this.localMember.getUuid() + ", new: " + newUuid);
        this.localMember = memberWithNewUuid = new MemberImpl.Builder(addressMap).version(this.localMember.getVersion()).localMember(true).uuid(newUuid).attributes(this.localMember.getAttributes()).liteMember(this.localMember.isLiteMember()).memberListJoinVersion(this.localMember.getMemberListJoinVersion()).instance(this.node.hazelcastInstance).build();
        this.node.loggingService.setThisMember(this.localMember);
    }

    public void resetJoinState() {
        this.lock.lock();
        try {
            this.setMasterAddress(null);
            this.setJoined(false);
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean finalizeJoin(MembersView membersView, Address callerAddress, String callerUuid, String targetUuid, String clusterId, ClusterState clusterState, Version clusterVersion, long clusterStartTime, long masterTime, OnJoinOp preJoinOp) {
        this.lock.lock();
        try {
            if (!this.checkValidMaster(callerAddress)) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Not finalizing join because caller: " + callerAddress + " is not known master: " + this.getMasterAddress());
                }
                MembersViewMetadata membersViewMetadata = new MembersViewMetadata(callerAddress, callerUuid, callerAddress, membersView.getVersion());
                this.sendExplicitSuspicion(membersViewMetadata);
                boolean bl = false;
                return bl;
            }
            if (this.isJoined()) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Node is already joined... No need to finalize join...");
                }
                boolean membersViewMetadata = false;
                return membersViewMetadata;
            }
            this.checkMemberUpdateContainsLocalMember(membersView, targetUuid);
            try {
                this.initialClusterState(clusterState, clusterVersion);
            }
            catch (VersionMismatchException e) {
                this.logger.severe(String.format("This member will shutdown because it cannot join the cluster: %s", e.getMessage()));
                this.node.shutdown(true);
                boolean bl = false;
                this.lock.unlock();
                return bl;
            }
            this.setClusterId(clusterId);
            ClusterClockImpl clusterClock = this.getClusterClock();
            clusterClock.setClusterStartTime(clusterStartTime);
            clusterClock.setMasterTime(masterTime);
            if (preJoinOp != null) {
                this.nodeEngine.getOperationService().run(preJoinOp);
            }
            this.membershipManager.updateMembers(membersView);
            this.clusterHeartbeatManager.heartbeat();
            this.setJoined(true);
            boolean bl = true;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean updateMembers(MembersView membersView, Address callerAddress, String callerUuid, String targetUuid) {
        this.lock.lock();
        try {
            if (!this.isJoined()) {
                this.logger.warning("Not updating members received from caller: " + callerAddress + " because node is not joined! ");
                boolean bl = false;
                return bl;
            }
            if (!this.checkValidMaster(callerAddress)) {
                this.logger.warning("Not updating members because caller: " + callerAddress + " is not known master: " + this.getMasterAddress());
                MembersViewMetadata callerMembersViewMetadata = new MembersViewMetadata(callerAddress, callerUuid, callerAddress, membersView.getVersion());
                if (!this.clusterJoinManager.isMastershipClaimInProgress()) {
                    this.sendExplicitSuspicion(callerMembersViewMetadata);
                }
                boolean bl = false;
                return bl;
            }
            this.checkMemberUpdateContainsLocalMember(membersView, targetUuid);
            if (!this.shouldProcessMemberUpdate(membersView)) {
                boolean bl = false;
                return bl;
            }
            this.membershipManager.updateMembers(membersView);
            boolean bl = true;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    private void checkMemberUpdateContainsLocalMember(MembersView membersView, String targetUuid) {
        String thisUuid = this.getThisUuid();
        if (!thisUuid.equals(targetUuid)) {
            String msg = "Not applying member update because target uuid: " + targetUuid + " is different! -> " + membersView + ", local member: " + this.localMember;
            throw new IllegalArgumentException(msg);
        }
        MemberImpl localMember = this.getLocalMember();
        if (!membersView.containsMember(localMember.getAddress(), localMember.getUuid())) {
            String msg = "Not applying member update because member list doesn't contain us! -> " + membersView + ", local member: " + localMember;
            throw new IllegalArgumentException(msg);
        }
    }

    private boolean checkValidMaster(Address callerAddress) {
        return callerAddress != null && callerAddress.equals(this.getMasterAddress());
    }

    private boolean shouldProcessMemberUpdate(MembersView membersView) {
        int memberListVersion = this.membershipManager.getMemberListVersion();
        if (memberListVersion > membersView.getVersion()) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Received an older member update, ignoring... Current version: " + memberListVersion + ", Received version: " + membersView.getVersion());
            }
            return false;
        }
        if (memberListVersion == membersView.getVersion()) {
            if (ASSERTION_ENABLED) {
                MemberMap memberMap = this.membershipManager.getMemberMap();
                Collection<Address> currentAddresses = memberMap.getAddresses();
                Set<Address> newAddresses = membersView.getAddresses();
                assert (currentAddresses.size() == newAddresses.size() && newAddresses.containsAll(currentAddresses)) : "Member view versions are same but new member view doesn't match the current! Current: " + memberMap.toMembersView() + ", New: " + membersView;
            }
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Received a periodic member update, ignoring... Version: " + memberListVersion);
            }
            return false;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateMemberAttribute(String uuid, MemberAttributeOperationType operationType, String key, Object value) {
        this.lock.lock();
        try {
            MemberImpl member = this.membershipManager.getMember(uuid);
            if (!member.equals(this.getLocalMember())) {
                member.updateAttribute(operationType, key, value);
            }
            this.sendMemberAttributeEvent(member, operationType, key, value);
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public void connectionAdded(Connection connection) {
    }

    @Override
    public void connectionRemoved(Connection connection) {
        Address masterAddress;
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Removed connection to " + connection.getEndPoint());
        }
        if (!this.isJoined() && (masterAddress = this.getMasterAddress()) != null && masterAddress.equals(connection.getEndPoint())) {
            this.setMasterAddressToJoin(null);
        }
    }

    public NodeEngineImpl getNodeEngine() {
        return this.nodeEngine;
    }

    public boolean isMissingMember(Address address, String uuid) {
        return this.membershipManager.isMissingMember(address, uuid);
    }

    public Collection<Member> getActiveAndMissingMembers() {
        return this.membershipManager.getActiveAndMissingMembers();
    }

    public void notifyForRemovedMember(MemberImpl member) {
        this.lock.lock();
        try {
            this.membershipManager.onMemberRemove(member);
        }
        finally {
            this.lock.unlock();
        }
    }

    public void shrinkMissingMembers(Collection<String> memberUuidsToRemove) {
        this.membershipManager.shrinkMissingMembers(memberUuidsToRemove);
    }

    private void sendMemberAttributeEvent(MemberImpl member, MemberAttributeOperationType operationType, String key, Object value) {
        final MemberAttributeServiceEvent event = new MemberAttributeServiceEvent((Cluster)this, member, operationType, key, value);
        MemberAttributeEvent attributeEvent = new MemberAttributeEvent(this, member, operationType, key, value);
        Collection<MembershipAwareService> membershipAwareServices = this.nodeEngine.getServices(MembershipAwareService.class);
        if (membershipAwareServices != null && !membershipAwareServices.isEmpty()) {
            for (final MembershipAwareService service : membershipAwareServices) {
                this.nodeEngine.getExecutionService().execute("hz:system", new Runnable(){

                    @Override
                    public void run() {
                        service.memberAttributeChanged(event);
                    }
                });
            }
        }
        InternalEventService eventService = this.nodeEngine.getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations(SERVICE_NAME, SERVICE_NAME);
        for (EventRegistration reg : registrations) {
            eventService.publishEvent(SERVICE_NAME, reg, (Object)attributeEvent, reg.getId().hashCode());
        }
    }

    @Override
    public MemberImpl getMember(Address address) {
        if (address == null) {
            return null;
        }
        return this.membershipManager.getMember(address);
    }

    @Override
    public MemberImpl getMember(String uuid) {
        if (uuid == null) {
            return null;
        }
        return this.membershipManager.getMember(uuid);
    }

    @Override
    public MemberImpl getMember(Address address, String uuid) {
        if (address == null || uuid == null) {
            return null;
        }
        return this.membershipManager.getMember(address, uuid);
    }

    @Override
    public Collection<MemberImpl> getMemberImpls() {
        return this.membershipManager.getMembers();
    }

    public Collection<Address> getMemberAddresses() {
        return this.membershipManager.getMemberMap().getAddresses();
    }

    @Override
    public Set<Member> getMembers() {
        return this.membershipManager.getMemberSet();
    }

    @Override
    public Collection<Member> getMembers(MemberSelector selector) {
        return new MemberSelectingCollection<Member>(this.membershipManager.getMembers(), selector);
    }

    @Override
    public void shutdown(boolean terminate) {
        this.clearInternalState();
    }

    private void clearInternalState() {
        this.lock.lock();
        try {
            this.membershipManager.reset();
            this.clusterHeartbeatManager.reset();
            this.clusterStateManager.reset();
            this.clusterJoinManager.reset();
            this.resetJoinState();
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean setMasterAddressToJoin(Address master) {
        this.lock.lock();
        try {
            if (this.isJoined()) {
                Address currentMasterAddress = this.getMasterAddress();
                if (!currentMasterAddress.equals(master)) {
                    this.logger.warning("Cannot set master address to " + master + " because node is already joined! Current master: " + currentMasterAddress);
                } else if (this.logger.isFineEnabled()) {
                    this.logger.fine("Master address is already set to " + master);
                }
                boolean bl = false;
                return bl;
            }
            this.setMasterAddress(master);
            boolean bl = true;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    void setMasterAddress(Address master) {
        assert (this.lock.isHeldByCurrentThread()) : "Called without holding cluster service lock!";
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Setting master address to " + master);
        }
        this.masterAddress = master;
    }

    @Override
    public Address getMasterAddress() {
        return this.masterAddress;
    }

    @Override
    public boolean isMaster() {
        return this.node.getThisAddress().equals(this.masterAddress);
    }

    @Override
    public Address getThisAddress() {
        return this.node.getThisAddress();
    }

    @Override
    public MemberImpl getLocalMember() {
        return this.localMember;
    }

    public String getThisUuid() {
        return this.localMember.getUuid();
    }

    void setJoined(boolean val) {
        assert (this.lock.isHeldByCurrentThread()) : "Called without holding cluster service lock!";
        this.joined.set(val);
    }

    @Override
    public boolean isJoined() {
        return this.joined.get();
    }

    @Override
    @Probe
    public int getSize() {
        return this.membershipManager.getMemberMap().size();
    }

    @Override
    public int getSize(MemberSelector selector) {
        int size = 0;
        for (MemberImpl member : this.membershipManager.getMembers()) {
            if (!selector.select(member)) continue;
            ++size;
        }
        return size;
    }

    @Override
    public ClusterClockImpl getClusterClock() {
        return this.clusterClock;
    }

    @Override
    public long getClusterTime() {
        return this.clusterClock.getClusterTime();
    }

    @Override
    public String getClusterId() {
        return this.clusterId;
    }

    void setClusterId(String newClusterId) {
        assert (this.lock.isHeldByCurrentThread()) : "Called without holding cluster service lock!";
        assert (this.clusterId == null) : "Cluster ID should be null: " + this.clusterId;
        this.clusterId = newClusterId;
    }

    private void resetClusterId() {
        assert (this.lock.isHeldByCurrentThread()) : "Called without holding cluster service lock!";
        this.clusterId = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String addMembershipListener(MembershipListener listener) {
        EventRegistration registration;
        Preconditions.checkNotNull(listener, "listener cannot be null");
        InternalEventService eventService = this.nodeEngine.getEventService();
        if (listener instanceof InitialMembershipListener) {
            this.lock.lock();
            try {
                ((InitialMembershipListener)listener).init(new InitialMembershipEvent(this, this.getMembers()));
                registration = eventService.registerLocalListener(SERVICE_NAME, SERVICE_NAME, listener);
            }
            finally {
                this.lock.unlock();
            }
        } else {
            registration = eventService.registerLocalListener(SERVICE_NAME, SERVICE_NAME, listener);
        }
        return registration.getId();
    }

    @Override
    public boolean removeMembershipListener(String registrationId) {
        Preconditions.checkNotNull(registrationId, "registrationId cannot be null");
        InternalEventService eventService = this.nodeEngine.getEventService();
        return eventService.deregisterListener(SERVICE_NAME, SERVICE_NAME, registrationId);
    }

    @Override
    @SuppressFBWarnings(value={"BC_UNCONFIRMED_CAST"})
    public void dispatchEvent(MembershipEvent event, MembershipListener listener) {
        switch (event.getEventType()) {
            case 1: {
                listener.memberAdded(event);
                break;
            }
            case 2: {
                listener.memberRemoved(event);
                break;
            }
            case 5: {
                MemberAttributeEvent memberAttributeEvent = (MemberAttributeEvent)event;
                listener.memberAttributeChanged(memberAttributeEvent);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unhandled event: " + event);
            }
        }
    }

    private String legacyMemberListString() {
        StringBuilder sb = new StringBuilder("\n\nMembers [");
        Collection<MemberImpl> members = this.getMemberImpls();
        sb.append(members.size());
        sb.append("] {");
        for (Member member : members) {
            sb.append("\n\t").append(member);
        }
        sb.append("\n}\n");
        return sb.toString();
    }

    public String getMemberListString() {
        return this.useLegacyMemberListFormat ? this.legacyMemberListString() : this.membershipManager.memberListString();
    }

    void printMemberList() {
        this.logger.info(this.getMemberListString());
    }

    @Override
    public ClusterState getClusterState() {
        return this.clusterStateManager.getState();
    }

    @Override
    public <T extends TransactionalObject> T createTransactionalObject(String name, Transaction transaction) {
        throw new UnsupportedOperationException("hz:core:clusterService does not support TransactionalObjects!");
    }

    @Override
    public void rollbackTransaction(String transactionId) {
        this.clusterStateManager.rollbackClusterState(transactionId);
    }

    @Override
    public void changeClusterState(ClusterState newState) {
        this.changeClusterState(newState, false);
    }

    private void changeClusterState(ClusterState newState, boolean isTransient) {
        int partitionStateVersion = this.node.getPartitionService().getPartitionStateVersion();
        this.clusterStateManager.changeClusterState(ClusterStateChange.from(newState), this.membershipManager.getMemberMap(), partitionStateVersion, isTransient);
    }

    @Override
    public void changeClusterState(ClusterState newState, TransactionOptions options) {
        this.changeClusterState(newState, options, false);
    }

    private void changeClusterState(ClusterState newState, TransactionOptions options, boolean isTransient) {
        int partitionStateVersion = this.node.getPartitionService().getPartitionStateVersion();
        this.clusterStateManager.changeClusterState(ClusterStateChange.from(newState), this.membershipManager.getMemberMap(), options, partitionStateVersion, isTransient);
    }

    @Override
    public Version getClusterVersion() {
        return this.clusterStateManager.getClusterVersion();
    }

    @Override
    public HotRestartService getHotRestartService() {
        return this.node.getNodeExtension().getHotRestartService();
    }

    @Override
    public void changeClusterVersion(Version version) {
        MemberMap memberMap = this.membershipManager.getMemberMap();
        this.changeClusterVersion(version, memberMap);
    }

    public void changeClusterVersion(Version version, MemberMap memberMap) {
        int partitionStateVersion = this.node.getPartitionService().getPartitionStateVersion();
        this.clusterStateManager.changeClusterState(ClusterStateChange.from(version), memberMap, partitionStateVersion, false);
    }

    @Override
    public void changeClusterVersion(Version version, TransactionOptions options) {
        int partitionStateVersion = this.node.getPartitionService().getPartitionStateVersion();
        this.clusterStateManager.changeClusterState(ClusterStateChange.from(version), this.membershipManager.getMemberMap(), options, partitionStateVersion, false);
    }

    @Override
    public int getMemberListJoinVersion() {
        this.lock.lock();
        try {
            if (!this.isJoined()) {
                throw new IllegalStateException("Member list join version is not available when not joined");
            }
            int joinVersion = this.localMember.getMemberListJoinVersion();
            if (joinVersion == -1) {
                throw new IllegalStateException("Member list join version is not yet available");
            }
            int n = joinVersion;
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public void shutdown() {
        this.shutdownCluster(null);
    }

    @Override
    public void shutdown(TransactionOptions options) {
        this.shutdownCluster(options);
    }

    private void shutdownCluster(TransactionOptions options) {
        if (options == null) {
            this.changeClusterState(ClusterState.PASSIVE, true);
        } else {
            this.changeClusterState(ClusterState.PASSIVE, options, true);
        }
        long timeoutNanos = this.node.getProperties().getNanos(GroupProperty.CLUSTER_SHUTDOWN_TIMEOUT_SECONDS);
        long startNanos = System.nanoTime();
        this.node.getNodeExtension().getInternalHotRestartService().waitPartitionReplicaSyncOnCluster(timeoutNanos, TimeUnit.NANOSECONDS);
        timeoutNanos -= System.nanoTime() - startNanos;
        if (this.node.config.getCPSubsystemConfig().getCPMemberCount() == 0) {
            this.shutdownNodesConcurrently(timeoutNanos);
        } else {
            this.shutdownNodesSerially(timeoutNanos);
        }
    }

    private void shutdownNodesConcurrently(long timeoutNanos) {
        ShutdownNodeOp op = new ShutdownNodeOp();
        Collection<Member> members = this.getMembers(MemberSelectors.NON_LOCAL_MEMBER_SELECTOR);
        long startTime = System.nanoTime();
        this.logger.info("Sending shut down operations to all members...");
        while (System.nanoTime() - startTime < timeoutNanos && !members.isEmpty()) {
            for (Member member : members) {
                this.nodeEngine.getOperationService().send(op, member.getAddress());
            }
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                this.logger.warning("Shutdown sleep interrupted. ", e);
                break;
            }
            members = this.getMembers(MemberSelectors.NON_LOCAL_MEMBER_SELECTOR);
        }
        this.logger.info("Number of other members remaining: " + this.getSize(MemberSelectors.NON_LOCAL_MEMBER_SELECTOR) + ". Shutting down itself.");
        HazelcastInstanceImpl hazelcastInstance = this.node.hazelcastInstance;
        hazelcastInstance.getLifecycleService().shutdown();
    }

    private void shutdownNodesSerially(long timeoutNanos) {
        ShutdownNodeOp op = new ShutdownNodeOp();
        long startTime = System.nanoTime();
        Collection<Member> members = this.getMembers(MemberSelectors.NON_LOCAL_MEMBER_SELECTOR);
        this.logger.info("Sending shut down operations to other members one by one...");
        while (System.nanoTime() - startTime < timeoutNanos && !members.isEmpty()) {
            Member member = members.iterator().next();
            this.nodeEngine.getOperationService().send(op, member.getAddress());
            members = this.getMembers(MemberSelectors.NON_LOCAL_MEMBER_SELECTOR);
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                this.logger.warning("Shutdown sleep interrupted. ", e);
                break;
            }
        }
        this.logger.info("Number of other members remaining: " + this.getSize(MemberSelectors.NON_LOCAL_MEMBER_SELECTOR) + ". Shutting down itself.");
        HazelcastInstanceImpl hazelcastInstance = this.node.hazelcastInstance;
        hazelcastInstance.getLifecycleService().shutdown();
    }

    private void initialClusterState(ClusterState clusterState, Version version) {
        if (this.isJoined()) {
            throw new IllegalStateException("Cannot set initial state after node joined! -> " + (Object)((Object)clusterState));
        }
        this.clusterStateManager.initialClusterState(clusterState, version);
    }

    public MembershipManager getMembershipManager() {
        return this.membershipManager;
    }

    public ClusterStateManager getClusterStateManager() {
        return this.clusterStateManager;
    }

    public ClusterJoinManager getClusterJoinManager() {
        return this.clusterJoinManager;
    }

    public ClusterHeartbeatManager getClusterHeartbeatManager() {
        return this.clusterHeartbeatManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void promoteLocalLiteMember() {
        MemberImpl member = this.getLocalMember();
        if (!member.isLiteMember()) {
            throw new IllegalStateException(member + " is not a lite member!");
        }
        MemberImpl master = this.getMasterMember();
        PromoteLiteMemberOp op = new PromoteLiteMemberOp();
        op.setCallerUuid(member.getUuid());
        InternalCompletableFuture future = this.nodeEngine.getOperationService().invokeOnTarget(SERVICE_NAME, op, master.getAddress());
        MembersView view = (MembersView)future.join();
        this.lock.lock();
        try {
            MemberImpl localMemberInMemberList;
            if (!member.getAddress().equals(master.getAddress())) {
                this.updateMembers(view, master.getAddress(), master.getUuid(), this.getThisUuid());
            }
            if ((localMemberInMemberList = this.membershipManager.getMember(member.getAddress())).isLiteMember()) {
                throw new IllegalStateException("Cannot promote to data member! Previous master was: " + master.getAddress() + ", Current master is: " + this.getMasterAddress());
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    MemberImpl promoteAndGetLocalMember() {
        MemberImpl member = this.getLocalMember();
        assert (member.isLiteMember()) : "Local member is not lite member!";
        assert (this.lock.isHeldByCurrentThread()) : "Called without holding cluster service lock!";
        this.localMember = new MemberImpl.Builder(member.getAddressMap()).version(member.getVersion()).localMember(true).uuid(member.getUuid()).attributes(member.getAttributes()).memberListJoinVersion(member.getMemberListJoinVersion()).instance(this.node.hazelcastInstance).build();
        this.node.loggingService.setThisMember(this.localMember);
        return this.localMember;
    }

    @Override
    public int getMemberListVersion() {
        return this.membershipManager.getMemberListVersion();
    }

    private MemberImpl getMasterMember() {
        MemberImpl master;
        this.lock.lock();
        try {
            Address masterAddress = this.getMasterAddress();
            if (masterAddress == null) {
                throw new IllegalStateException("Master is not known yet!");
            }
            master = this.getMember(masterAddress);
        }
        finally {
            this.lock.unlock();
        }
        return master;
    }

    public String toString() {
        return "ClusterService{address=" + this.getThisAddress() + '}';
    }
}

