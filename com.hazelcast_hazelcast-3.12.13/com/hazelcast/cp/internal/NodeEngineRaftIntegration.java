/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.CPMember;
import com.hazelcast.cp.internal.RaftNodeLifecycleAwareService;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.operation.integration.AppendFailureResponseOp;
import com.hazelcast.cp.internal.operation.integration.AppendRequestOp;
import com.hazelcast.cp.internal.operation.integration.AppendSuccessResponseOp;
import com.hazelcast.cp.internal.operation.integration.AsyncRaftOp;
import com.hazelcast.cp.internal.operation.integration.InstallSnapshotOp;
import com.hazelcast.cp.internal.operation.integration.PreVoteRequestOp;
import com.hazelcast.cp.internal.operation.integration.PreVoteResponseOp;
import com.hazelcast.cp.internal.operation.integration.VoteRequestOp;
import com.hazelcast.cp.internal.operation.integration.VoteResponseOp;
import com.hazelcast.cp.internal.raft.SnapshotAwareService;
import com.hazelcast.cp.internal.raft.impl.RaftIntegration;
import com.hazelcast.cp.internal.raft.impl.RaftNodeStatus;
import com.hazelcast.cp.internal.raft.impl.dto.AppendFailureResponse;
import com.hazelcast.cp.internal.raft.impl.dto.AppendRequest;
import com.hazelcast.cp.internal.raft.impl.dto.AppendSuccessResponse;
import com.hazelcast.cp.internal.raft.impl.dto.InstallSnapshot;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteRequest;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteResponse;
import com.hazelcast.cp.internal.raft.impl.dto.VoteRequest;
import com.hazelcast.cp.internal.raft.impl.dto.VoteResponse;
import com.hazelcast.cp.internal.raftop.NotifyTermChangeOp;
import com.hazelcast.cp.internal.raftop.snapshot.RestoreSnapshotOp;
import com.hazelcast.cp.internal.util.PartitionSpecificRunnableAdaptor;
import com.hazelcast.internal.util.SimpleCompletableFuture;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationexecutor.impl.OperationExecutorImpl;
import com.hazelcast.spi.impl.operationexecutor.impl.PartitionOperationThread;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.spi.impl.servicemanager.ServiceInfo;
import com.hazelcast.util.executor.ManagedExecutorService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

