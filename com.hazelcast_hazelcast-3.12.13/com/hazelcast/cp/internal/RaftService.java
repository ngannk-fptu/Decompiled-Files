/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.config.cp.RaftAlgorithmConfig;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.Member;
import com.hazelcast.cp.CPGroup;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.CPMember;
import com.hazelcast.cp.CPSubsystemManagementService;
import com.hazelcast.cp.exception.CPGroupDestroyedException;
import com.hazelcast.cp.internal.CPGroupInfo;
import com.hazelcast.cp.internal.CPMemberInfo;
import com.hazelcast.cp.internal.MetadataRaftGroupManager;
import com.hazelcast.cp.internal.MetadataRaftGroupSnapshot;
import com.hazelcast.cp.internal.NodeEngineRaftIntegration;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftGroupMembershipManager;
import com.hazelcast.cp.internal.RaftInvocationManager;
import com.hazelcast.cp.internal.RaftNodeLifecycleAwareService;
import com.hazelcast.cp.internal.datastructures.spi.RaftManagedService;
import com.hazelcast.cp.internal.datastructures.spi.RaftRemoteService;
import com.hazelcast.cp.internal.exception.CannotRemoveCPMemberException;
import com.hazelcast.cp.internal.operation.RestartCPMemberOp;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.cp.internal.raft.SnapshotAwareService;
import com.hazelcast.cp.internal.raft.impl.RaftNode;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftNodeStatus;
import com.hazelcast.cp.internal.raft.impl.dto.AppendFailureResponse;
import com.hazelcast.cp.internal.raft.impl.dto.AppendRequest;
import com.hazelcast.cp.internal.raft.impl.dto.AppendSuccessResponse;
import com.hazelcast.cp.internal.raft.impl.dto.InstallSnapshot;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteRequest;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteResponse;
import com.hazelcast.cp.internal.raft.impl.dto.VoteRequest;
import com.hazelcast.cp.internal.raft.impl.dto.VoteResponse;
import com.hazelcast.cp.internal.raftop.GetInitialRaftGroupMembersIfCurrentGroupMemberOp;
import com.hazelcast.cp.internal.raftop.metadata.AddCPMemberOp;
import com.hazelcast.cp.internal.raftop.metadata.ForceDestroyRaftGroupOp;
import com.hazelcast.cp.internal.raftop.metadata.GetActiveCPMembersOp;
import com.hazelcast.cp.internal.raftop.metadata.GetActiveRaftGroupByNameOp;
import com.hazelcast.cp.internal.raftop.metadata.GetActiveRaftGroupIdsOp;
import com.hazelcast.cp.internal.raftop.metadata.GetRaftGroupIdsOp;
import com.hazelcast.cp.internal.raftop.metadata.GetRaftGroupOp;
import com.hazelcast.cp.internal.raftop.metadata.RaftServicePreJoinOp;
import com.hazelcast.cp.internal.raftop.metadata.RemoveCPMemberOp;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.internal.util.SimpleCompletableFuture;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.GracefulShutdownAwareService;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.MemberAttributeServiceEvent;
import com.hazelcast.spi.MembershipAwareService;
import com.hazelcast.spi.MembershipServiceEvent;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PreJoinAwareService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.servicemanager.ServiceInfo;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.UuidUtil;
import com.hazelcast.util.executor.ManagedExecutorService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RaftService
implements ManagedService,
SnapshotAwareService<MetadataRaftGroupSnapshot>,
GracefulShutdownAwareService,
MembershipAwareService,
CPSubsystemManagementService,
PreJoinAwareService,
RaftNodeLifecycleAwareService {
    public static final String SERVICE_NAME = "hz:core:raft";
    private static final long REMOVE_MISSING_MEMBER_TASK_PERIOD_SECONDS = 1L;
    private static final int AWAIT_DISCOVERY_STEP_MILLIS = 10;
    private static final int METADATA_LOG_CAPACITY = 1000000;
    private final ConcurrentMap<CPGroupId, RaftNode> nodes = new ConcurrentHashMap<CPGroupId, RaftNode>();
    private final NodeEngineImpl nodeEngine;
    private final ILogger logger;
    private final Set<CPGroupId> destroyedGroupIds = Collections.newSetFromMap(new ConcurrentHashMap());
    private final Set<CPGroupId> steppedDownGroupIds = Collections.newSetFromMap(new ConcurrentHashMap());
    private final CPSubsystemConfig config;
    private final RaftInvocationManager invocationManager;
    private final MetadataRaftGroupManager metadataGroupManager;
    private final ConcurrentMap<CPMemberInfo, Long> missingMembers = new ConcurrentHashMap<CPMemberInfo, Long>();

    public RaftService(NodeEngine nodeEngine) {
        this.nodeEngine = (NodeEngineImpl)nodeEngine;
        this.logger = nodeEngine.getLogger(this.getClass());
        CPSubsystemConfig cpSubsystemConfig = nodeEngine.getConfig().getCPSubsystemConfig();
        this.config = cpSubsystemConfig != null ? new CPSubsystemConfig(cpSubsystemConfig) : new CPSubsystemConfig();
        ConfigValidator.checkCPSubsystemConfig(this.config);
        this.metadataGroupManager = new MetadataRaftGroupManager(nodeEngine, this, this.config);
        this.invocationManager = new RaftInvocationManager(nodeEngine, this);
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        if (!this.metadataGroupManager.init()) {
            return;
        }
        if (this.config.getMissingCPMemberAutoRemovalSeconds() > 0) {
            nodeEngine.getExecutionService().scheduleWithRepetition(new AutoRemoveMissingCPMemberTask(), 1L, 1L, TimeUnit.SECONDS);
        }
    }

    @Override
    public void reset() {
        this.missingMembers.clear();
    }

    @Override
    public void shutdown(boolean terminate) {
    }

    @Override
    public MetadataRaftGroupSnapshot takeSnapshot(CPGroupId groupId, long commitIndex) {
        return this.metadataGroupManager.takeSnapshot(groupId, commitIndex);
    }

    @Override
    public void restoreSnapshot(CPGroupId groupId, long commitIndex, MetadataRaftGroupSnapshot snapshot) {
        this.metadataGroupManager.restoreSnapshot(groupId, commitIndex, snapshot);
    }

    public ICompletableFuture<Collection<CPGroupId>> getAllCPGroupIds() {
        return this.invocationManager.invoke(this.getMetadataGroupId(), new GetRaftGroupIdsOp());
    }

    @Override
    public ICompletableFuture<Collection<CPGroupId>> getCPGroupIds() {
        return this.invocationManager.invoke(this.getMetadataGroupId(), new GetActiveRaftGroupIdsOp());
    }

    public ICompletableFuture<CPGroup> getCPGroup(CPGroupId groupId) {
        return this.invocationManager.invoke(this.getMetadataGroupId(), new GetRaftGroupOp(groupId));
    }

    @Override
    public ICompletableFuture<CPGroup> getCPGroup(String name) {
        return this.invocationManager.invoke(this.getMetadataGroupId(), new GetActiveRaftGroupByNameOp(name));
    }

    @Override
    public ICompletableFuture<Void> restart() {
        Preconditions.checkState(this.config.getCPMemberCount() > 0, "CP subsystem is not enabled!");
        final SimpleCompletableFuture<Void> future = this.newCompletableFuture();
        ClusterService clusterService = this.nodeEngine.getClusterService();
        final Collection<Member> members = clusterService.getMembers(MemberSelectors.NON_LOCAL_MEMBER_SELECTOR);
        if (!clusterService.isMaster()) {
            return this.complete(future, new IllegalStateException("Only master can restart CP subsystem!"));
        }
        if (this.config.getCPMemberCount() > members.size() + 1) {
            return this.complete(future, new IllegalStateException("Not enough cluster members to restart CP subsystem! Required: " + this.config.getCPMemberCount() + ", available: " + (members.size() + 1)));
        }
        ExecutionCallback<Void> callback = new ExecutionCallback<Void>(){
            final AtomicInteger latch;
            volatile Throwable failure;
            {
                this.latch = new AtomicInteger(members.size());
            }

            @Override
            public void onResponse(Void response) {
                if (this.latch.decrementAndGet() == 0) {
                    if (this.failure == null) {
                        future.setResult(response);
                    } else {
                        RaftService.this.complete(future, this.failure);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                this.failure = t;
                if (this.latch.decrementAndGet() == 0) {
                    RaftService.this.complete(future, t);
                }
            }
        };
        long seed = this.newSeed();
        this.logger.warning("Restarting CP subsystem with groupId seed: " + seed);
        this.restartLocal(seed);
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        for (Member member : members) {
            RestartCPMemberOp op = new RestartCPMemberOp(seed);
            operationService.invokeOnTarget(SERVICE_NAME, op, member.getAddress()).andThen(callback);
        }
        return future;
    }

    private long newSeed() {
        long seed;
        long currentSeed = this.metadataGroupManager.getGroupIdSeed();
        for (seed = Clock.currentTimeMillis(); seed <= currentSeed; ++seed) {
        }
        return seed;
    }

    public void restartLocal(long seed) {
        if (seed == 0L) {
            throw new IllegalArgumentException("Seed cannot be zero!");
        }
        if (seed == this.metadataGroupManager.getGroupIdSeed()) {
            this.logger.severe("Ignoring restart request. Current groupId seed is already equal to " + seed);
            return;
        }
        this.resetLocalRaftState();
        this.metadataGroupManager.restart(seed);
        this.logger.info("CP state is reset with groupId seed: " + seed);
    }

    private void resetLocalRaftState() {
        for (ServiceInfo serviceInfo : this.nodeEngine.getServiceInfos(RaftRemoteService.class)) {
            if (!(serviceInfo.getService() instanceof RaftManagedService)) continue;
            ((RaftManagedService)serviceInfo.getService()).onCPSubsystemRestart();
        }
        for (RaftNode node : this.nodes.values()) {
            node.forceSetTerminatedStatus();
        }
        this.destroyedGroupIds.addAll(this.nodes.keySet());
        this.nodes.clear();
        this.missingMembers.clear();
        this.invocationManager.reset();
    }

    @Override
    public ICompletableFuture<Void> promoteToCPMember() {
        final SimpleCompletableFuture<Void> future = this.newCompletableFuture();
        if (!this.metadataGroupManager.isDiscoveryCompleted()) {
            return this.complete(future, new IllegalStateException("CP subsystem discovery is not completed yet!"));
        }
        if (this.nodeEngine.getLocalMember().isLiteMember()) {
            return this.complete(future, new IllegalStateException("Lite members cannot be promoted to CP member!"));
        }
        if (this.getLocalCPMember() != null) {
            future.setResult(null);
            return future;
        }
        MemberImpl localMember = this.nodeEngine.getLocalMember();
        final CPMemberInfo member = new CPMemberInfo(UuidUtil.newUnsecureUUID(), localMember.getAddress());
        this.logger.info("Adding new CP member: " + member);
        this.invocationManager.invoke(this.getMetadataGroupId(), new AddCPMemberOp(member)).andThen(new ExecutionCallback<Object>(){

            @Override
            public void onResponse(Object response) {
                RaftService.this.metadataGroupManager.initPromotedCPMember(member);
                future.setResult(response);
            }

            @Override
            public void onFailure(Throwable t) {
                RaftService.this.complete(future, t);
            }
        });
        return future;
    }

    private <T> SimpleCompletableFuture<T> newCompletableFuture() {
        ManagedExecutorService executor = this.nodeEngine.getExecutionService().getExecutor("hz:system");
        return new SimpleCompletableFuture(executor, this.logger);
    }

    @Override
    public ICompletableFuture<Void> removeCPMember(final String cpMemberUuid) {
        final ClusterService clusterService = this.nodeEngine.getClusterService();
        final SimpleCompletableFuture<Void> future = this.newCompletableFuture();
        final ExecutionCallback<Void> removeMemberCallback = new ExecutionCallback<Void>(){

            @Override
            public void onResponse(Void response) {
                future.setResult(response);
            }

            @Override
            public void onFailure(Throwable t) {
                if (t instanceof CannotRemoveCPMemberException) {
                    t = new IllegalStateException(t.getMessage());
                }
                RaftService.this.complete(future, t);
            }
        };
        this.invocationManager.invoke(this.getMetadataGroupId(), new GetActiveCPMembersOp()).andThen(new ExecutionCallback<Collection<CPMember>>(){

            @Override
            public void onResponse(Collection<CPMember> cpMembers) {
                CPMemberInfo cpMemberToRemove = null;
                for (CPMember cpMember : cpMembers) {
                    if (!cpMember.getUuid().equals(cpMemberUuid)) continue;
                    cpMemberToRemove = (CPMemberInfo)cpMember;
                    break;
                }
                if (cpMemberToRemove == null) {
                    RaftService.this.complete(future, new IllegalArgumentException("No CPMember found with uuid: " + cpMemberUuid));
                    return;
                }
                MemberImpl member = clusterService.getMember(cpMemberToRemove.getAddress());
                if (member != null) {
                    RaftService.this.logger.warning("Only unreachable/crashed CP members should be removed. " + member + " is alive but " + cpMemberToRemove + " with the same address is being removed.");
                }
                RaftService.this.invokeTriggerRemoveMember(cpMemberToRemove).andThen(removeMemberCallback);
            }

            @Override
            public void onFailure(Throwable t) {
                RaftService.this.complete(future, t);
            }
        });
        return future;
    }

    @Override
    public ICompletableFuture<Void> forceDestroyCPGroup(String groupName) {
        return this.invocationManager.invoke(this.getMetadataGroupId(), new ForceDestroyRaftGroupOp(groupName));
    }

    @Override
    public ICompletableFuture<Collection<CPMember>> getCPMembers() {
        return this.invocationManager.invoke(this.getMetadataGroupId(), new GetActiveCPMembersOp());
    }

    @Override
    public boolean isDiscoveryCompleted() {
        return this.metadataGroupManager.isDiscoveryCompleted();
    }

    @Override
    public boolean awaitUntilDiscoveryCompleted(long timeout, TimeUnit timeUnit) throws InterruptedException {
        long sleepMillis;
        for (long timeoutMillis = timeUnit.toMillis(timeout); timeoutMillis > 0L && !this.metadataGroupManager.isDiscoveryCompleted(); timeoutMillis -= sleepMillis) {
            sleepMillis = Math.min(10L, timeoutMillis);
            Thread.sleep(sleepMillis);
        }
        return this.metadataGroupManager.isDiscoveryCompleted();
    }

    @Override
    public boolean onShutdown(long timeout, TimeUnit unit) {
        CPMemberInfo localMember = this.getLocalCPMember();
        if (localMember == null) {
            return true;
        }
        this.logger.fine("Triggering remove member procedure for " + localMember);
        if (this.ensureCPMemberRemoved(localMember, unit.toNanos(timeout))) {
            return true;
        }
        this.logger.fine("Remove member procedure NOT completed for " + localMember + " in " + unit.toMillis(timeout) + " ms.");
        return false;
    }

    private boolean ensureCPMemberRemoved(CPMemberInfo member, long remainingTimeNanos) {
        while (remainingTimeNanos > 0L) {
            long start = System.nanoTime();
            try {
                if (this.metadataGroupManager.getActiveMembers().size() == 1) {
                    this.logger.warning("I am one of the last 2 CP members...");
                    return true;
                }
                this.invokeTriggerRemoveMember(member).get();
                this.logger.fine(member + " is marked as being removed.");
                break;
            }
            catch (ExecutionException e) {
                if (!(e.getCause() instanceof CannotRemoveCPMemberException)) {
                    throw ExceptionUtil.rethrow(e);
                }
                if ((remainingTimeNanos -= System.nanoTime() - start) <= 0L) {
                    throw new IllegalStateException(e.getMessage());
                }
                try {
                    Thread.sleep(RaftGroupMembershipManager.MANAGEMENT_TASK_PERIOD_IN_MILLIS);
                }
                catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            catch (Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
        return true;
    }

    @Override
    public Operation getPreJoinOperation() {
        if (this.nodeEngine.getClusterService().getClusterVersion().isLessThan(Versions.V3_12)) {
            return null;
        }
        if (this.config.getCPMemberCount() == 0) {
            return null;
        }
        boolean master = this.nodeEngine.getClusterService().isMaster();
        boolean discoveryCompleted = this.metadataGroupManager.isDiscoveryCompleted();
        RaftGroupId metadataGroupId = this.metadataGroupManager.getMetadataGroupId();
        return master ? new RaftServicePreJoinOp(discoveryCompleted, metadataGroupId) : null;
    }

    @Override
    public void memberAdded(MembershipServiceEvent event) {
        this.metadataGroupManager.broadcastActiveCPMembers();
        this.updateMissingMembers();
    }

    @Override
    public void memberRemoved(MembershipServiceEvent event) {
        this.updateMissingMembers();
    }

    @Override
    public void memberAttributeChanged(MemberAttributeServiceEvent event) {
    }

    void updateMissingMembers() {
        if (this.config.getMissingCPMemberAutoRemovalSeconds() == 0 || !this.metadataGroupManager.isDiscoveryCompleted()) {
            return;
        }
        Collection<CPMemberInfo> activeMembers = this.metadataGroupManager.getActiveMembers();
        this.missingMembers.keySet().retainAll(activeMembers);
        ClusterService clusterService = this.nodeEngine.getClusterService();
        for (CPMemberInfo cpMember : activeMembers) {
            if (clusterService.getMember(cpMember.getAddress()) == null) {
                if (this.missingMembers.putIfAbsent(cpMember, Clock.currentTimeMillis()) != null) continue;
                this.logger.warning(cpMember + " is not present in the cluster. It will be auto-removed after " + this.config.getMissingCPMemberAutoRemovalSeconds() + " seconds.");
                continue;
            }
            if (this.missingMembers.remove(cpMember) == null) continue;
            this.logger.info(cpMember + " is removed from the missing members list as it is in the cluster.");
        }
    }

    Collection<CPMemberInfo> getMissingMembers() {
        return Collections.unmodifiableSet(this.missingMembers.keySet());
    }

    public Collection<CPGroupId> getCPGroupIdsLocally() {
        return this.metadataGroupManager.getGroupIds();
    }

    public CPGroupInfo getCPGroupLocally(CPGroupId groupId) {
        return this.metadataGroupManager.getGroup(groupId);
    }

    public MetadataRaftGroupManager getMetadataGroupManager() {
        return this.metadataGroupManager;
    }

    public RaftInvocationManager getInvocationManager() {
        return this.invocationManager;
    }

    public void handlePreVoteRequest(CPGroupId groupId, PreVoteRequest request, CPMember target) {
        RaftNode node = this.getOrInitRaftNodeIfTargetLocalCPMember(groupId, request, target);
        if (node != null) {
            node.handlePreVoteRequest(request);
        }
    }

    public void handlePreVoteResponse(CPGroupId groupId, PreVoteResponse response, CPMember target) {
        RaftNode node = this.getOrInitRaftNodeIfTargetLocalCPMember(groupId, response, target);
        if (node != null) {
            node.handlePreVoteResponse(response);
        }
    }

    public void handleVoteRequest(CPGroupId groupId, VoteRequest request, CPMember target) {
        RaftNode node = this.getOrInitRaftNodeIfTargetLocalCPMember(groupId, request, target);
        if (node != null) {
            node.handleVoteRequest(request);
        }
    }

    public void handleVoteResponse(CPGroupId groupId, VoteResponse response, CPMember target) {
        RaftNode node = this.getOrInitRaftNodeIfTargetLocalCPMember(groupId, response, target);
        if (node != null) {
            node.handleVoteResponse(response);
        }
    }

    public void handleAppendEntries(CPGroupId groupId, AppendRequest request, CPMember target) {
        RaftNode node = this.getOrInitRaftNodeIfTargetLocalCPMember(groupId, request, target);
        if (node != null) {
            node.handleAppendRequest(request);
        }
    }

    public void handleAppendResponse(CPGroupId groupId, AppendSuccessResponse response, CPMember target) {
        RaftNode node = this.getOrInitRaftNodeIfTargetLocalCPMember(groupId, response, target);
        if (node != null) {
            node.handleAppendResponse(response);
        }
    }

    public void handleAppendResponse(CPGroupId groupId, AppendFailureResponse response, CPMember target) {
        RaftNode node = this.getOrInitRaftNodeIfTargetLocalCPMember(groupId, response, target);
        if (node != null) {
            node.handleAppendResponse(response);
        }
    }

    public void handleSnapshot(CPGroupId groupId, InstallSnapshot request, CPMember target) {
        RaftNode node = this.getOrInitRaftNodeIfTargetLocalCPMember(groupId, request, target);
        if (node != null) {
            node.handleInstallSnapshot(request);
        }
    }

    public Collection<RaftNode> getAllRaftNodes() {
        return new ArrayList<RaftNode>(this.nodes.values());
    }

    public RaftNode getRaftNode(CPGroupId groupId) {
        return (RaftNode)this.nodes.get(groupId);
    }

    public RaftNode getOrInitRaftNode(CPGroupId groupId) {
        RaftNode node = (RaftNode)this.nodes.get(groupId);
        if (node == null && this.metadataGroupManager.isDiscoveryCompleted() && !this.destroyedGroupIds.contains(groupId)) {
            this.logger.fine("RaftNode[" + groupId + "] does not exist. Asking to the METADATA CP group...");
            this.nodeEngine.getExecutionService().execute("hz:async", new InitializeRaftNodeTask(groupId));
        }
        return node;
    }

    private RaftNode getOrInitRaftNodeIfTargetLocalCPMember(CPGroupId groupId, Object message, CPMember target) {
        RaftNode node = this.getOrInitRaftNode(groupId);
        if (node == null) {
            if (this.logger.isFineEnabled()) {
                this.logger.warning("RaftNode[" + groupId + "] does not exist to handle: " + message);
            }
            return null;
        }
        if (!target.equals(node.getLocalMember())) {
            if (this.logger.isFineEnabled()) {
                this.logger.warning("Won't handle " + message + ". We are not the expected target: " + target);
            }
            return null;
        }
        return node;
    }

    public boolean isRaftGroupDestroyed(CPGroupId groupId) {
        return this.destroyedGroupIds.contains(groupId);
    }

    public CPSubsystemConfig getConfig() {
        return this.config;
    }

    @Override
    public CPMemberInfo getLocalCPMember() {
        return this.metadataGroupManager.getLocalCPMember();
    }

    public void createRaftNode(CPGroupId groupId, Collection<CPMemberInfo> members) {
        this.createRaftNode(groupId, members, this.getLocalCPMember());
    }

    void createRaftNode(CPGroupId groupId, Collection<CPMemberInfo> members, CPMember localCPMember) {
        RaftNodeImpl node;
        if (this.nodes.containsKey(groupId)) {
            return;
        }
        if (this.destroyedGroupIds.contains(groupId)) {
            this.logger.warning("Not creating RaftNode[" + groupId + "] since the CP group is already destroyed");
            return;
        }
        if (this.steppedDownGroupIds.contains(groupId)) {
            if (!this.nodeEngine.isRunning()) {
                this.logger.fine("Not creating RaftNode[" + groupId + "] since the local CP member is already stepped down");
                return;
            }
            this.steppedDownGroupIds.remove(groupId);
        }
        NodeEngineRaftIntegration integration = new NodeEngineRaftIntegration(this.nodeEngine, groupId, localCPMember);
        RaftAlgorithmConfig raftAlgorithmConfig = this.config.getRaftAlgorithmConfig();
        if ("METADATA".equals(groupId.name())) {
            raftAlgorithmConfig = new RaftAlgorithmConfig(raftAlgorithmConfig);
            raftAlgorithmConfig.setCommitIndexAdvanceCountToSnapshot(1000000);
        }
        if (this.nodes.putIfAbsent(groupId, node = new RaftNodeImpl(groupId, localCPMember, members, raftAlgorithmConfig, integration)) == null) {
            if (this.destroyedGroupIds.contains(groupId)) {
                node.forceSetTerminatedStatus();
                this.logger.warning("Not creating RaftNode[" + groupId + "] since the CP group is already destroyed");
                return;
            }
            node.start();
            this.logger.info("RaftNode[" + groupId + "] is created with " + members);
        }
    }

    public void destroyRaftNode(CPGroupId groupId) {
        this.destroyedGroupIds.add(groupId);
        RaftNode node = (RaftNode)this.nodes.remove(groupId);
        if (node != null) {
            node.forceSetTerminatedStatus();
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Local RaftNode[" + groupId + "] is destroyed.");
            }
        }
    }

    public void stepDownRaftNode(CPGroupId groupId) {
        RaftNode node = (RaftNode)this.nodes.get(groupId);
        if (node != null && node.getStatus() == RaftNodeStatus.STEPPED_DOWN) {
            this.steppedDownGroupIds.add(groupId);
            this.nodes.remove(groupId, node);
        }
    }

    public RaftGroupId createRaftGroupForProxy(String name) {
        String groupName = RaftService.getGroupNameForProxy(name);
        try {
            CPGroupInfo groupInfo = this.getGroupInfoForProxy(groupName).join();
            if (groupInfo != null) {
                return groupInfo.id();
            }
            return (RaftGroupId)this.invocationManager.createRaftGroup(groupName).get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Could not create CP group: " + groupName);
        }
        catch (ExecutionException e) {
            throw new IllegalStateException("Could not create CP group: " + groupName);
        }
    }

    public InternalCompletableFuture<RaftGroupId> createRaftGroupForProxyAsync(String name) {
        final String groupName = RaftService.getGroupNameForProxy(name);
        final SimpleCompletableFuture<RaftGroupId> future = this.newCompletableFuture();
        InternalCompletableFuture<CPGroupInfo> groupIdFuture = this.getGroupInfoForProxy(groupName);
        groupIdFuture.andThen(new ExecutionCallback<CPGroupInfo>(){

            @Override
            public void onResponse(CPGroupInfo response) {
                if (response != null) {
                    future.setResult(response.id());
                } else {
                    RaftService.this.invocationManager.createRaftGroup(groupName).andThen(new ExecutionCallback<RaftGroupId>(){

                        @Override
                        public void onResponse(RaftGroupId response) {
                            future.setResult(response);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            RaftService.this.complete(future, t);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable t) {
                RaftService.this.complete(future, t);
            }
        });
        return future;
    }

    private InternalCompletableFuture<CPGroupInfo> getGroupInfoForProxy(String groupName) {
        GetActiveRaftGroupByNameOp op = new GetActiveRaftGroupByNameOp(groupName);
        return this.invocationManager.invoke(this.getMetadataGroupId(), op);
    }

    private ICompletableFuture<Void> invokeTriggerRemoveMember(CPMemberInfo member) {
        return this.invocationManager.invoke(this.getMetadataGroupId(), new RemoveCPMemberOp(member));
    }

    private <T> SimpleCompletableFuture<T> complete(SimpleCompletableFuture<T> future, Throwable t) {
        if (!(t instanceof ExecutionException)) {
            t = new ExecutionException(t);
        }
        future.setResult(t);
        return future;
    }

    public static String withoutDefaultGroupName(String name) {
        int i = (name = name.trim()).indexOf("@");
        if (i == -1) {
            return name;
        }
        Preconditions.checkTrue(name.indexOf("@", i + 1) == -1, "Custom group name must be specified at most once");
        String groupName = name.substring(i + 1).trim();
        if (groupName.equalsIgnoreCase("default")) {
            return name.substring(0, i);
        }
        return name;
    }

    public static String getGroupNameForProxy(String name) {
        int i = (name = name.trim()).indexOf("@");
        if (i == -1) {
            return "default";
        }
        Preconditions.checkTrue(i < name.length() - 1, "Custom CP group name cannot be empty string");
        Preconditions.checkTrue(name.indexOf("@", i + 1) == -1, "Custom group name must be specified at most once");
        String groupName = name.substring(i + 1).trim();
        Preconditions.checkTrue(groupName.length() > 0, "Custom CP group name cannot be empty string");
        Preconditions.checkFalse(groupName.equalsIgnoreCase("METADATA"), "CP data structures cannot run on the METADATA CP group!");
        return groupName.equalsIgnoreCase("default") ? "default" : groupName;
    }

    public static String getObjectNameForProxy(String name) {
        int i = name.indexOf("@");
        if (i == -1) {
            return name;
        }
        Preconditions.checkTrue(i < name.length() - 1, "Object name cannot be empty string");
        Preconditions.checkTrue(name.indexOf("@", i + 1) == -1, "Custom CP group name must be specified at most once");
        String objectName = name.substring(0, i).trim();
        Preconditions.checkTrue(objectName.length() > 0, "Object name cannot be empty string");
        return objectName;
    }

    public RaftGroupId getMetadataGroupId() {
        return this.metadataGroupManager.getMetadataGroupId();
    }

    public void handleActiveCPMembers(RaftGroupId latestMetadataGroupId, long membersCommitIndex, Collection<CPMemberInfo> members) {
        if (!this.metadataGroupManager.isDiscoveryCompleted()) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Ignoring received active CP members: " + members + " since discovery is in progress.");
            }
            return;
        }
        Preconditions.checkNotNull(members);
        Preconditions.checkTrue(members.size() > 0, "Active CP members list cannot be empty");
        if (members.size() == 1) {
            this.logger.fine("There is one active CP member left: " + members);
            return;
        }
        this.invocationManager.getRaftInvocationContext().setMembers(latestMetadataGroupId.seed(), membersCommitIndex, members);
        CPMemberInfo localMember = this.getLocalCPMember();
        if (localMember != null && !members.contains(localMember) && this.nodeEngine.getNode().isRunning()) {
            if (this.nodeEngine.getNode().isRunning()) {
                boolean missingAutoRemovalEnabled = this.config.getMissingCPMemberAutoRemovalSeconds() > 0;
                this.logger.severe("Local " + localMember + " is not part of received active CP members: " + members + ". It seems local member is removed from CP subsystem. Auto removal of missing members is " + (missingAutoRemovalEnabled ? "enabled." : "disabled."));
            }
            return;
        }
        RaftGroupId metadataGroupId = this.getMetadataGroupId();
        if (latestMetadataGroupId.seed() < metadataGroupId.seed() || metadataGroupId.equals(latestMetadataGroupId)) {
            return;
        }
        if (this.getRaftNode(latestMetadataGroupId) != null) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine(localMember + " is already part of METADATA group but received active CP members!");
            }
            return;
        }
        if (!latestMetadataGroupId.equals(metadataGroupId) && this.getRaftNode(metadataGroupId) != null) {
            this.logger.warning(localMember + " was part of " + metadataGroupId + ", but received active CP members for " + latestMetadataGroupId + ".");
            return;
        }
        this.metadataGroupManager.handleMetadataGroupId(latestMetadataGroupId);
    }

    @Override
    public void onRaftGroupDestroyed(CPGroupId groupId) {
        this.destroyRaftNode(groupId);
    }

    @Override
    public void onRaftNodeSteppedDown(CPGroupId groupId) {
        this.stepDownRaftNode(groupId);
    }

    private class AutoRemoveMissingCPMemberTask
    implements Runnable {
        private AutoRemoveMissingCPMemberTask() {
        }

        @Override
        public void run() {
            try {
                if (!RaftService.this.metadataGroupManager.isMetadataGroupLeader() || RaftService.this.metadataGroupManager.getMembershipChangeSchedule() != null) {
                    return;
                }
                for (Map.Entry e : RaftService.this.missingMembers.entrySet()) {
                    long missingTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - (Long)e.getValue());
                    if (missingTimeSeconds < (long)RaftService.this.config.getMissingCPMemberAutoRemovalSeconds()) continue;
                    CPMemberInfo missingMember = (CPMemberInfo)e.getKey();
                    RaftService.this.logger.warning("Removing " + missingMember + " since it is absent for " + missingTimeSeconds + " seconds.");
                    RaftService.this.removeCPMember(missingMember.getUuid()).get();
                    RaftService.this.logger.info("Auto-removal of " + missingMember + " is successful.");
                    return;
                }
            }
            catch (Exception e) {
                RaftService.this.logger.severe("RemoveMissingMembersTask failed", e);
            }
        }
    }

    private class InitializeRaftNodeTask
    implements Runnable {
        private final CPGroupId groupId;

        InitializeRaftNodeTask(CPGroupId groupId) {
            this.groupId = groupId;
        }

        @Override
        public void run() {
            this.queryInitialMembersFromMetadataRaftGroup();
        }

        private void queryInitialMembersFromMetadataRaftGroup() {
            GetRaftGroupOp op = new GetRaftGroupOp(this.groupId);
            InternalCompletableFuture f = RaftService.this.invocationManager.query(RaftService.this.getMetadataGroupId(), op, QueryPolicy.LEADER_LOCAL);
            f.andThen(new ExecutionCallback<CPGroupInfo>(){

                @Override
                public void onResponse(CPGroupInfo group) {
                    if (group != null) {
                        if (group.memberImpls().contains(RaftService.this.getLocalCPMember())) {
                            RaftService.this.createRaftNode(InitializeRaftNodeTask.this.groupId, group.initialMembers());
                        } else {
                            InitializeRaftNodeTask.this.queryInitialMembersFromTargetRaftGroup();
                        }
                    } else if (RaftService.this.logger.isFineEnabled()) {
                        RaftService.this.logger.fine("Cannot get initial members of " + InitializeRaftNodeTask.this.groupId + " from the METADATA CP group");
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    if (t instanceof CPGroupDestroyedException) {
                        CPGroupId destroyedGroupId = ((CPGroupDestroyedException)t).getGroupId();
                        RaftService.this.destroyedGroupIds.add(destroyedGroupId);
                    }
                    if (RaftService.this.logger.isFineEnabled()) {
                        RaftService.this.logger.fine("Cannot get initial members of " + InitializeRaftNodeTask.this.groupId + " from the METADATA CP group", t);
                    }
                }
            });
        }

        void queryInitialMembersFromTargetRaftGroup() {
            CPMemberInfo localMember = RaftService.this.getLocalCPMember();
            if (localMember == null) {
                return;
            }
            GetInitialRaftGroupMembersIfCurrentGroupMemberOp op = new GetInitialRaftGroupMembersIfCurrentGroupMemberOp(localMember);
            InternalCompletableFuture f = RaftService.this.invocationManager.query(this.groupId, op, QueryPolicy.LEADER_LOCAL);
            f.andThen(new ExecutionCallback<Collection<CPMemberInfo>>(){

                @Override
                public void onResponse(Collection<CPMemberInfo> initialMembers) {
                    RaftService.this.createRaftNode(InitializeRaftNodeTask.this.groupId, initialMembers);
                }

                @Override
                public void onFailure(Throwable t) {
                    if (RaftService.this.logger.isFineEnabled()) {
                        RaftService.this.logger.fine("Cannot get initial members of " + InitializeRaftNodeTask.this.groupId + " from the CP group itself", t);
                    }
                }
            });
        }
    }
}

