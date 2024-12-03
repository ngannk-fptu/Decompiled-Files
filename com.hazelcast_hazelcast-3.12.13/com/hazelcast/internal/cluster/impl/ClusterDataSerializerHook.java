/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.cluster.impl.VectorClock;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.MemberInfo;
import com.hazelcast.internal.cluster.impl.BindMessage;
import com.hazelcast.internal.cluster.impl.ClusterStateChange;
import com.hazelcast.internal.cluster.impl.ClusterStateTransactionLogRecord;
import com.hazelcast.internal.cluster.impl.ConfigCheck;
import com.hazelcast.internal.cluster.impl.ExtendedBindMessage;
import com.hazelcast.internal.cluster.impl.JoinMessage;
import com.hazelcast.internal.cluster.impl.JoinRequest;
import com.hazelcast.internal.cluster.impl.MembersView;
import com.hazelcast.internal.cluster.impl.MembersViewMetadata;
import com.hazelcast.internal.cluster.impl.SplitBrainJoinMessage;
import com.hazelcast.internal.cluster.impl.operations.AuthenticationFailureOp;
import com.hazelcast.internal.cluster.impl.operations.AuthorizationOp;
import com.hazelcast.internal.cluster.impl.operations.BeforeJoinCheckFailureOp;
import com.hazelcast.internal.cluster.impl.operations.CommitClusterStateOp;
import com.hazelcast.internal.cluster.impl.operations.ConfigMismatchOp;
import com.hazelcast.internal.cluster.impl.operations.ExplicitSuspicionOp;
import com.hazelcast.internal.cluster.impl.operations.FetchMembersViewOp;
import com.hazelcast.internal.cluster.impl.operations.FinalizeJoinOp;
import com.hazelcast.internal.cluster.impl.operations.GroupMismatchOp;
import com.hazelcast.internal.cluster.impl.operations.HeartbeatComplaintOp;
import com.hazelcast.internal.cluster.impl.operations.HeartbeatOp;
import com.hazelcast.internal.cluster.impl.operations.JoinMastershipClaimOp;
import com.hazelcast.internal.cluster.impl.operations.JoinRequestOp;
import com.hazelcast.internal.cluster.impl.operations.LockClusterStateOp;
import com.hazelcast.internal.cluster.impl.operations.MasterResponseOp;
import com.hazelcast.internal.cluster.impl.operations.MemberAttributeChangedOp;
import com.hazelcast.internal.cluster.impl.operations.MembersUpdateOp;
import com.hazelcast.internal.cluster.impl.operations.MergeClustersOp;
import com.hazelcast.internal.cluster.impl.operations.OnJoinOp;
import com.hazelcast.internal.cluster.impl.operations.PromoteLiteMemberOp;
import com.hazelcast.internal.cluster.impl.operations.RollbackClusterStateOp;
import com.hazelcast.internal.cluster.impl.operations.ShutdownNodeOp;
import com.hazelcast.internal.cluster.impl.operations.SplitBrainMergeValidationOp;
import com.hazelcast.internal.cluster.impl.operations.TriggerExplicitSuspicionOp;
import com.hazelcast.internal.cluster.impl.operations.TriggerMemberListPublishOp;
import com.hazelcast.internal.cluster.impl.operations.WhoisMasterOp;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.ArrayDataSerializableFactory;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.version.Version;

