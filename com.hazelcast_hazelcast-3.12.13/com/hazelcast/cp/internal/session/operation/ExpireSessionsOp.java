/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.session.RaftSessionService;
import com.hazelcast.cp.internal.session.RaftSessionServiceDataSerializerHook;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ExpireSessionsOp
extends RaftOp
implements IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    private Collection<Tuple2<Long, Long>> sessions;

    public ExpireSessionsOp() {
    }

    public ExpireSessionsOp(Collection<Tuple2<Long, Long>> sessionIds) {
        this.sessions = sessionIds;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftSessionService service = (RaftSessionService)this.getService();
        service.expireSessions(groupId, this.sessions);
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
        return 7;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.sessions.size());
        for (Tuple2<Long, Long> s : this.sessions) {
            out.writeLong((Long)s.element1);
            out.writeLong((Long)s.element2);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        ArrayList<Tuple2<Long, Long>> sessionIds = new ArrayList<Tuple2<Long, Long>>();
        for (int i = 0; i < size; ++i) {
            long sessionId = in.readLong();
            long version = in.readLong();
            sessionIds.add(Tuple2.of(sessionId, version));
        }
        this.sessions = sessionIds;
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", sessions=").append(this.sessions);
    }
}

