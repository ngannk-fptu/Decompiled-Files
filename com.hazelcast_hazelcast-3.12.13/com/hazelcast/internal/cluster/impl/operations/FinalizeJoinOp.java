/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.core.Member;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.MembersView;
import com.hazelcast.internal.cluster.impl.operations.MembersUpdateOp;
import com.hazelcast.internal.cluster.impl.operations.OnJoinOp;
import com.hazelcast.internal.partition.PartitionRuntimeState;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationAccessor;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.OperationResponseHandlerFactory;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.operationservice.TargetAware;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.version.Version;
import java.io.IOException;
import java.util.Set;

public class FinalizeJoinOp
extends MembersUpdateOp
implements TargetAware {
    private OnJoinOp preJoinOp;
    private OnJoinOp postJoinOp;
    private String clusterId;
    private long clusterStartTime;
    private ClusterState clusterState;
    private Version clusterVersion;
    private transient boolean finalized;
    private transient Exception deserializationFailure;

    public FinalizeJoinOp() {
    }

    public FinalizeJoinOp(String targetUuid, MembersView members, OnJoinOp preJoinOp, OnJoinOp postJoinOp, long masterTime, String clusterId, long clusterStartTime, ClusterState clusterState, Version clusterVersion, PartitionRuntimeState partitionRuntimeState) {
        super(targetUuid, members, masterTime, partitionRuntimeState, true);
        this.preJoinOp = preJoinOp;
        this.postJoinOp = postJoinOp;
        this.clusterId = clusterId;
        this.clusterStartTime = clusterStartTime;
        this.clusterState = clusterState;
        this.clusterVersion = clusterVersion;
    }

    @Override
    public void run() throws Exception {
        ClusterServiceImpl clusterService = (ClusterServiceImpl)this.getService();
        Address callerAddress = this.getConnectionEndpointOrThisAddress();
        String callerUuid = this.getCallerUuid();
        String targetUuid = this.getTargetUuid();
        this.checkDeserializationFailure(clusterService);
        this.preparePostOp(this.preJoinOp);
        this.finalized = clusterService.finalizeJoin(this.getMembersView(), callerAddress, callerUuid, targetUuid, this.clusterId, this.clusterState, this.clusterVersion, this.clusterStartTime, this.masterTime, this.preJoinOp);
        if (!this.finalized) {
            return;
        }
        this.processPartitionState();
    }

    private void checkDeserializationFailure(ClusterServiceImpl clusterService) {
        if (this.deserializationFailure != null) {
            this.getLogger().severe("Node could not join cluster.", this.deserializationFailure);
            Node node = clusterService.getNodeEngine().getNode();
            node.shutdown(true);
            throw ExceptionUtil.rethrow(this.deserializationFailure);
        }
    }

    @Override
    public void afterRun() throws Exception {
        super.afterRun();
        if (!this.finalized) {
            return;
        }
        this.sendPostJoinOperations();
        if (this.preparePostOp(this.postJoinOp)) {
            this.getNodeEngine().getOperationService().run(this.postJoinOp);
        }
    }

    private boolean preparePostOp(Operation postOp) {
        if (postOp == null) {
            return false;
        }
        ClusterServiceImpl clusterService = (ClusterServiceImpl)this.getService();
        NodeEngineImpl nodeEngine = clusterService.getNodeEngine();
        postOp.setNodeEngine(nodeEngine);
        OperationAccessor.setCallerAddress(postOp, this.getCallerAddress());
        OperationAccessor.setConnection(postOp, this.getConnection());
        postOp.setOperationResponseHandler(OperationResponseHandlerFactory.createEmptyResponseHandler());
        return true;
    }

    private void sendPostJoinOperations() {
        ClusterServiceImpl clusterService = (ClusterServiceImpl)this.getService();
        NodeEngineImpl nodeEngine = clusterService.getNodeEngine();
        Operation[] postJoinOperations = nodeEngine.getPostJoinOperations();
        if (postJoinOperations != null && postJoinOperations.length > 0) {
            InternalOperationService operationService = nodeEngine.getOperationService();
            Set<Member> members = clusterService.getMembers();
            for (Member member : members) {
                if (member.localMember()) continue;
                OnJoinOp operation = new OnJoinOp(postJoinOperations);
                operationService.invokeOnTarget("hz:core:clusterService", operation, member.getAddress());
            }
        }
    }

    @Override
    protected void writeInternalImpl(ObjectDataOutput out) throws IOException {
        super.writeInternalImpl(out);
        boolean hasPJOp = this.postJoinOp != null;
        out.writeBoolean(hasPJOp);
        if (hasPJOp) {
            this.postJoinOp.writeData(out);
        }
        out.writeUTF(this.clusterId);
        out.writeLong(this.clusterStartTime);
        out.writeUTF(this.clusterState.toString());
        out.writeObject(this.clusterVersion);
        out.writeObject(this.preJoinOp);
    }

    @Override
    protected void readInternalImpl(ObjectDataInput in) throws IOException {
        super.readInternalImpl(in);
        boolean hasPostJoinOp = in.readBoolean();
        if (hasPostJoinOp) {
            this.postJoinOp = new OnJoinOp();
            try {
                this.postJoinOp.readData(in);
            }
            catch (Exception e) {
                this.deserializationFailure = e;
                return;
            }
        }
        this.clusterId = in.readUTF();
        this.clusterStartTime = in.readLong();
        String stateName = in.readUTF();
        this.clusterState = ClusterState.valueOf(stateName);
        this.clusterVersion = (Version)in.readObject();
        try {
            this.preJoinOp = (OnJoinOp)in.readObject();
        }
        catch (Exception e) {
            this.deserializationFailure = e;
        }
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", postJoinOp=").append(this.postJoinOp);
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    public void setTarget(Address address) {
        if (this.preJoinOp != null) {
            this.preJoinOp.setTarget(address);
        }
        if (this.postJoinOp != null) {
            this.postJoinOp.setTarget(address);
        }
    }
}

