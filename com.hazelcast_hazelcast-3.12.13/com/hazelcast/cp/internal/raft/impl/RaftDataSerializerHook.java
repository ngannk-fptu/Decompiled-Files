/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl;

import com.hazelcast.cp.internal.raft.command.DestroyRaftGroupCmd;
import com.hazelcast.cp.internal.raft.impl.command.UpdateRaftGroupMembersCmd;
import com.hazelcast.cp.internal.raft.impl.dto.AppendFailureResponse;
import com.hazelcast.cp.internal.raft.impl.dto.AppendRequest;
import com.hazelcast.cp.internal.raft.impl.dto.AppendSuccessResponse;
import com.hazelcast.cp.internal.raft.impl.dto.InstallSnapshot;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteRequest;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteResponse;
import com.hazelcast.cp.internal.raft.impl.dto.VoteRequest;
import com.hazelcast.cp.internal.raft.impl.dto.VoteResponse;
import com.hazelcast.cp.internal.raft.impl.log.LogEntry;
import com.hazelcast.cp.internal.raft.impl.log.SnapshotEntry;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public final class RaftDataSerializerHook
implements DataSerializerHook {
    private static final int RAFT_DS_FACTORY_ID = -1001;
    private static final String RAFT_DS_FACTORY = "hazelcast.serialization.ds.raft";
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.raft", -1001);
    public static final int PRE_VOTE_REQUEST = 1;
    public static final int PRE_VOTE_RESPONSE = 2;
    public static final int VOTE_REQUEST = 3;
    public static final int VOTE_RESPONSE = 4;
    public static final int APPEND_REQUEST = 5;
    public static final int APPEND_SUCCESS_RESPONSE = 6;
    public static final int APPEND_FAILURE_RESPONSE = 7;
    public static final int LOG_ENTRY = 8;
    public static final int SNAPSHOT_ENTRY = 9;
    public static final int INSTALL_SNAPSHOT = 10;
    public static final int DESTROY_RAFT_GROUP_COMMAND = 11;
    public static final int UPDATE_RAFT_GROUP_MEMBERS_COMMAND = 12;

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
                        return new PreVoteRequest();
                    }
                    case 2: {
                        return new PreVoteResponse();
                    }
                    case 3: {
                        return new VoteRequest();
                    }
                    case 4: {
                        return new VoteResponse();
                    }
                    case 5: {
                        return new AppendRequest();
                    }
                    case 6: {
                        return new AppendSuccessResponse();
                    }
                    case 7: {
                        return new AppendFailureResponse();
                    }
                    case 8: {
                        return new LogEntry();
                    }
                    case 9: {
                        return new SnapshotEntry();
                    }
                    case 10: {
                        return new InstallSnapshot();
                    }
                    case 11: {
                        return new DestroyRaftGroupCmd();
                    }
                    case 12: {
                        return new UpdateRaftGroupMembersCmd();
                    }
                }
                throw new IllegalArgumentException("Undefined type: " + typeId);
            }
        };
    }
}

