/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.cp.internal.CPGroupInfo;
import com.hazelcast.cp.internal.CPMemberInfo;
import com.hazelcast.cp.internal.MembershipChangeSchedule;
import com.hazelcast.cp.internal.MetadataRaftGroupSnapshot;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.operation.ChangeRaftGroupMembershipOp;
import com.hazelcast.cp.internal.operation.DefaultRaftReplicateOp;
import com.hazelcast.cp.internal.operation.DestroyRaftGroupOp;
import com.hazelcast.cp.internal.operation.RaftQueryOp;
import com.hazelcast.cp.internal.operation.RestartCPMemberOp;
import com.hazelcast.cp.internal.operation.integration.AppendFailureResponseOp;
import com.hazelcast.cp.internal.operation.integration.AppendRequestOp;
import com.hazelcast.cp.internal.operation.integration.AppendSuccessResponseOp;
import com.hazelcast.cp.internal.operation.integration.InstallSnapshotOp;
import com.hazelcast.cp.internal.operation.integration.PreVoteRequestOp;
import com.hazelcast.cp.internal.operation.integration.PreVoteResponseOp;
import com.hazelcast.cp.internal.operation.integration.VoteRequestOp;
import com.hazelcast.cp.internal.operation.integration.VoteResponseOp;
import com.hazelcast.cp.internal.raftop.GetInitialRaftGroupMembersIfCurrentGroupMemberOp;
import com.hazelcast.cp.internal.raftop.NotifyTermChangeOp;
import com.hazelcast.cp.internal.raftop.metadata.AddCPMemberOp;
import com.hazelcast.cp.internal.raftop.metadata.CompleteDestroyRaftGroupsOp;
import com.hazelcast.cp.internal.raftop.metadata.CompleteRaftGroupMembershipChangesOp;
import com.hazelcast.cp.internal.raftop.metadata.CreateRaftGroupOp;
import com.hazelcast.cp.internal.raftop.metadata.CreateRaftNodeOp;
import com.hazelcast.cp.internal.raftop.metadata.DestroyRaftNodesOp;
import com.hazelcast.cp.internal.raftop.metadata.ForceDestroyRaftGroupOp;
import com.hazelcast.cp.internal.raftop.metadata.GetActiveCPMembersOp;
import com.hazelcast.cp.internal.raftop.metadata.GetActiveRaftGroupByNameOp;
import com.hazelcast.cp.internal.raftop.metadata.GetActiveRaftGroupIdsOp;
import com.hazelcast.cp.internal.raftop.metadata.GetDestroyingRaftGroupIdsOp;
import com.hazelcast.cp.internal.raftop.metadata.GetMembershipChangeScheduleOp;
import com.hazelcast.cp.internal.raftop.metadata.GetRaftGroupIdsOp;
import com.hazelcast.cp.internal.raftop.metadata.GetRaftGroupOp;
import com.hazelcast.cp.internal.raftop.metadata.InitMetadataRaftGroupOp;
import com.hazelcast.cp.internal.raftop.metadata.PublishActiveCPMembersOp;
import com.hazelcast.cp.internal.raftop.metadata.RaftServicePreJoinOp;
import com.hazelcast.cp.internal.raftop.metadata.RemoveCPMemberOp;
import com.hazelcast.cp.internal.raftop.metadata.TriggerDestroyRaftGroupOp;
import com.hazelcast.cp.internal.raftop.snapshot.RestoreSnapshotOp;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public final class RaftServiceDataSerializerHook
implements DataSerializerHook {
    private static final int RAFT_DS_FACTORY_ID = -1002;
    private static final String RAFT_DS_FACTORY = "hazelcast.serialization.ds.raft.service";
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.raft.service", -1002);
    public static final int GROUP_ID = 1;
    public static final int RAFT_GROUP_INFO = 2;
    public static final int PRE_VOTE_REQUEST_OP = 3;
    public static final int PRE_VOTE_RESPONSE_OP = 4;
    public static final int VOTE_REQUEST_OP = 5;
    public static final int VOTE_RESPONSE_OP = 6;
    public static final int APPEND_REQUEST_OP = 7;
    public static final int APPEND_SUCCESS_RESPONSE_OP = 8;
    public static final int APPEND_FAILURE_RESPONSE_OP = 9;
    public static final int METADATA_RAFT_GROUP_SNAPSHOT = 10;
    public static final int INSTALL_SNAPSHOT_OP = 11;
    public static final int DEFAULT_RAFT_GROUP_REPLICATE_OP = 12;
    public static final int CREATE_RAFT_GROUP_OP = 13;
    public static final int TRIGGER_DESTROY_RAFT_GROUP_OP = 14;
    public static final int COMPLETE_DESTROY_RAFT_GROUPS_OP = 15;
    public static final int REMOVE_CP_MEMBER_OP = 16;
    public static final int COMPLETE_RAFT_GROUP_MEMBERSHIP_CHANGES_OP = 17;
    public static final int MEMBERSHIP_CHANGE_REPLICATE_OP = 18;
    public static final int MEMBERSHIP_CHANGE_SCHEDULE = 19;
    public static final int DEFAULT_RAFT_GROUP_QUERY_OP = 20;
    public static final int DESTROY_RAFT_NODES_OP = 21;
    public static final int GET_ACTIVE_CP_MEMBERS_OP = 22;
    public static final int GET_DESTROYING_RAFT_GROUP_IDS_OP = 23;
    public static final int GET_MEMBERSHIP_CHANGE_SCHEDULE_OP = 24;
    public static final int GET_RAFT_GROUP_OP = 25;
    public static final int GET_ACTIVE_RAFT_GROUP_BY_NAME_OP = 26;
    public static final int CREATE_RAFT_NODE_OP = 27;
    public static final int DESTROY_RAFT_GROUP_OP = 28;
    public static final int RESTORE_SNAPSHOT_OP = 29;
    public static final int NOTIFY_TERM_CHANGE_OP = 30;
    public static final int CP_MEMBER = 31;
    public static final int PUBLISH_ACTIVE_CP_MEMBERS_OP = 32;
    public static final int ADD_CP_MEMBER_OP = 33;
    public static final int INIT_METADATA_RAFT_GROUP_OP = 34;
    public static final int FORCE_DESTROY_RAFT_GROUP_OP = 35;
    public static final int GET_INITIAL_RAFT_GROUP_MEMBERS_IF_CURRENT_GROUP_MEMBER_OP = 36;
    public static final int GET_RAFT_GROUP_IDS_OP = 37;
    public static final int GET_ACTIVE_RAFT_GROUP_IDS_OP = 38;
    public static final int RAFT_PRE_JOIN_OP = 39;
    public static final int RESTART_CP_MEMBER_OP = 40;
    public static final int GROUP_MEMBERSHIP_CHANGE = 41;

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
                        return new RaftGroupId();
                    }
                    case 2: {
                        return new CPGroupInfo();
                    }
                    case 3: {
                        return new PreVoteRequestOp();
                    }
                    case 4: {
                        return new PreVoteResponseOp();
                    }
                    case 5: {
                        return new VoteRequestOp();
                    }
                    case 6: {
                        return new VoteResponseOp();
                    }
                    case 7: {
                        return new AppendRequestOp();
                    }
                    case 8: {
                        return new AppendSuccessResponseOp();
                    }
                    case 9: {
                        return new AppendFailureResponseOp();
                    }
                    case 10: {
                        return new MetadataRaftGroupSnapshot();
                    }
                    case 11: {
                        return new InstallSnapshotOp();
                    }
                    case 13: {
                        return new CreateRaftGroupOp();
                    }
                    case 12: {
                        return new DefaultRaftReplicateOp();
                    }
                    case 14: {
                        return new TriggerDestroyRaftGroupOp();
                    }
                    case 15: {
                        return new CompleteDestroyRaftGroupsOp();
                    }
                    case 16: {
                        return new RemoveCPMemberOp();
                    }
                    case 17: {
                        return new CompleteRaftGroupMembershipChangesOp();
                    }
                    case 18: {
                        return new ChangeRaftGroupMembershipOp();
                    }
                    case 19: {
                        return new MembershipChangeSchedule();
                    }
                    case 20: {
                        return new RaftQueryOp();
                    }
                    case 21: {
                        return new DestroyRaftNodesOp();
                    }
                    case 22: {
                        return new GetActiveCPMembersOp();
                    }
                    case 23: {
                        return new GetDestroyingRaftGroupIdsOp();
                    }
                    case 24: {
                        return new GetMembershipChangeScheduleOp();
                    }
                    case 25: {
                        return new GetRaftGroupOp();
                    }
                    case 26: {
                        return new GetActiveRaftGroupByNameOp();
                    }
                    case 27: {
                        return new CreateRaftNodeOp();
                    }
                    case 28: {
                        return new DestroyRaftGroupOp();
                    }
                    case 29: {
                        return new RestoreSnapshotOp();
                    }
                    case 30: {
                        return new NotifyTermChangeOp();
                    }
                    case 31: {
                        return new CPMemberInfo();
                    }
                    case 32: {
                        return new PublishActiveCPMembersOp();
                    }
                    case 33: {
                        return new AddCPMemberOp();
                    }
                    case 34: {
                        return new InitMetadataRaftGroupOp();
                    }
                    case 35: {
                        return new ForceDestroyRaftGroupOp();
                    }
                    case 36: {
                        return new GetInitialRaftGroupMembersIfCurrentGroupMemberOp();
                    }
                    case 37: {
                        return new GetRaftGroupIdsOp();
                    }
                    case 38: {
                        return new GetActiveRaftGroupIdsOp();
                    }
                    case 39: {
                        return new RaftServicePreJoinOp();
                    }
                    case 40: {
                        return new RestartCPMemberOp();
                    }
                    case 41: {
                        return new MembershipChangeSchedule.CPGroupMembershipChange();
                    }
                }
                throw new IllegalArgumentException("Undefined type: " + typeId);
            }
        };
    }
}

