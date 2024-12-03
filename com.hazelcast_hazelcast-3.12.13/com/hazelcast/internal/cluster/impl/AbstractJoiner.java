/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.cluster.Joiner;
import com.hazelcast.config.Config;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeExtension;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.cluster.impl.ClusterJoinManager;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.SplitBrainJoinMessage;
import com.hazelcast.internal.cluster.impl.operations.MergeClustersOp;
import com.hazelcast.internal.cluster.impl.operations.SplitBrainMergeValidationOp;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.OperationResponseHandlerFactory;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.Clock;
import com.hazelcast.util.FutureUtil;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractJoiner
implements Joiner {
    private static final int JOIN_TRY_COUNT = 5;
    private static final int SPLIT_BRAIN_MERGE_TIMEOUT_SECONDS = 30;
    private static final int SPLIT_BRAIN_JOIN_CHECK_TIMEOUT_SECONDS = 10;
    private static final long MIN_WAIT_BEFORE_JOIN_SECONDS = 10L;
    private static final long SPLIT_BRAIN_SLEEP_TIME_MILLIS = 10L;
    private static final long SPLIT_BRAIN_CONN_TIMEOUT_MILLIS = 5000L;
    protected final Config config;
    protected final Node node;
    protected final ClusterServiceImpl clusterService;
    protected final ILogger logger;
    protected final ConcurrentMap<Address, Boolean> blacklistedAddresses = new ConcurrentHashMap<Address, Boolean>();
    protected final ClusterJoinManager clusterJoinManager;
    private final AtomicLong joinStartTime = new AtomicLong(Clock.currentTimeMillis());
    private final AtomicInteger tryCount = new AtomicInteger(0);
    private final long mergeNextRunDelayMs;
    private volatile Address targetAddress;
    private final FutureUtil.ExceptionHandler splitBrainMergeExceptionHandler = new FutureUtil.ExceptionHandler(){

        @Override
        public void handleException(Throwable throwable) {
            if (throwable instanceof MemberLeftException) {
                return;
            }
            AbstractJoiner.this.logger.warning("Problem while waiting for merge operation result", throwable);
        }
    };

    public AbstractJoiner(Node node) {
        this.node = node;
        this.logger = node.loggingService.getLogger(this.getClass());
        this.config = node.config;
        this.clusterService = node.getClusterService();
        this.clusterJoinManager = this.clusterService.getClusterJoinManager();
        this.mergeNextRunDelayMs = node.getProperties().getMillis(GroupProperty.MERGE_NEXT_RUN_DELAY_SECONDS);
    }

    @Override
    public final long getStartTime() {
        return this.joinStartTime.get();
    }

    @Override
    public void setTargetAddress(Address targetAddress) {
        this.targetAddress = targetAddress;
    }

    @Override
    public void blacklist(Address address, boolean permanent) {
        this.logger.info(address + " is added to the blacklist.");
        this.blacklistedAddresses.putIfAbsent(address, permanent);
    }

    @Override
    public boolean unblacklist(Address address) {
        if (this.blacklistedAddresses.remove(address, Boolean.FALSE)) {
            this.logger.info(address + " is removed from the blacklist.");
            return true;
        }
        return false;
    }

    @Override
    public boolean isBlacklisted(Address address) {
        return this.blacklistedAddresses.containsKey(address);
    }

    public abstract void doJoin();

    @Override
    public final void join() {
        this.blacklistedAddresses.clear();
        this.doJoin();
        if (!this.clusterService.isJoined() && this.isMemberExcludedFromHotRestart()) {
            this.logger.warning("Could not join to the cluster because hot restart data must be reset.");
            this.node.getNodeExtension().getInternalHotRestartService().forceStartBeforeJoin();
            this.reset();
            this.doJoin();
        }
        this.postJoin();
    }

    protected final boolean shouldRetry() {
        return this.node.isRunning() && !this.clusterService.isJoined() && !this.isMemberExcludedFromHotRestart();
    }

    private boolean isMemberExcludedFromHotRestart() {
        NodeExtension nodeExtension = this.node.getNodeExtension();
        return !nodeExtension.isStartCompleted() && nodeExtension.getInternalHotRestartService().isMemberExcluded(this.node.getThisAddress(), this.node.getThisUuid());
    }

    private void postJoin() {
        this.blacklistedAddresses.clear();
        if (this.logger.isFineEnabled()) {
            this.logger.fine("PostJoin master: " + this.clusterService.getMasterAddress() + ", isMaster: " + this.clusterService.isMaster());
        }
        if (!this.node.isRunning()) {
            return;
        }
        if (this.tryCount.incrementAndGet() == 5) {
            this.logger.warning("Join try count exceed limit, setting this node as master!");
            this.clusterJoinManager.setThisMemberAsMaster();
        }
        if (this.clusterService.isJoined()) {
            if (!this.clusterService.isMaster()) {
                this.ensureConnectionToAllMembers();
            }
            if (this.clusterService.getSize() == 1) {
                this.clusterService.printMemberList();
            }
        }
    }

    private void ensureConnectionToAllMembers() {
        if (this.clusterService.isJoined()) {
            this.logger.fine("Waiting for all connections");
            int connectAllWaitSeconds = this.node.getProperties().getSeconds(GroupProperty.CONNECT_ALL_WAIT_SECONDS);
            int checkCount = 0;
            while (checkCount++ < connectAllWaitSeconds) {
                boolean allConnected = true;
                Set<Member> members = this.clusterService.getMembers();
                for (Member member : members) {
                    if (member.localMember() || this.node.getEndpointManager(EndpointQualifier.MEMBER).getOrConnect(member.getAddress()) != null) continue;
                    allConnected = false;
                    if (!this.logger.isFineEnabled()) continue;
                    this.logger.fine("Not-connected to " + member.getAddress());
                }
                if (allConnected) break;
                try {
                    TimeUnit.SECONDS.sleep(1L);
                }
                catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    protected final long getMaxJoinMillis() {
        return this.node.getProperties().getMillis(GroupProperty.MAX_JOIN_SECONDS);
    }

    protected final long getMaxJoinTimeToMasterNode() {
        return TimeUnit.SECONDS.toMillis(10L) + this.node.getProperties().getMillis(GroupProperty.MAX_WAIT_SECONDS_BEFORE_JOIN);
    }

    protected final SplitBrainJoinMessage.SplitBrainMergeCheckResult sendSplitBrainJoinMessageAndCheckResponse(Address target, SplitBrainJoinMessage request) {
        SplitBrainJoinMessage response = this.sendSplitBrainJoinMessage(target, request);
        return this.clusterService.getClusterJoinManager().shouldMerge(response);
    }

    private SplitBrainJoinMessage sendSplitBrainJoinMessage(Address target, SplitBrainJoinMessage request) {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Sending SplitBrainJoinMessage to " + target);
        }
        Object conn = this.node.getEndpointManager(EndpointQualifier.MEMBER).getOrConnect(target, true);
        long timeout = 5000L;
        while (conn == null) {
            if ((timeout -= 10L) < 0L) {
                this.logger.fine("Returning null timeout<0, " + timeout);
                return null;
            }
            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
            conn = this.node.getEndpointManager(EndpointQualifier.MEMBER).getConnection(target);
        }
        NodeEngineImpl nodeEngine = this.node.nodeEngine;
        InternalCompletableFuture future = nodeEngine.getOperationService().createInvocationBuilder("hz:core:clusterService", (Operation)new SplitBrainMergeValidationOp(request), target).setTryCount(1).invoke();
        try {
            return (SplitBrainJoinMessage)future.get(10L, TimeUnit.SECONDS);
        }
        catch (TimeoutException e) {
            this.logger.fine("Timeout during join check!", e);
        }
        catch (Exception e) {
            this.logger.warning("Error during join check!", e);
        }
        return null;
    }

    @Override
    public void reset() {
        this.joinStartTime.set(Clock.currentTimeMillis());
        this.tryCount.set(0);
    }

    protected void startClusterMerge(Address targetAddress, int expectedMemberListVersion) {
        ClusterServiceImpl clusterService = this.node.clusterService;
        if (!this.prepareClusterState(clusterService, expectedMemberListVersion)) {
            return;
        }
        InternalOperationService operationService = this.node.nodeEngine.getOperationService();
        Set<Member> memberList = clusterService.getMembers();
        ArrayList futures = new ArrayList(memberList.size());
        for (Member member : memberList) {
            if (member.localMember()) continue;
            MergeClustersOp op = new MergeClustersOp(targetAddress);
            InternalCompletableFuture future = operationService.invokeOnTarget("hz:core:clusterService", op, member.getAddress());
            futures.add(future);
        }
        FutureUtil.waitWithDeadline(futures, 30L, TimeUnit.SECONDS, this.splitBrainMergeExceptionHandler);
        MergeClustersOp op = new MergeClustersOp(targetAddress);
        op.setNodeEngine(this.node.nodeEngine).setService(clusterService).setOperationResponseHandler(OperationResponseHandlerFactory.createEmptyResponseHandler());
        operationService.run(op);
    }

    private boolean prepareClusterState(ClusterServiceImpl clusterService, int expectedMemberListVersion) {
        if (!this.preCheckClusterState(clusterService)) {
            return false;
        }
        long until = Clock.currentTimeMillis() + this.mergeNextRunDelayMs;
        while (Clock.currentTimeMillis() < until) {
            ClusterState clusterState = clusterService.getClusterState();
            if (!clusterState.isMigrationAllowed() && !clusterState.isJoinAllowed() && clusterState != ClusterState.IN_TRANSITION) {
                return clusterService.getMemberListVersion() == expectedMemberListVersion;
            }
            if (clusterService.getMemberListVersion() != expectedMemberListVersion) {
                this.logger.warning("Could not change cluster state to FROZEN because local member list version: " + clusterService.getMemberListVersion() + " is different than expected member list version: " + expectedMemberListVersion);
                return false;
            }
            if (clusterState != ClusterState.IN_TRANSITION) {
                try {
                    clusterService.changeClusterState(ClusterState.FROZEN);
                    return this.verifyMemberListVersionAfterStateChange(clusterService, clusterState, expectedMemberListVersion);
                }
                catch (Exception e) {
                    String error = e.getClass().getName() + ": " + e.getMessage();
                    this.logger.warning("While changing cluster state to FROZEN! " + error);
                }
            }
            try {
                TimeUnit.SECONDS.sleep(1L);
            }
            catch (InterruptedException e) {
                this.logger.warning("Interrupted while preparing cluster for merge!");
                Thread.currentThread().interrupt();
                return false;
            }
        }
        this.logger.warning("Could not change cluster state to FROZEN in time. Postponing merge process until next attempt.");
        return false;
    }

    private boolean verifyMemberListVersionAfterStateChange(ClusterServiceImpl clusterService, ClusterState clusterState, int expectedMemberListVersion) {
        if (clusterService.getMemberListVersion() != expectedMemberListVersion) {
            try {
                this.logger.warning("Reverting cluster state back to " + (Object)((Object)clusterState) + " because member list version: " + clusterService.getMemberListVersion() + " is different than expected member list version: " + expectedMemberListVersion);
                clusterService.changeClusterState(clusterState);
            }
            catch (Exception e) {
                String error = e.getClass().getName() + ": " + e.getMessage();
                this.logger.warning("While reverting cluster state to " + (Object)((Object)clusterState) + "! " + error);
            }
            return false;
        }
        return true;
    }

    private boolean preCheckClusterState(ClusterService clusterService) {
        ClusterState initialState = clusterService.getClusterState();
        if (!initialState.isJoinAllowed()) {
            this.logger.warning("Could not prepare cluster state since it has been changed to " + (Object)((Object)initialState));
            return false;
        }
        return true;
    }

    protected Address getTargetAddress() {
        Address target = this.targetAddress;
        this.targetAddress = null;
        return target;
    }
}

