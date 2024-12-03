/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.session.RaftSessionService;
import com.hazelcast.cp.internal.session.RaftSessionServiceDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class CloseInactiveSessionsOp
extends RaftOp
implements IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    private Collection<Long> sessions;

    public CloseInactiveSessionsOp() {
    }

    public CloseInactiveSessionsOp(Collection<Long> sessions) {
        this.sessions = sessions;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftSessionService service = (RaftSessionService)this.getService();
        service.closeInactiveSessions(groupId, this.sessions);
        return null;
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return true;
    }

    @Override
    public String getServiceName() {
        return "hz:core:raftSession";
    }

    @Override
    public int getFactoryId() {
        return RaftSessionServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 8;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.sessions.size());
        for (long sessionId : this.sessions) {
            out.writeLong(sessionId);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int count = in.readInt();
        this.sessions = new ArrayList<Long>();
        for (int i = 0; i < count; ++i) {
            long sessionId = in.readLong();
            this.sessions.add(sessionId);
        }
    }
}

