/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.partition.NoDataMemberInClusterException;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.impl.operationservice.impl.Invocation;

final class PartitionInvocation
extends Invocation<PartitionReplica> {
    private final boolean failOnIndeterminateOperationState;

    PartitionInvocation(Invocation.Context context, Operation op, Runnable doneCallback, int tryCount, long tryPauseMillis, long callTimeoutMillis, boolean deserialize, boolean failOnIndeterminateOperationState, EndpointManager endpointManager) {
        super(context, op, doneCallback, tryCount, tryPauseMillis, callTimeoutMillis, deserialize, endpointManager);
        this.failOnIndeterminateOperationState = failOnIndeterminateOperationState && !(op instanceof ReadonlyOperation);
    }

    PartitionInvocation(Invocation.Context context, Operation op, int tryCount, long tryPauseMillis, long callTimeoutMillis, boolean deserialize, boolean failOnIndeterminateOperationState) {
        this(context, op, null, tryCount, tryPauseMillis, callTimeoutMillis, deserialize, failOnIndeterminateOperationState, null);
    }

    @Override
    PartitionReplica getInvocationTarget() {
        InternalPartition partition = this.context.partitionService.getPartition(this.op.getPartitionId());
        return partition.getReplica(this.op.getReplicaIndex());
    }

    @Override
    Address toTargetAddress(PartitionReplica replica) {
        return replica.address();
    }

    @Override
    Member toTargetMember(PartitionReplica replica) {
        return this.context.clusterService.getMember(replica.address(), replica.uuid());
    }

    @Override
    Exception newTargetNullException() {
        ClusterState clusterState = this.context.clusterService.getClusterState();
        if (!clusterState.isMigrationAllowed()) {
            return new IllegalStateException("Target of invocation cannot be found! Partition owner is null but partitions can't be assigned in cluster-state: " + (Object)((Object)clusterState));
        }
        if (this.context.clusterService.getSize(MemberSelectors.DATA_MEMBER_SELECTOR) == 0) {
            return new NoDataMemberInClusterException("Target of invocation cannot be found! Partition owner is null but partitions can't be assigned since all nodes in the cluster are lite members.");
        }
        return super.newTargetNullException();
    }

    @Override
    protected boolean shouldFailOnIndeterminateOperationState() {
        return this.failOnIndeterminateOperationState;
    }

    @Override
    ExceptionAction onException(Throwable t) {
        if (this.shouldFailOnIndeterminateOperationState() && t instanceof MemberLeftException) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        ExceptionAction action = this.op.onInvocationException(t);
        return action != null ? action : ExceptionAction.THROW_EXCEPTION;
    }
}

