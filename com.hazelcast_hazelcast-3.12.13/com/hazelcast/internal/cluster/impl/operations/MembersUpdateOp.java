/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.MemberInfo;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.MembersView;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;
import com.hazelcast.internal.partition.PartitionRuntimeState;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.util.Clock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MembersUpdateOp
extends AbstractClusterOperation
implements Versioned {
    long masterTime = Clock.currentTimeMillis();
    private List<MemberInfo> memberInfos;
    private String targetUuid;
    private boolean returnResponse;
    private PartitionRuntimeState partitionRuntimeState;
    private int memberListVersion;

    public MembersUpdateOp() {
        this.memberInfos = Collections.emptyList();
    }

    public MembersUpdateOp(String targetUuid, MembersView membersView, long masterTime, PartitionRuntimeState partitionRuntimeState, boolean returnResponse) {
        this.targetUuid = targetUuid;
        this.masterTime = masterTime;
        this.memberInfos = membersView.getMembers();
        this.returnResponse = returnResponse;
        this.partitionRuntimeState = partitionRuntimeState;
        this.memberListVersion = membersView.getVersion();
    }

    @Override
    public void run() throws Exception {
        ClusterServiceImpl clusterService = (ClusterServiceImpl)this.getService();
        Address callerAddress = this.getConnectionEndpointOrThisAddress();
        String callerUuid = this.getCallerUuid();
        if (clusterService.updateMembers(this.getMembersView(), callerAddress, callerUuid, this.targetUuid)) {
            this.processPartitionState();
        }
    }

    final int getMemberListVersion() {
        return this.memberListVersion;
    }

    final MembersView getMembersView() {
        return new MembersView(this.getMemberListVersion(), Collections.unmodifiableList(this.memberInfos));
    }

    final String getTargetUuid() {
        return this.targetUuid;
    }

    final Address getConnectionEndpointOrThisAddress() {
        ClusterServiceImpl clusterService = (ClusterServiceImpl)this.getService();
        NodeEngineImpl nodeEngine = clusterService.getNodeEngine();
        Node node = nodeEngine.getNode();
        Connection conn = this.getConnection();
        return conn != null ? conn.getEndPoint() : node.getThisAddress();
    }

    final void processPartitionState() {
        if (this.partitionRuntimeState == null) {
            return;
        }
        this.partitionRuntimeState.setMaster(this.getCallerAddress());
        ClusterServiceImpl clusterService = (ClusterServiceImpl)this.getService();
        Node node = clusterService.getNodeEngine().getNode();
        node.partitionService.processPartitionRuntimeState(this.partitionRuntimeState);
    }

    @Override
    public final boolean returnsResponse() {
        return this.returnResponse;
    }

    protected void readInternalImpl(ObjectDataInput in) throws IOException {
        this.targetUuid = in.readUTF();
        this.masterTime = in.readLong();
        int size = in.readInt();
        this.memberInfos = new ArrayList<MemberInfo>(size);
        while (size-- > 0) {
            MemberInfo memberInfo = new MemberInfo();
            memberInfo.readData(in);
            this.memberInfos.add(memberInfo);
        }
        this.partitionRuntimeState = (PartitionRuntimeState)in.readObject();
        this.returnResponse = in.readBoolean();
    }

    @Override
    protected final void readInternal(ObjectDataInput in) throws IOException {
        this.readInternalImpl(in);
        this.memberListVersion = in.readInt();
    }

    protected void writeInternalImpl(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.targetUuid);
        out.writeLong(this.masterTime);
        out.writeInt(this.memberInfos.size());
        for (MemberInfo memberInfo : this.memberInfos) {
            memberInfo.writeData(out);
        }
        out.writeObject(this.partitionRuntimeState);
        out.writeBoolean(this.returnResponse);
    }

    @Override
    protected final void writeInternal(ObjectDataOutput out) throws IOException {
        this.writeInternalImpl(out);
        out.writeInt(this.memberListVersion);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", targetUuid=").append(this.targetUuid);
        sb.append(", members=");
        for (MemberInfo address : this.memberInfos) {
            sb.append(address).append(' ');
        }
    }

    @Override
    public int getId() {
        return 6;
    }
}

