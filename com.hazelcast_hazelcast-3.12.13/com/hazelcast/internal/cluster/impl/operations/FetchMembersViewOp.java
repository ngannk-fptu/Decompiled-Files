/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.MembersView;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;
import com.hazelcast.internal.cluster.impl.operations.JoinOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.exception.CallerNotMemberException;
import java.io.IOException;

public class FetchMembersViewOp
extends AbstractClusterOperation
implements JoinOperation {
    private String targetUuid;
    private MembersView membersView;

    public FetchMembersViewOp() {
    }

    public FetchMembersViewOp(String targetUuid) {
        this.targetUuid = targetUuid;
    }

    @Override
    public void run() throws Exception {
        ClusterServiceImpl service = (ClusterServiceImpl)this.getService();
        String thisUuid = service.getLocalMember().getUuid();
        if (!this.targetUuid.equals(thisUuid)) {
            throw new IllegalStateException("Rejecting mastership claim, since target UUID[" + this.targetUuid + "] is not matching local member UUID[" + thisUuid + "].");
        }
        this.membersView = service.handleMastershipClaim(this.getCallerAddress(), this.getCallerUuid());
    }

    @Override
    public boolean returnsResponse() {
        return true;
    }

    @Override
    public Object getResponse() {
        return this.membersView;
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        if (throwable instanceof MemberLeftException || throwable instanceof CallerNotMemberException) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        return super.onInvocationException(throwable);
    }

    @Override
    public int getId() {
        return 36;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.targetUuid);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.targetUuid = in.readUTF();
    }
}

