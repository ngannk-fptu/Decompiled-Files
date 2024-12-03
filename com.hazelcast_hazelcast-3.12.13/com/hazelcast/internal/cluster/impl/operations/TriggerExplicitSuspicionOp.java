/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.MembersViewMetadata;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class TriggerExplicitSuspicionOp
extends AbstractClusterOperation {
    private int callerMemberListVersion;
    private MembersViewMetadata suspectedMembersViewMetadata;

    public TriggerExplicitSuspicionOp() {
    }

    public TriggerExplicitSuspicionOp(int callerMemberListVersion, MembersViewMetadata suspectedMembersViewMetadata) {
        this.callerMemberListVersion = callerMemberListVersion;
        this.suspectedMembersViewMetadata = suspectedMembersViewMetadata;
    }

    @Override
    public void run() throws Exception {
        ClusterServiceImpl clusterService = (ClusterServiceImpl)this.getService();
        clusterService.handleExplicitSuspicionTrigger(this.getCallerAddress(), this.callerMemberListVersion, this.suspectedMembersViewMetadata);
    }

    @Override
    public int getId() {
        return 39;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.callerMemberListVersion);
        out.writeObject(this.suspectedMembersViewMetadata);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.callerMemberListVersion = in.readInt();
        this.suspectedMembersViewMetadata = (MembersViewMetadata)in.readObject();
    }
}

