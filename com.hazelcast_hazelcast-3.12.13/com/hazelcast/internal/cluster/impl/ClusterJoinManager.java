/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.Member;
import com.hazelcast.hotrestart.InternalHotRestartService;
import com.hazelcast.instance.BuildInfo;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.MemberInfo;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.cluster.impl.ClusterClockImpl;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.ClusterStateManager;
import com.hazelcast.internal.cluster.impl.ConfigCheck;
import com.hazelcast.internal.cluster.impl.ConfigMismatchException;
import com.hazelcast.internal.cluster.impl.JoinMessage;
import com.hazelcast.internal.cluster.impl.JoinRequest;
import com.hazelcast.internal.cluster.impl.MemberMap;
import com.hazelcast.internal.cluster.impl.MembersView;
import com.hazelcast.internal.cluster.impl.MembersViewMetadata;
import com.hazelcast.internal.cluster.impl.SplitBrainJoinMessage;
import com.hazelcast.internal.cluster.impl.operations.AuthenticationFailureOp;
import com.hazelcast.internal.cluster.impl.operations.BeforeJoinCheckFailureOp;
import com.hazelcast.internal.cluster.impl.operations.ConfigMismatchOp;
import com.hazelcast.internal.cluster.impl.operations.FinalizeJoinOp;
import com.hazelcast.internal.cluster.impl.operations.GroupMismatchOp;
import com.hazelcast.internal.cluster.impl.operations.JoinRequestOp;
import com.hazelcast.internal.cluster.impl.operations.MasterResponseOp;
import com.hazelcast.internal.cluster.impl.operations.MembersUpdateOp;
import com.hazelcast.internal.cluster.impl.operations.OnJoinOp;
import com.hazelcast.internal.cluster.impl.operations.WhoisMasterOp;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.PartitionRuntimeState;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.Credentials;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.Clock;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.UuidUtil;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.version.Version;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public class ClusterJoinManager {
    public static final String STALE_JOIN_PREVENTION_DURATION_PROP = "hazelcast.stale.join.prevention.duration.seconds";
    private static final int CLUSTER_OPERATION_RETRY_COUNT = 100;
    private static final int STALE_JOIN_PREVENTION_DURATION_SECONDS = Integer.getInteger("hazelcast.stale.join.prevention.duration.seconds", 30);
    private final ILogger logger;
    private final Node node;
    private final NodeEngineImpl nodeEngine;
    private final ClusterServiceImpl clusterService;
    private final Lock clusterServiceLock;
    private final ClusterClockImpl clusterClock;
    private final ClusterStateManager clusterStateManager;
    private final Map<Address, MemberInfo> joiningMembers = new LinkedHashMap<Address, MemberInfo>();
    private final Map<String, Long> recentlyJoinedMemberUuids = new HashMap<String, Long>();
    private final long maxWaitMillisBeforeJoin;
    private final long waitMillisBeforeJoin;
    private final long staleJoinPreventionDuration;
    private long firstJoinRequest;
    private long timeToStartJoin;
    private volatile boolean joinInProgress;

    ClusterJoinManager(Node node, ClusterServiceImpl clusterService, Lock clusterServiceLock) {
        this.node = node;
        this.clusterService = clusterService;
        this.clusterServiceLock = clusterServiceLock;
        this.nodeEngine = clusterService.getNodeEngine();
        this.logger = node.getLogger(this.getClass());
        this.clusterStateManager = clusterService.getClusterStateManager();
        this.clusterClock = clusterService.getClusterClock();
        this.maxWaitMillisBeforeJoin = node.getProperties().getMillis(GroupProperty.MAX_WAIT_SECONDS_BEFORE_JOIN);
        this.waitMillisBeforeJoin = node.getProperties().getMillis(GroupProperty.WAIT_SECONDS_BEFORE_JOIN);
        this.staleJoinPreventionDuration = TimeUnit.SECONDS.toMillis(STALE_JOIN_PREVENTION_DURATION_SECONDS);
    }

    boolean isJoinInProgress() {
        if (this.joinInProgress) {
            return true;
        }
        this.clusterServiceLock.lock();
        try {
            boolean bl = this.joinInProgress || !this.joiningMembers.isEmpty();
            return bl;
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    boolean isMastershipClaimInProgress() {
        this.clusterServiceLock.lock();
        try {
            boolean bl = this.joinInProgress && this.joiningMembers.isEmpty();
            return bl;
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    public void handleJoinRequest(JoinRequest joinRequest, Connection connection) {
        if (!this.ensureNodeIsReady()) {
            return;
        }
        if (!this.ensureValidConfiguration(joinRequest)) {
            return;
        }
        Address target = joinRequest.getAddress();
        boolean isRequestFromCurrentMaster = target.equals(this.clusterService.getMasterAddress());
        if (!this.clusterService.isMaster() && !isRequestFromCurrentMaster) {
            this.sendMasterAnswer(target);
            return;
        }
        if (this.joinInProgress) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine(String.format("Join or membership claim is in progress, cannot handle join request from %s at the moment", target));
            }
            return;
        }
        this.executeJoinRequest(joinRequest, connection);
    }

    private boolean ensureNodeIsReady() {
        if (this.clusterService.isJoined() && this.node.isRunning()) {
            return true;
        }
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Node is not ready to process join request...");
        }
        return false;
    }

    private boolean ensureValidConfiguration(JoinMessage joinMessage) {
        Address address = joinMessage.getAddress();
        try {
            if (this.isValidJoinMessage(joinMessage)) {
                return true;
            }
            this.logger.warning(String.format("Received an invalid join request from %s, cause: clusters part of different cluster-groups", address));
            this.nodeEngine.getOperationService().send(new GroupMismatchOp(), address);
        }
        catch (ConfigMismatchException e) {
            this.logger.warning(String.format("Received an invalid join request from %s, cause: %s", address, e.getMessage()));
            InternalOperationService operationService = this.nodeEngine.getOperationService();
            operationService.send(new ConfigMismatchOp(e.getMessage()), address);
        }
        return false;
    }

    private boolean isValidJoinMessage(JoinMessage joinMessage) {
        try {
            return this.validateJoinMessage(joinMessage);
        }
        catch (ConfigMismatchException e) {
            throw e;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean validateJoinMessage(JoinMessage joinMessage) throws Exception {
        if (joinMessage.getPacketVersion() != 4) {
            return false;
        }
        try {
            ConfigCheck newMemberConfigCheck = joinMessage.getConfigCheck();
            ConfigCheck clusterConfigCheck = this.node.createConfigCheck();
            return clusterConfigCheck.isCompatible(newMemberConfigCheck);
        }
        catch (Exception e) {
            this.logger.warning(String.format("Invalid join request from %s, cause: %s", joinMessage.getAddress(), e.getMessage()));
            throw e;
        }
    }

    private void executeJoinRequest(JoinRequest joinRequest, Connection connection) {
        this.clusterServiceLock.lock();
        try {
            if (this.checkJoinRequest(joinRequest, connection)) {
                return;
            }
            if (!this.authenticate(joinRequest)) {
                return;
            }
            if (!this.validateJoinRequest(joinRequest, joinRequest.getAddress())) {
                return;
            }
            this.startJoinRequest(joinRequest.toMemberInfo());
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    private boolean checkJoinRequest(JoinRequest joinRequest, Connection connection) {
        String targetUuid;
        Address target;
        if (this.checkIfJoinRequestFromAnExistingMember(joinRequest, connection)) {
            return true;
        }
        InternalHotRestartService hotRestartService = this.node.getNodeExtension().getInternalHotRestartService();
        if (hotRestartService.isMemberExcluded(target = joinRequest.getAddress(), targetUuid = joinRequest.getUuid())) {
            this.logger.fine("cannot join " + target + " because it is excluded in cluster start.");
            hotRestartService.notifyExcludedMember(target);
            return true;
        }
        if (joinRequest.getExcludedMemberUuids().contains(this.clusterService.getThisUuid())) {
            this.logger.warning("cannot join " + target + " since this node is excluded in its list...");
            hotRestartService.handleExcludedMemberUuids(target, joinRequest.getExcludedMemberUuids());
            return true;
        }
        return this.checkClusterStateBeforeJoin(target, targetUuid);
    }

    private boolean checkClusterStateBeforeJoin(Address target, String uuid) {
        ClusterState state = this.clusterStateManager.getState();
        if (state == ClusterState.IN_TRANSITION) {
            this.logger.warning("Cluster state is in transition process. Join is not allowed until transaction is completed -> " + this.clusterStateManager.stateToString());
            return true;
        }
        if (state.isJoinAllowed()) {
            return this.checkRecentlyJoinedMemberUuidBeforeJoin(target, uuid);
        }
        if (this.clusterService.getClusterVersion().isLessThan(Versions.V3_12) && this.node.getNodeExtension().getInternalHotRestartService().isEnabled() && this.clusterService.isMissingMember(target, uuid)) {
            Collection<MemberImpl> missingMembers = this.clusterService.getMembershipManager().getMissingMembers();
            for (MemberImpl member : missingMembers) {
                if (uuid.equals(member.getUuid()) || !target.equals(member.getAddress())) continue;
                MemberImpl joiningMember = new MemberImpl(target, MemberVersion.UNKNOWN, false, uuid);
                this.logger.warning("Address " + target + " was being used by " + member + " before. " + joiningMember + " is not allowed to join with an address which belongs to a known missing member.");
                return true;
            }
            return false;
        }
        if (this.clusterService.isMissingMember(target, uuid)) {
            return false;
        }
        if (this.node.getNodeExtension().isStartCompleted()) {
            String message = "Cluster state either is locked or doesn't allow new members to join -> " + this.clusterStateManager.stateToString();
            this.logger.warning(message);
            InternalOperationService operationService = this.nodeEngine.getOperationService();
            BeforeJoinCheckFailureOp op = new BeforeJoinCheckFailureOp(message);
            operationService.send(op, target);
        } else {
            String message = "Cluster state either is locked or doesn't allow new members to join -> " + this.clusterStateManager.stateToString() + ". Silently ignored join request of " + target + " because start not completed.";
            this.logger.warning(message);
        }
        return true;
    }

    void insertIntoRecentlyJoinedMemberSet(Collection<? extends Member> members) {
        this.cleanupRecentlyJoinedMemberUuids();
        if (this.clusterService.getClusterState().isJoinAllowed()) {
            long localTime = Clock.currentTimeMillis();
            for (Member member : members) {
                this.recentlyJoinedMemberUuids.put(member.getUuid(), localTime);
            }
        }
    }

    private boolean checkRecentlyJoinedMemberUuidBeforeJoin(Address target, String uuid) {
        this.cleanupRecentlyJoinedMemberUuids();
        boolean recentlyJoined = this.recentlyJoinedMemberUuids.containsKey(uuid);
        if (recentlyJoined) {
            this.logger.warning("Cannot allow join request from " + target + ", since it has been already joined with " + uuid);
        }
        return recentlyJoined;
    }

    private void cleanupRecentlyJoinedMemberUuids() {
        long currentTime = Clock.currentTimeMillis();
        Iterator<Long> it = this.recentlyJoinedMemberUuids.values().iterator();
        while (it.hasNext()) {
            long joinTime = it.next();
            if (currentTime - joinTime < this.staleJoinPreventionDuration) continue;
            it.remove();
        }
    }

    private boolean authenticate(JoinRequest joinRequest) {
        if (!this.joiningMembers.containsKey(joinRequest.getAddress())) {
            try {
                this.secureLogin(joinRequest);
            }
            catch (Exception e) {
                ILogger securityLogger = this.node.loggingService.getLogger("com.hazelcast.security");
                this.nodeEngine.getOperationService().send(new AuthenticationFailureOp(), joinRequest.getAddress());
                securityLogger.severe(e);
                return false;
            }
        }
        return true;
    }

    private void secureLogin(JoinRequest joinRequest) {
        if (this.node.securityContext != null) {
            Credentials credentials = joinRequest.getCredentials();
            if (credentials == null) {
                throw new SecurityException("Expecting security credentials, but credentials could not be found in join request");
            }
            try {
                LoginContext loginContext = this.node.securityContext.createMemberLoginContext(credentials);
                loginContext.login();
            }
            catch (LoginException e) {
                throw new SecurityException(String.format("Authentication has failed for %s@%s, cause: %s", credentials.getPrincipal(), credentials.getEndpoint(), e.getMessage()));
            }
        }
    }

    private boolean validateJoinRequest(JoinRequest joinRequest, Address target) {
        if (this.clusterService.isMaster()) {
            try {
                this.node.getNodeExtension().validateJoinRequest(joinRequest);
            }
            catch (Exception e) {
                this.logger.warning(e.getMessage());
                this.nodeEngine.getOperationService().send(new BeforeJoinCheckFailureOp(e.getMessage()), target);
                return false;
            }
        }
        return true;
    }

    private void startJoinRequest(MemberInfo memberInfo) {
        MemberInfo existing;
        long now = Clock.currentTimeMillis();
        if (this.logger.isFineEnabled()) {
            String timeToStart = this.timeToStartJoin > 0L ? ", timeToStart: " + (this.timeToStartJoin - now) : "";
            this.logger.fine(String.format("Handling join from %s, joinInProgress: %b%s", memberInfo.getAddress(), this.joinInProgress, timeToStart));
        }
        if (this.firstJoinRequest == 0L) {
            this.firstJoinRequest = now;
        }
        if ((existing = this.joiningMembers.put(memberInfo.getAddress(), memberInfo)) == null) {
            this.sendMasterAnswer(memberInfo.getAddress());
            if (now - this.firstJoinRequest < this.maxWaitMillisBeforeJoin) {
                this.timeToStartJoin = now + this.waitMillisBeforeJoin;
            }
        } else if (!existing.getUuid().equals(memberInfo.getUuid())) {
            this.logger.warning("Received a new join request from " + memberInfo.getAddress() + " with a new UUID " + memberInfo.getUuid() + ". Previous UUID was " + existing.getUuid());
        }
        if (now >= this.timeToStartJoin) {
            this.startJoin();
        }
    }

    public boolean sendJoinRequest(Address toAddress, boolean withCredentials) {
        if (toAddress == null) {
            toAddress = this.clusterService.getMasterAddress();
        }
        JoinRequestOp joinRequest = new JoinRequestOp(this.node.createJoinRequest(withCredentials));
        return this.nodeEngine.getOperationService().send(joinRequest, toAddress);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean setThisMemberAsMaster() {
        this.clusterServiceLock.lock();
        try {
            if (this.clusterService.isJoined()) {
                this.logger.warning("Cannot set as master because node is already joined!");
                boolean bl = false;
                return bl;
            }
            this.logger.finest("This node is being set as the master");
            Address thisAddress = this.node.getThisAddress();
            MemberVersion version = this.node.getVersion();
            this.clusterService.setMasterAddress(thisAddress);
            if (this.clusterService.getClusterVersion().isUnknown()) {
                this.clusterService.getClusterStateManager().setClusterVersion(version.asVersion());
            }
            this.clusterService.getClusterClock().setClusterStartTime(Clock.currentTimeMillis());
            this.clusterService.setClusterId(UuidUtil.createClusterUuid());
            this.clusterService.getMembershipManager().setLocalMemberListJoinVersion(1);
            this.clusterService.setJoined(true);
            boolean bl = true;
            return bl;
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handleMasterResponse(Address masterAddress, Address callerAddress) {
        this.clusterServiceLock.lock();
        try {
            if (this.logger.isFineEnabled()) {
                this.logger.fine(String.format("Handling master response %s from %s", masterAddress, callerAddress));
            }
            if (this.clusterService.isJoined()) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine(String.format("Ignoring master response %s from %s, this node is already joined", masterAddress, callerAddress));
                }
                return;
            }
            if (this.node.getThisAddress().equals(masterAddress)) {
                this.logger.warning("Received my address as master address from " + callerAddress);
                return;
            }
            Address currentMaster = this.clusterService.getMasterAddress();
            if (currentMaster == null || currentMaster.equals(masterAddress)) {
                this.setMasterAndJoin(masterAddress);
                return;
            }
            if (currentMaster.equals(callerAddress)) {
                this.logger.warning(String.format("Setting master to %s since %s says it is not master anymore", masterAddress, currentMaster));
                this.setMasterAndJoin(masterAddress);
                return;
            }
            Object conn = this.node.getEndpointManager(EndpointQualifier.MEMBER).getConnection(currentMaster);
            if (conn != null && conn.isAlive()) {
                this.logger.info(String.format("Ignoring master response %s from %s since this node has an active master %s", masterAddress, callerAddress, currentMaster));
                this.sendJoinRequest(currentMaster, true);
            } else {
                this.logger.warning(String.format("Ambiguous master response! Received master response %s from %s. This node has a master %s, but does not have an active connection to it. Master field will be unset now.", masterAddress, callerAddress, currentMaster));
                this.clusterService.setMasterAddress(null);
            }
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    private void setMasterAndJoin(Address masterAddress) {
        this.clusterService.setMasterAddress(masterAddress);
        this.node.getEndpointManager(EndpointQualifier.MEMBER).getOrConnect(masterAddress);
        if (!this.sendJoinRequest(masterAddress, true)) {
            this.logger.warning("Could not create connection to possible master " + masterAddress);
        }
    }

    public boolean sendMasterQuestion(Address toAddress) {
        Preconditions.checkNotNull(toAddress, "No endpoint is specified!");
        BuildInfo buildInfo = this.node.getBuildInfo();
        Address thisAddress = this.node.getThisAddress();
        JoinMessage joinMessage = new JoinMessage(4, buildInfo.getBuildNumber(), this.node.getVersion(), thisAddress, this.clusterService.getThisUuid(), this.node.isLiteMember(), this.node.createConfigCheck());
        return this.nodeEngine.getOperationService().send(new WhoisMasterOp(joinMessage), toAddress);
    }

    public void answerWhoisMasterQuestion(JoinMessage joinMessage, Connection connection) {
        if (!this.ensureValidConfiguration(joinMessage)) {
            return;
        }
        if (this.clusterService.isJoined()) {
            if (!this.checkIfJoinRequestFromAnExistingMember(joinMessage, connection)) {
                this.sendMasterAnswer(joinMessage.getAddress());
            }
        } else if (this.logger.isFineEnabled()) {
            this.logger.fine(String.format("Received a master question from %s, but this node is not master itself or doesn't have a master yet!", joinMessage.getAddress()));
        }
    }

    private void sendMasterAnswer(Address target) {
        Address masterAddress = this.clusterService.getMasterAddress();
        if (masterAddress == null) {
            this.logger.info(String.format("Cannot send master answer to %s since master node is not known yet", target));
            return;
        }
        if (masterAddress.equals(this.node.getThisAddress()) && this.node.getNodeExtension().getInternalHotRestartService().isMemberExcluded(masterAddress, this.clusterService.getThisUuid())) {
            this.logger.info("Cannot send master answer because " + target + " should not join to this master node.");
            return;
        }
        if (masterAddress.equals(target)) {
            this.logger.fine("Cannot send master answer to " + target + " since it is the known master");
            return;
        }
        MasterResponseOp op = new MasterResponseOp(masterAddress);
        this.nodeEngine.getOperationService().send(op, target);
    }

    private boolean checkIfJoinRequestFromAnExistingMember(JoinMessage joinMessage, Connection connection) {
        Address target = joinMessage.getAddress();
        MemberImpl member = this.clusterService.getMember(target);
        if (member == null) {
            return this.checkIfUsingAnExistingMemberUuid(joinMessage);
        }
        if (joinMessage.getUuid().equals(member.getUuid())) {
            this.sendMasterAnswer(target);
            if (this.clusterService.isMaster() && !this.isMastershipClaimInProgress()) {
                if (this.logger.isFineEnabled()) {
                    this.logger.fine(String.format("Ignoring join request, member already exists: %s", joinMessage));
                }
                OnJoinOp preJoinOp = this.preparePreJoinOps();
                OnJoinOp postJoinOp = this.preparePostJoinOp();
                PartitionRuntimeState partitionRuntimeState = this.node.getPartitionService().createPartitionState();
                FinalizeJoinOp op = new FinalizeJoinOp(member.getUuid(), this.clusterService.getMembershipManager().getMembersView(), preJoinOp, postJoinOp, this.clusterClock.getClusterTime(), this.clusterService.getClusterId(), this.clusterClock.getClusterStartTime(), this.clusterStateManager.getState(), this.clusterService.getClusterVersion(), partitionRuntimeState);
                op.setCallerUuid(this.clusterService.getThisUuid());
                this.invokeClusterOp(op, target);
            }
            return true;
        }
        if (this.clusterService.isMaster() || target.equals(this.clusterService.getMasterAddress())) {
            String msg = String.format("New join request has been received from an existing endpoint %s. Removing old member and processing join request...", member);
            this.logger.warning(msg);
            this.clusterService.suspectMember(member, msg, false);
            Object existing = this.node.getEndpointManager(EndpointQualifier.MEMBER).getConnection(target);
            if (existing != connection) {
                if (existing != null) {
                    existing.close(msg, null);
                }
                this.node.getEndpointManager(EndpointQualifier.MEMBER).registerConnection(target, connection);
            }
        }
        return true;
    }

    private boolean checkIfUsingAnExistingMemberUuid(JoinMessage joinMessage) {
        MemberImpl member = this.clusterService.getMember(joinMessage.getUuid());
        Address target = joinMessage.getAddress();
        if (member != null && !member.getAddress().equals(joinMessage.getAddress())) {
            if (this.clusterService.isMaster() && !this.isMastershipClaimInProgress()) {
                String message = "There's already an existing member " + member + " with the same UUID. " + target + " is not allowed to join.";
                this.logger.warning(message);
            } else {
                this.sendMasterAnswer(target);
            }
            return true;
        }
        return false;
    }

    void setMastershipClaimInProgress() {
        this.clusterServiceLock.lock();
        try {
            this.joinInProgress = true;
            this.joiningMembers.clear();
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void startJoin() {
        this.logger.fine("Starting join...");
        this.clusterServiceLock.lock();
        try {
            InternalPartitionService partitionService = this.node.getPartitionService();
            try {
                this.joinInProgress = true;
                partitionService.pauseMigration();
                MemberMap memberMap = this.clusterService.getMembershipManager().getMemberMap();
                MembersView newMembersView = MembersView.cloneAdding(memberMap.toMembersView(), this.joiningMembers.values());
                long time = this.clusterClock.getClusterTime();
                String thisUuid = this.clusterService.getThisUuid();
                if (!this.clusterService.updateMembers(newMembersView, this.node.getThisAddress(), thisUuid, thisUuid)) {
                    return;
                }
                OnJoinOp preJoinOp = this.preparePreJoinOps();
                OnJoinOp postJoinOp = this.preparePostJoinOp();
                PartitionRuntimeState partitionRuntimeState = partitionService.createPartitionState();
                for (MemberInfo memberInfo : this.joiningMembers.values()) {
                    long startTime = this.clusterClock.getClusterStartTime();
                    FinalizeJoinOp op = new FinalizeJoinOp(memberInfo.getUuid(), newMembersView, preJoinOp, postJoinOp, time, this.clusterService.getClusterId(), startTime, this.clusterStateManager.getState(), this.clusterService.getClusterVersion(), partitionRuntimeState);
                    op.setCallerUuid(thisUuid);
                    this.invokeClusterOp(op, memberInfo.getAddress());
                }
                for (MemberImpl memberImpl : memberMap.getMembers()) {
                    if (memberImpl.localMember() || this.joiningMembers.containsKey(memberImpl.getAddress())) continue;
                    MembersUpdateOp op = new MembersUpdateOp(memberImpl.getUuid(), newMembersView, time, partitionRuntimeState, true);
                    op.setCallerUuid(thisUuid);
                    this.invokeClusterOp(op, memberImpl.getAddress());
                }
            }
            finally {
                this.reset();
                partitionService.resumeMigration();
            }
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    private OnJoinOp preparePostJoinOp() {
        Operation[] postJoinOps = this.nodeEngine.getPostJoinOperations();
        boolean createPostJoinOperation = postJoinOps != null && postJoinOps.length > 0;
        return createPostJoinOperation ? new OnJoinOp(postJoinOps) : null;
    }

    private OnJoinOp preparePreJoinOps() {
        Operation[] preJoinOps = this.nodeEngine.getPreJoinOperations();
        return preJoinOps != null && preJoinOps.length > 0 ? new OnJoinOp(preJoinOps) : null;
    }

    private Future invokeClusterOp(Operation op, Address target) {
        return this.nodeEngine.getOperationService().createInvocationBuilder("hz:core:clusterService", op, target).setTryCount(100).invoke();
    }

    public SplitBrainJoinMessage.SplitBrainMergeCheckResult shouldMerge(SplitBrainJoinMessage joinMessage) {
        int currentDataMemberCount;
        if (joinMessage == null) {
            return SplitBrainJoinMessage.SplitBrainMergeCheckResult.CANNOT_MERGE;
        }
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Checking if we should merge to: " + joinMessage);
        }
        if (!this.checkValidSplitBrainJoinMessage(joinMessage)) {
            return SplitBrainJoinMessage.SplitBrainMergeCheckResult.CANNOT_MERGE;
        }
        if (!this.checkCompatibleSplitBrainJoinMessage(joinMessage)) {
            return SplitBrainJoinMessage.SplitBrainMergeCheckResult.CANNOT_MERGE;
        }
        if (!this.checkMergeTargetIsNotMember(joinMessage)) {
            return SplitBrainJoinMessage.SplitBrainMergeCheckResult.CANNOT_MERGE;
        }
        if (!this.checkClusterStateAllowsJoinBeforeMerge(joinMessage)) {
            return SplitBrainJoinMessage.SplitBrainMergeCheckResult.CANNOT_MERGE;
        }
        if (!this.checkMembershipIntersectionSetEmpty(joinMessage)) {
            return SplitBrainJoinMessage.SplitBrainMergeCheckResult.CANNOT_MERGE;
        }
        int targetDataMemberCount = joinMessage.getDataMemberCount();
        if (targetDataMemberCount > (currentDataMemberCount = this.clusterService.getSize(MemberSelectors.DATA_MEMBER_SELECTOR))) {
            this.logger.info("We should merge to " + joinMessage.getAddress() + " because their data member count is bigger than ours [" + targetDataMemberCount + " > " + currentDataMemberCount + ']');
            return SplitBrainJoinMessage.SplitBrainMergeCheckResult.LOCAL_NODE_SHOULD_MERGE;
        }
        if (targetDataMemberCount < currentDataMemberCount) {
            this.logger.info(joinMessage.getAddress() + " should merge to us because our data member count is bigger than theirs [" + currentDataMemberCount + " > " + targetDataMemberCount + ']');
            return SplitBrainJoinMessage.SplitBrainMergeCheckResult.REMOTE_NODE_SHOULD_MERGE;
        }
        if (this.shouldMergeTo(this.node.getThisAddress(), joinMessage.getAddress())) {
            this.logger.info("We should merge to " + joinMessage.getAddress() + ", both have the same data member count: " + currentDataMemberCount);
            return SplitBrainJoinMessage.SplitBrainMergeCheckResult.LOCAL_NODE_SHOULD_MERGE;
        }
        this.logger.info(joinMessage.getAddress() + " should merge to us , both have the same data member count: " + currentDataMemberCount);
        return SplitBrainJoinMessage.SplitBrainMergeCheckResult.REMOTE_NODE_SHOULD_MERGE;
    }

    private boolean checkValidSplitBrainJoinMessage(SplitBrainJoinMessage joinMessage) {
        try {
            if (!this.validateJoinMessage(joinMessage)) {
                this.logger.fine("Cannot process split brain merge message from " + joinMessage.getAddress() + ", since join-message could not be validated.");
                return false;
            }
        }
        catch (Exception e) {
            this.logger.fine("failure during validating join message", e);
            return false;
        }
        return true;
    }

    private boolean checkCompatibleSplitBrainJoinMessage(SplitBrainJoinMessage joinMessage) {
        Version clusterVersion = this.clusterService.getClusterVersion();
        if (!clusterVersion.isEqualTo(joinMessage.getClusterVersion())) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Should not merge to " + joinMessage.getAddress() + " because other cluster version is " + joinMessage.getClusterVersion() + " while this cluster version is " + clusterVersion);
            }
            return false;
        }
        return true;
    }

    private boolean checkMergeTargetIsNotMember(SplitBrainJoinMessage joinMessage) {
        if (this.clusterService.getMember(joinMessage.getAddress()) != null) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Should not merge to " + joinMessage.getAddress() + ", because it is already member of this cluster.");
            }
            return false;
        }
        return true;
    }

    private boolean checkClusterStateAllowsJoinBeforeMerge(SplitBrainJoinMessage joinMessage) {
        ClusterState clusterState = this.clusterService.getClusterState();
        if (!clusterState.isJoinAllowed()) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Should not merge to " + joinMessage.getAddress() + ", because this cluster is in " + (Object)((Object)clusterState) + " state.");
            }
            return false;
        }
        return true;
    }

    private boolean checkMembershipIntersectionSetEmpty(SplitBrainJoinMessage joinMessage) {
        Collection<Address> targetMemberAddresses = joinMessage.getMemberAddresses();
        Address joinMessageAddress = joinMessage.getAddress();
        if (targetMemberAddresses.contains(this.node.getThisAddress())) {
            MembersViewMetadata membersViewMetadata = new MembersViewMetadata(joinMessageAddress, joinMessage.getUuid(), joinMessageAddress, joinMessage.getMemberListVersion());
            this.clusterService.sendExplicitSuspicion(membersViewMetadata);
            this.logger.info(this.node.getThisAddress() + " CANNOT merge to " + joinMessageAddress + ", because it thinks this-node as its member.");
            return false;
        }
        for (Address address : this.clusterService.getMemberAddresses()) {
            if (!targetMemberAddresses.contains(address)) continue;
            this.logger.info(this.node.getThisAddress() + " CANNOT merge to " + joinMessageAddress + ", because it thinks " + address + " is its member. But " + address + " is member of this cluster.");
            return false;
        }
        return true;
    }

    private boolean shouldMergeTo(Address thisAddress, Address targetAddress) {
        String targetAddressStr;
        String thisAddressStr = "[" + thisAddress.getHost() + "]:" + thisAddress.getPort();
        if (thisAddressStr.equals(targetAddressStr = "[" + targetAddress.getHost() + "]:" + targetAddress.getPort())) {
            throw new IllegalArgumentException("Addresses should be different! This: " + thisAddress + ", Target: " + targetAddress);
        }
        int result = thisAddressStr.compareTo(targetAddressStr);
        return result > 0;
    }

    void reset() {
        this.clusterServiceLock.lock();
        try {
            this.joinInProgress = false;
            this.joiningMembers.clear();
            this.timeToStartJoin = Clock.currentTimeMillis() + this.waitMillisBeforeJoin;
            this.firstJoinRequest = 0L;
        }
        finally {
            this.clusterServiceLock.unlock();
        }
    }

    void removeJoin(Address address) {
        this.joiningMembers.remove(address);
    }
}

