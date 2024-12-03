/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raftop.metadata;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.MetadataRaftGroupManager;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.util.Preconditions;

public abstract class MetadataRaftGroupOp
extends RaftOp {
    @Override
    public final Object run(CPGroupId groupId, long commitIndex) throws Exception {
        RaftService service = (RaftService)this.getService();
        MetadataRaftGroupManager metadataGroupManager = service.getMetadataGroupManager();
        Preconditions.checkTrue(metadataGroupManager.getMetadataGroupId().equals(groupId), "Cannot perform CP subsystem management call on " + groupId);
        return this.run(metadataGroupManager, commitIndex);
    }

    public abstract Object run(MetadataRaftGroupManager var1, long var2) throws Exception;

    @Override
    public final String getServiceName() {
        return "hz:core:raft";
    }
}

