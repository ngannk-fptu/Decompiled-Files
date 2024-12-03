/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.core.Member;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.SplitBrainJoinMessage;
import com.hazelcast.internal.cluster.impl.operations.AbstractJoinOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.NodeEngineImpl;
import java.io.IOException;

public class SplitBrainMergeValidationOp
extends AbstractJoinOperation {
    private SplitBrainJoinMessage request;
    private SplitBrainJoinMessage response;
    private transient Member suspectedCaller;

    public SplitBrainMergeValidationOp() {
    }

    public SplitBrainMergeValidationOp(SplitBrainJoinMessage request) {
        this.request = request;
    }

    @Override
    public void run() {
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        Node node = nodeEngine.getNode();
        if (!this.preCheck(node)) {
            return;
        }
        if (!this.masterCheck()) {
            return;
        }
        if (this.request != null) {
            ILogger logger = this.getLogger();
            if (this.checkSplitBrainJoinMessage()) {
                this.response = node.createSplitBrainJoinMessage();
            }
            if (logger.isFineEnabled()) {
                logger.fine("Returning " + this.response + " to " + this.getCallerAddress());
            }
        }
    }

    @Override
    public void afterRun() throws Exception {
        if (this.suspectedCaller != null) {
            ClusterServiceImpl service = (ClusterServiceImpl)this.getService();
            String reason = "Removing " + this.suspectedCaller + ", since it thinks it's already split from this cluster and looking to merge.";
            service.suspectMember(this.suspectedCaller, reason, true);
        }
    }

    private boolean preCheck(Node node) {
        ILogger logger = this.getLogger();
        ClusterServiceImpl clusterService = node.getClusterService();
        if (!clusterService.isJoined()) {
            logger.info("Ignoring join check from " + this.getCallerAddress() + ", because this node is not joined to a cluster yet...");
            return false;
        }
        if (!node.isRunning()) {
            logger.info("Ignoring join check from " + this.getCallerAddress() + " because this node is not active...");
            return false;
        }
        ClusterState clusterState = clusterService.getClusterState();
        if (!clusterState.isJoinAllowed()) {
            logger.info("Ignoring join check from " + this.getCallerAddress() + " because cluster is in " + (Object)((Object)clusterState) + " state...");
            return false;
        }
        return true;
    }

    private boolean masterCheck() {
        ILogger logger = this.getLogger();
        ClusterServiceImpl service = (ClusterServiceImpl)this.getService();
        if (service.isMaster()) {
            MemberImpl existingMember = service.getMembershipManager().getMember(this.request.getAddress(), this.request.getUuid());
            if (existingMember != null) {
                logger.info("Removing " + this.suspectedCaller + ", since it thinks it's already split from this cluster and looking to merge.");
                this.suspectedCaller = existingMember;
            }
            return true;
        }
        logger.info("Ignoring join check from " + this.getCallerAddress() + ", because this node is not master...");
        return false;
    }

    private boolean checkSplitBrainJoinMessage() {
        ClusterServiceImpl service = (ClusterServiceImpl)this.getService();
        ILogger logger = this.getLogger();
        try {
            if (!service.getClusterJoinManager().validateJoinMessage(this.request)) {
                return false;
            }
            if (!service.getClusterVersion().equals(this.request.getClusterVersion())) {
                logger.info("Join check from " + this.getCallerAddress() + " failed validation due to incompatible version,remote cluster version is " + this.request.getClusterVersion() + ", this cluster is " + service.getClusterVersion());
                return false;
            }
            SplitBrainJoinMessage.SplitBrainMergeCheckResult result = service.getClusterJoinManager().shouldMerge(this.request);
            if (result == SplitBrainJoinMessage.SplitBrainMergeCheckResult.REMOTE_NODE_SHOULD_MERGE) {
                return service.getMembershipManager().verifySplitBrainMergeMemberListVersion(this.request);
            }
            return result != SplitBrainJoinMessage.SplitBrainMergeCheckResult.CANNOT_MERGE;
        }
        catch (Exception e) {
            if (logger.isFineEnabled()) {
                logger.fine("Could not validate split-brain join message! -> " + e.getMessage());
            }
            return false;
        }
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.request = new SplitBrainJoinMessage();
        this.request.readData(in);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        this.request.writeData(out);
    }

    @Override
    public int getId() {
        return 13;
    }
}