public final class ClusterDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = 0;
    public static final int AUTH_FAILURE = 0;
    public static final int ADDRESS = 1;
    public static final int MEMBER = 2;
    public static final int HEARTBEAT = 3;
    public static final int CONFIG_CHECK = 4;
    public static final int BIND_MESSAGE = 5;
    public static final int MEMBER_INFO_UPDATE = 6;
    public static final int FINALIZE_JOIN = 7;
    public static final int AUTHORIZATION = 8;
    public static final int BEFORE_JOIN_CHECK_FAILURE = 9;
    public static final int CHANGE_CLUSTER_STATE = 10;
    public static final int CONFIG_MISMATCH = 11;
    public static final int GROUP_MISMATCH = 12;
    public static final int SPLIT_BRAIN_MERGE_VALIDATION = 13;
    public static final int JOIN_REQUEST_OP = 14;
    public static final int LOCK_CLUSTER_STATE = 15;
    public static final int MASTER_CLAIM = 16;
    public static final int WHOIS_MASTER = 18;
    public static final int MEMBER_ATTR_CHANGED = 19;
    public static final int MERGE_CLUSTERS = 21;
    public static final int POST_JOIN = 22;
    public static final int ROLLBACK_CLUSTER_STATE = 23;
    public static final int MASTER_RESPONSE = 24;
    public static final int SHUTDOWN_NODE = 25;
    public static final int TRIGGER_MEMBER_LIST_PUBLISH = 26;
    public static final int CLUSTER_STATE_TRANSACTION_LOG_RECORD = 27;
    public static final int MEMBER_INFO = 28;
    public static final int JOIN_MESSAGE = 29;
    public static final int JOIN_REQUEST = 30;
    public static final int MIGRATION_INFO = 31;
    public static final int MEMBER_VERSION = 32;
    public static final int CLUSTER_STATE_CHANGE = 33;
    public static final int SPLIT_BRAIN_JOIN_MESSAGE = 34;
    public static final int VERSION = 35;
    public static final int FETCH_MEMBER_LIST_STATE = 36;
    public static final int EXPLICIT_SUSPICION = 37;
    public static final int MEMBERS_VIEW = 38;
    public static final int TRIGGER_EXPLICIT_SUSPICION = 39;
    public static final int MEMBERS_VIEW_METADATA = 40;
    public static final int HEARTBEAT_COMPLAINT = 41;
    public static final int PROMOTE_LITE_MEMBER = 42;
    public static final int VECTOR_CLOCK = 43;
    public static final int EXTENDED_BIND_MESSAGE = 44;
    public static final int ENDPOINT_QUALIFIER = 45;
    static final int LEN = 46;

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public DataSerializableFactory createFactory() {
        ConstructorFunction[] constructors = new ConstructorFunction[46];
        constructors[0] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AuthenticationFailureOp();
            }
        };
        constructors[1] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new Address();
            }
        };
        constructors[2] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MemberImpl();
            }
        };
        constructors[3] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new HeartbeatOp();
            }
        };
        constructors[4] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ConfigCheck();
            }
        };
        constructors[5] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BindMessage();
            }
        };
        constructors[6] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MembersUpdateOp();
            }
        };
        constructors[7] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new FinalizeJoinOp();
            }
        };
        constructors[8] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AuthorizationOp();
            }
        };
        constructors[9] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BeforeJoinCheckFailureOp();
            }
        };
        constructors[10] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CommitClusterStateOp();
            }
        };
        constructors[11] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ConfigMismatchOp();
            }
        };
        constructors[12] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new GroupMismatchOp();
            }
        };
        constructors[13] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SplitBrainMergeValidationOp();
            }
        };
        constructors[14] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new JoinRequestOp();
            }
        };
        constructors[15] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LockClusterStateOp();
            }
        };
        constructors[16] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new JoinMastershipClaimOp();
            }
        };
        constructors[18] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new WhoisMasterOp();
            }
        };
        constructors[19] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MemberAttributeChangedOp();
            }
        };
        constructors[21] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MergeClustersOp();
            }
        };
        constructors[22] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new OnJoinOp();
            }
        };
        constructors[23] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RollbackClusterStateOp();
            }
        };
        constructors[24] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MasterResponseOp();
            }
        };
        constructors[25] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ShutdownNodeOp();
            }
        };
        constructors[26] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TriggerMemberListPublishOp();
            }
        };
        constructors[27] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ClusterStateTransactionLogRecord();
            }
        };
        constructors[28] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MemberInfo();
            }
        };
        constructors[29] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new JoinMessage();
            }
        };
        constructors[30] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new JoinRequest();
            }
        };
        constructors[31] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MigrationInfo();
            }
        };
        constructors[32] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MemberVersion();
            }
        };
        constructors[33] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ClusterStateChange();
            }
        };
        constructors[34] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SplitBrainJoinMessage();
            }
        };
        constructors[35] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new Version();
            }
        };
        constructors[36] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new FetchMembersViewOp();
            }
        };
        constructors[37] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ExplicitSuspicionOp();
            }
        };
        constructors[38] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MembersView();
            }
        };
        constructors[39] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TriggerExplicitSuspicionOp();
            }
        };
        constructors[40] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MembersViewMetadata();
            }
        };
        constructors[41] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new HeartbeatComplaintOp();
            }
        };
        constructors[42] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PromoteLiteMemberOp();
            }
        };
        constructors[43] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new VectorClock();
            }
        };
        constructors[44] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ExtendedBindMessage();
            }
        };
        constructors[45] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EndpointQualifier();
            }
        };
        return new ArrayDataSerializableFactory(constructors);
    }
}

