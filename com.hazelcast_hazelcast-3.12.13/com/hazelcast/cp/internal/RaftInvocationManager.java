/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.CPMemberInfo;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.exception.CannotCreateRaftGroupException;
import com.hazelcast.cp.internal.operation.ChangeRaftGroupMembershipOp;
import com.hazelcast.cp.internal.operation.DefaultRaftReplicateOp;
import com.hazelcast.cp.internal.operation.DestroyRaftGroupOp;
import com.hazelcast.cp.internal.operation.RaftQueryOp;
import com.hazelcast.cp.internal.raft.MembershipChangeMode;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.cp.internal.raftop.metadata.CreateRaftGroupOp;
import com.hazelcast.cp.internal.raftop.metadata.GetActiveCPMembersOp;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.util.SimpleCompletableFuture;
import com.hazelcast.internal.util.SimpleCompletedFuture;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.spi.impl.operationservice.impl.RaftInvocation;
import com.hazelcast.spi.impl.operationservice.impl.RaftInvocationContext;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.executor.ManagedExecutorService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RaftInvocationManager {
    private final NodeEngineImpl nodeEngine;
    private final OperationServiceImpl operationService;
    private final RaftService raftService;
    private final ILogger logger;
    private final RaftInvocationContext raftInvocationContext;
    private final long operationCallTimeout;
    private final int invocationMaxRetryCount;
    private final long invocationRetryPauseMillis;
    private final boolean cpSubsystemEnabled;

    RaftInvocationManager(NodeEngine nodeEngine, RaftService raftService) {
        this.nodeEngine = (NodeEngineImpl)nodeEngine;
        this.operationService = (OperationServiceImpl)nodeEngine.getOperationService();
        this.logger = nodeEngine.getLogger(this.getClass());
        this.raftService = raftService;
        this.raftInvocationContext = new RaftInvocationContext(this.logger, raftService);
        this.invocationMaxRetryCount = nodeEngine.getProperties().getInteger(GroupProperty.INVOCATION_MAX_RETRY_COUNT);
        this.invocationRetryPauseMillis = nodeEngine.getProperties().getMillis(GroupProperty.INVOCATION_RETRY_PAUSE);
        this.operationCallTimeout = nodeEngine.getProperties().getMillis(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS);
        this.cpSubsystemEnabled = raftService.getConfig().getCPMemberCount() > 0;
    }

    void reset() {
        this.raftInvocationContext.reset();
    }

    public InternalCompletableFuture<RaftGroupId> createRaftGroup(String groupName) {
        return this.createRaftGroup(groupName, this.raftService.getConfig().getGroupSize());
    }

    public InternalCompletableFuture<RaftGroupId> createRaftGroup(String groupName, int groupSize) {
        InternalCompletableFuture<RaftGroupId> completedFuture = this.completeExceptionallyIfCPSubsystemNotAvailable();
        if (completedFuture != null) {
            return completedFuture;
        }
        ManagedExecutorService executor = this.nodeEngine.getExecutionService().getExecutor("hz:async");
        SimpleCompletableFuture<RaftGroupId> resultFuture = new SimpleCompletableFuture<RaftGroupId>(executor, this.logger);
        this.invokeGetMembersToCreateRaftGroup(groupName, groupSize, resultFuture);
        return resultFuture;
    }

    private <V> InternalCompletableFuture<V> completeExceptionallyIfCPSubsystemNotAvailable() {
        if (this.nodeEngine.getClusterService().getClusterVersion().isLessThan(Versions.V3_12)) {
            return new SimpleCompletedFuture(new UnsupportedOperationException("CP Subsystem is not available before version 3.12!"));
        }
        if (!this.cpSubsystemEnabled) {
            return new SimpleCompletedFuture(new HazelcastException("CP Subsystem is not enabled!"));
        }
        return null;
    }

    private void invokeGetMembersToCreateRaftGroup(final String groupName, final int groupSize, final SimpleCompletableFuture<RaftGroupId> resultFuture) {
        GetActiveCPMembersOp op = new GetActiveCPMembersOp();
        InternalCompletableFuture f = this.query(this.raftService.getMetadataGroupId(), op, QueryPolicy.LEADER_LOCAL);
        f.andThen(new ExecutionCallback<List<CPMemberInfo>>(){

            @Override
            public void onResponse(List<CPMemberInfo> members) {
                if ((members = new ArrayList<CPMemberInfo>(members)).size() < groupSize) {
                    IllegalArgumentException result = new IllegalArgumentException("There are not enough active members to create CP group " + groupName + ". Active members: " + members.size() + ", Requested count: " + groupSize);
                    resultFuture.setResult(result);
                    return;
                }
                Collections.shuffle(members);
                Collections.sort(members, new CPMemberReachabilityComparator());
                members = members.subList(0, groupSize);
                RaftInvocationManager.this.invokeCreateRaftGroup(groupName, groupSize, members, resultFuture);
            }

            @Override
            public void onFailure(Throwable t) {
                resultFuture.setResult(new ExecutionException(t));
            }
        });
    }

    private void invokeCreateRaftGroup(final String groupName, final int groupSize, final List<CPMemberInfo> members, final SimpleCompletableFuture<RaftGroupId> resultFuture) {
        InternalCompletableFuture f = this.invoke(this.raftService.getMetadataGroupId(), new CreateRaftGroupOp(groupName, members));
        f.andThen(new ExecutionCallback<RaftGroupId>(){

            @Override
            public void onResponse(RaftGroupId groupId) {
                resultFuture.setResult(groupId);
            }

            @Override
            public void onFailure(Throwable t) {
                if (t instanceof CannotCreateRaftGroupException) {
                    RaftInvocationManager.this.logger.fine("Could not create CP group: " + groupName + " with members: " + members, t.getCause());
                    RaftInvocationManager.this.invokeGetMembersToCreateRaftGroup(groupName, groupSize, resultFuture);
                    return;
                }
                resultFuture.setResult(t);
            }
        });
    }

    <T> InternalCompletableFuture<T> changeMembership(CPGroupId groupId, long membersCommitIndex, CPMemberInfo member, MembershipChangeMode membershipChangeMode) {
        InternalCompletableFuture completedFuture = this.completeExceptionallyIfCPSubsystemNotAvailable();
        if (completedFuture != null) {
            return completedFuture;
        }
        ChangeRaftGroupMembershipOp operation = new ChangeRaftGroupMembershipOp(groupId, membersCommitIndex, member, membershipChangeMode);
        RaftInvocation invocation = new RaftInvocation(this.operationService.getInvocationContext(), this.raftInvocationContext, groupId, operation, this.invocationMaxRetryCount, this.invocationRetryPauseMillis, this.operationCallTimeout);
        return invocation.invoke();
    }

    public <T> InternalCompletableFuture<T> invoke(CPGroupId groupId, RaftOp raftOp) {
        return this.invoke(groupId, raftOp, true);
    }

    public <T> InternalCompletableFuture<T> invoke(CPGroupId groupId, RaftOp raftOp, boolean deserializeResponse) {
        InternalCompletableFuture completedFuture = this.completeExceptionallyIfCPSubsystemNotAvailable();
        if (completedFuture != null) {
            return completedFuture;
        }
        DefaultRaftReplicateOp operation = new DefaultRaftReplicateOp(groupId, raftOp);
        RaftInvocation invocation = new RaftInvocation(this.operationService.getInvocationContext(), this.raftInvocationContext, groupId, operation, this.invocationMaxRetryCount, this.invocationRetryPauseMillis, this.operationCallTimeout, deserializeResponse);
        return invocation.invoke();
    }

    public <T> InternalCompletableFuture<T> query(CPGroupId groupId, RaftOp raftOp, QueryPolicy queryPolicy) {
        return this.query(groupId, raftOp, queryPolicy, true);
    }

    public <T> InternalCompletableFuture<T> query(CPGroupId groupId, RaftOp raftOp, QueryPolicy queryPolicy, boolean deserializeResponse) {
        InternalCompletableFuture completedFuture = this.completeExceptionallyIfCPSubsystemNotAvailable();
        if (completedFuture != null) {
            return completedFuture;
        }
        RaftQueryOp operation = new RaftQueryOp(groupId, raftOp, queryPolicy);
        RaftInvocation invocation = new RaftInvocation(this.operationService.getInvocationContext(), this.raftInvocationContext, groupId, operation, this.invocationMaxRetryCount, this.invocationRetryPauseMillis, this.operationCallTimeout, deserializeResponse);
        return invocation.invoke();
    }

    public <T> InternalCompletableFuture<T> queryLocally(CPGroupId groupId, RaftOp raftOp, QueryPolicy queryPolicy) {
        InternalCompletableFuture completedFuture = this.completeExceptionallyIfCPSubsystemNotAvailable();
        if (completedFuture != null) {
            return completedFuture;
        }
        RaftQueryOp operation = new RaftQueryOp(groupId, raftOp, queryPolicy);
        return this.nodeEngine.getOperationService().invokeOnTarget("hz:core:raft", operation, this.nodeEngine.getThisAddress());
    }

    public InternalCompletableFuture<Object> destroy(CPGroupId groupId) {
        InternalCompletableFuture<Object> completedFuture = this.completeExceptionallyIfCPSubsystemNotAvailable();
        if (completedFuture != null) {
            return completedFuture;
        }
        DestroyRaftGroupOp operation = new DestroyRaftGroupOp(groupId);
        RaftInvocation invocation = new RaftInvocation(this.operationService.getInvocationContext(), this.raftInvocationContext, groupId, operation, this.invocationMaxRetryCount, this.invocationRetryPauseMillis, this.operationCallTimeout);
        return invocation.invoke();
    }

    public RaftInvocationContext getRaftInvocationContext() {
        return this.raftInvocationContext;
    }

    private class CPMemberReachabilityComparator
    implements Comparator<CPMemberInfo> {
        final ClusterService clusterService;

        private CPMemberReachabilityComparator() {
            this.clusterService = RaftInvocationManager.this.nodeEngine.getClusterService();
        }

        @Override
        public int compare(CPMemberInfo o1, CPMemberInfo o2) {
            boolean b2;
            boolean b1 = this.clusterService.getMember(o1.getAddress()) != null;
            boolean bl = b2 = this.clusterService.getMember(o2.getAddress()) != null;
            return b1 == b2 ? 0 : (b1 ? -1 : 1);
        }
    }
}

