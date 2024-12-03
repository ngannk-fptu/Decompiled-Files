/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.core.IndeterminateOperationState;
import com.hazelcast.core.LocalMemberResetException;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.CPMember;
import com.hazelcast.cp.exception.LeaderDemotedException;
import com.hazelcast.cp.exception.NotLeaderException;
import com.hazelcast.cp.exception.StaleAppendRequestException;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.exception.CallerNotMemberException;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.impl.operationservice.impl.Invocation;
import com.hazelcast.spi.impl.operationservice.impl.RaftInvocationContext;

public class RaftInvocation
extends Invocation<CPMember> {
    private final RaftInvocationContext raftInvocationContext;
    private final CPGroupId groupId;
    private volatile RaftInvocationContext.MemberCursor memberCursor;
    private volatile CPMember lastInvocationEndpoint;
    private volatile Throwable indeterminateException;

    public RaftInvocation(Invocation.Context context, RaftInvocationContext raftInvocationContext, CPGroupId groupId, Operation op, int retryCount, long retryPauseMillis, long callTimeoutMillis) {
        this(context, raftInvocationContext, groupId, op, retryCount, retryPauseMillis, callTimeoutMillis, true);
    }

    public RaftInvocation(Invocation.Context context, RaftInvocationContext raftInvocationContext, CPGroupId groupId, Operation op, int retryCount, long retryPauseMillis, long callTimeoutMillis, boolean deserializeResponse) {
        super(context, op, null, retryCount, retryPauseMillis, callTimeoutMillis, deserializeResponse, null);
        this.raftInvocationContext = raftInvocationContext;
        this.groupId = groupId;
        int partitionId = context.partitionService.getPartitionId(groupId.id());
        op.setPartitionId(partitionId);
    }

    @Override
    CPMember getInvocationTarget() {
        CPMember target;
        this.lastInvocationEndpoint = target = this.getTargetEndpoint();
        return target;
    }

    @Override
    Address toTargetAddress(CPMember target) {
        return target.getAddress();
    }

    @Override
    Member toTargetMember(CPMember target) {
        return this.context.clusterService.getMember(target.getAddress());
    }

    @Override
    void notifyNormalResponse(Object value, int expectedBackups) {
        if (!(value instanceof IndeterminateOperationState) && this.indeterminateException != null && this.isRetryable(value)) {
            value = this.indeterminateException;
        }
        super.notifyNormalResponse(value, expectedBackups);
        this.raftInvocationContext.setKnownLeader(this.groupId, this.lastInvocationEndpoint);
    }

    @Override
    void notifyError(Object error) {
        if (error instanceof Throwable && ((Throwable)error).getCause() instanceof LocalMemberResetException) {
            return;
        }
        super.notifyError(error);
    }

    @Override
    protected ExceptionAction onException(Throwable t) {
        this.raftInvocationContext.updateKnownLeaderOnFailure(this.groupId, t);
        if (t instanceof IndeterminateOperationState) {
            if (this.isRetryableOnIndeterminateOperationState()) {
                if (this.indeterminateException == null) {
                    this.indeterminateException = t;
                }
                return ExceptionAction.RETRY_INVOCATION;
            }
            if (this.shouldFailOnIndeterminateOperationState()) {
                return ExceptionAction.THROW_EXCEPTION;
            }
            if (this.indeterminateException == null) {
                this.indeterminateException = t;
            }
        }
        return this.isRetryable(t) ? ExceptionAction.RETRY_INVOCATION : this.op.onInvocationException(t);
    }

    private boolean isRetryable(Object cause) {
        return cause instanceof NotLeaderException || cause instanceof LeaderDemotedException || cause instanceof StaleAppendRequestException || cause instanceof MemberLeftException || cause instanceof CallerNotMemberException || cause instanceof TargetNotMemberException;
    }

    @Override
    boolean skipTimeoutDetection() {
        return false;
    }

    private CPMember getTargetEndpoint() {
        CPMember target = this.raftInvocationContext.getKnownLeader(this.groupId);
        if (target != null) {
            return target;
        }
        RaftInvocationContext.MemberCursor cursor = this.memberCursor;
        if (cursor == null || !cursor.advance()) {
            cursor = this.raftInvocationContext.newMemberCursor(this.groupId);
            if (!cursor.advance()) {
                return null;
            }
            this.memberCursor = cursor;
        }
        return cursor.get();
    }

    private boolean isRetryableOnIndeterminateOperationState() {
        if (this.op instanceof IndeterminateOperationStateAware) {
            return ((IndeterminateOperationStateAware)((Object)this.op)).isRetryableOnIndeterminateOperationState();
        }
        return false;
    }

    @Override
    protected boolean shouldFailOnIndeterminateOperationState() {
        return this.raftInvocationContext.shouldFailOnIndeterminateOperationState();
    }
}

