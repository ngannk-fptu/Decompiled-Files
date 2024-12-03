/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session;

import com.hazelcast.cp.internal.session.CPSessionInfo;
import com.hazelcast.cp.internal.session.RaftSessionRegistry;
import com.hazelcast.cp.internal.session.SessionResponse;
import com.hazelcast.cp.internal.session.operation.CloseInactiveSessionsOp;
import com.hazelcast.cp.internal.session.operation.CloseSessionOp;
import com.hazelcast.cp.internal.session.operation.CreateSessionOp;
import com.hazelcast.cp.internal.session.operation.ExpireSessionsOp;
import com.hazelcast.cp.internal.session.operation.GenerateThreadIdOp;
import com.hazelcast.cp.internal.session.operation.GetSessionsOp;
import com.hazelcast.cp.internal.session.operation.HeartbeatSessionOp;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class RaftSessionServiceDataSerializerHook
implements DataSerializerHook {
    private static final int RAFT_SESSION_DS_FACTORY_ID = -1003;
    private static final String RAFT_SESSION_DS_FACTORY = "hazelcast.serialization.ds.raft.session";
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.raft.session", -1003);
    public static final int RAFT_SESSION = 1;
    public static final int RAFT_SESSION_REGISTRY = 2;
    public static final int SESSION_RESPONSE = 3;
    public static final int CREATE_SESSION_OP = 4;
    public static final int HEARTBEAT_SESSION_OP = 5;
    public static final int CLOSE_SESSION_OP = 6;
    public static final int EXPIRE_SESSIONS_OP = 7;
    public static final int CLOSE_INACTIVE_SESSIONS_OP = 8;
    public static final int GET_SESSIONS_OP = 9;
    public static final int GENERATE_THREAD_ID_OP = 10;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        return new DataSerializableFactory(){

            @Override
            public IdentifiedDataSerializable create(int typeId) {
                switch (typeId) {
                    case 1: {
                        return new CPSessionInfo();
                    }
                    case 2: {
                        return new RaftSessionRegistry();
                    }
                    case 3: {
                        return new SessionResponse();
                    }
                    case 4: {
                        return new CreateSessionOp();
                    }
                    case 5: {
                        return new HeartbeatSessionOp();
                    }
                    case 6: {
                        return new CloseSessionOp();
                    }
                    case 7: {
                        return new ExpireSessionsOp();
                    }
                    case 8: {
                        return new CloseInactiveSessionsOp();
                    }
                    case 9: {
                        return new GetSessionsOp();
                    }
                    case 10: {
                        return new GenerateThreadIdOp();
                    }
                }
                throw new IllegalArgumentException("Undefined type: " + typeId);
            }
        };
    }
}