final class NodeEngineRaftIntegration
implements RaftIntegration {
    private final NodeEngineImpl nodeEngine;
    private final CPGroupId groupId;
    private final CPMember localCPMember;
    private final InternalOperationService operationService;
    private final TaskScheduler taskScheduler;
    private final int partitionId;
    private final int threadId;

    NodeEngineRaftIntegration(NodeEngineImpl nodeEngine, CPGroupId groupId, CPMember localCPMember) {
        this.nodeEngine = nodeEngine;
        this.groupId = groupId;
        this.localCPMember = localCPMember;
        OperationServiceImpl operationService = (OperationServiceImpl)nodeEngine.getOperationService();
        this.operationService = operationService;
        this.partitionId = nodeEngine.getPartitionService().getPartitionId(groupId);
        OperationExecutorImpl operationExecutor = (OperationExecutorImpl)operationService.getOperationExecutor();
        this.threadId = operationExecutor.toPartitionThreadIndex(this.partitionId);
        this.taskScheduler = nodeEngine.getExecutionService().getGlobalTaskScheduler();
    }

    @Override
    public void execute(Runnable task) {
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof PartitionOperationThread && ((PartitionOperationThread)currentThread).getThreadId() == this.threadId) {
            task.run();
        } else {
            this.operationService.execute(new PartitionSpecificRunnableAdaptor(task, this.partitionId));
        }
    }

    @Override
    public void schedule(final Runnable task, long delay, TimeUnit timeUnit) {
        this.taskScheduler.schedule(new Runnable(){

            @Override
            public void run() {
                NodeEngineRaftIntegration.this.execute(task);
            }
        }, delay, timeUnit);
    }

    @Override
    public SimpleCompletableFuture newCompletableFuture() {
        ManagedExecutorService executor = this.nodeEngine.getExecutionService().getExecutor("hz:async");
        return new SimpleCompletableFuture(executor, this.nodeEngine.getLogger(this.getClass()));
    }

    @Override
    public Object getAppendedEntryOnLeaderElection() {
        return new NotifyTermChangeOp();
    }

    @Override
    public ILogger getLogger(String name) {
        return this.nodeEngine.getLogger(name);
    }

    @Override
    public boolean isReady() {
        return this.nodeEngine.getClusterService().isJoined();
    }

    @Override
    public boolean isReachable(Endpoint member) {
        return this.nodeEngine.getClusterService().getMember(((CPMember)member).getAddress()) != null;
    }

    @Override
    public boolean send(PreVoteRequest request, Endpoint target) {
        return this.send(new PreVoteRequestOp(this.groupId, request), target);
    }

    @Override
    public boolean send(PreVoteResponse response, Endpoint target) {
        return this.send(new PreVoteResponseOp(this.groupId, response), target);
    }

    @Override
    public boolean send(VoteRequest request, Endpoint target) {
        return this.send(new VoteRequestOp(this.groupId, request), target);
    }

    @Override
    public boolean send(VoteResponse response, Endpoint target) {
        return this.send(new VoteResponseOp(this.groupId, response), target);
    }

    @Override
    public boolean send(AppendRequest request, Endpoint target) {
        return this.send(new AppendRequestOp(this.groupId, request), target);
    }

    @Override
    public boolean send(AppendSuccessResponse response, Endpoint target) {
        return this.send(new AppendSuccessResponseOp(this.groupId, response), target);
    }

    @Override
    public boolean send(AppendFailureResponse response, Endpoint target) {
        return this.send(new AppendFailureResponseOp(this.groupId, response), target);
    }

    @Override
    public boolean send(InstallSnapshot request, Endpoint target) {
        return this.send(new InstallSnapshotOp(this.groupId, request), target);
    }

    @Override
    public Object runOperation(Object op, long commitIndex) {
        RaftOp operation = (RaftOp)op;
        operation.setNodeEngine(this.nodeEngine);
        try {
            return operation.run(this.groupId, commitIndex);
        }
        catch (Throwable t) {
            operation.logFailure(t);
            return t;
        }
    }

    @Override
    public Object takeSnapshot(long commitIndex) {
        try {
            ArrayList<RestoreSnapshotOp> snapshotOps = new ArrayList<RestoreSnapshotOp>();
            for (ServiceInfo serviceInfo : this.nodeEngine.getServiceInfos(SnapshotAwareService.class)) {
                SnapshotAwareService service = (SnapshotAwareService)serviceInfo.getService();
                Object snapshot = service.takeSnapshot(this.groupId, commitIndex);
                if (snapshot == null) continue;
                snapshotOps.add(new RestoreSnapshotOp(serviceInfo.getName(), snapshot));
            }
            return snapshotOps;
        }
        catch (Throwable t) {
            return t;
        }
    }

    @Override
    public void restoreSnapshot(Object op, long commitIndex) {
        ILogger logger = this.nodeEngine.getLogger(this.getClass());
        List snapshotOps = (List)op;
        for (RestoreSnapshotOp snapshotOp : snapshotOps) {
            Object result = this.runOperation(snapshotOp, commitIndex);
            if (!(result instanceof Throwable)) continue;
            logger.severe("Restore of " + snapshotOp + " failed...", (Throwable)result);
        }
    }

    private boolean send(AsyncRaftOp operation, Endpoint target) {
        CPMember targetMember = (CPMember)target;
        if (this.localCPMember.getAddress().equals(targetMember.getAddress())) {
            if (this.localCPMember.getUuid().equals(target.getUuid())) {
                throw new IllegalStateException("Cannot send " + operation + " to " + target + " because it's same with the local CP member!");
            }
            return false;
        }
        operation.setTargetMember(targetMember).setPartitionId(this.partitionId);
        return this.operationService.send(operation, targetMember.getAddress());
    }

    @Override
    public void onNodeStatusChange(RaftNodeStatus status) {
        block3: {
            block2: {
                if (status != RaftNodeStatus.TERMINATED) break block2;
                Collection<RaftNodeLifecycleAwareService> services = this.nodeEngine.getServices(RaftNodeLifecycleAwareService.class);
                for (RaftNodeLifecycleAwareService service : services) {
                    service.onRaftGroupDestroyed(this.groupId);
                }
                break block3;
            }
            if (status != RaftNodeStatus.STEPPED_DOWN) break block3;
            Collection<RaftNodeLifecycleAwareService> services = this.nodeEngine.getServices(RaftNodeLifecycleAwareService.class);
            for (RaftNodeLifecycleAwareService service : services) {
                service.onRaftNodeSteppedDown(this.groupId);
            }
        }
    }
}

