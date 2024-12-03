/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.cluster.Joiner;
import com.hazelcast.cluster.impl.TcpIpJoiner;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.operations.AbstractJoinOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.impl.NodeEngineImpl;

public class JoinMastershipClaimOp
extends AbstractJoinOperation {
    private transient boolean approvedAsMaster;

    @Override
    public void run() {
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        Node node = nodeEngine.getNode();
        Joiner joiner = node.getJoiner();
        ClusterServiceImpl clusterService = node.getClusterService();
        ILogger logger = node.getLogger(this.getClass().getName());
        if (joiner instanceof TcpIpJoiner) {
            TcpIpJoiner tcpIpJoiner = (TcpIpJoiner)joiner;
            Address endpoint = this.getCallerAddress();
            Address masterAddress = clusterService.getMasterAddress();
            this.approvedAsMaster = !tcpIpJoiner.isClaimingMastership() && !clusterService.isMaster() && (masterAddress == null || masterAddress.equals(endpoint));
        } else {
            this.approvedAsMaster = false;
            logger.warning("This node requires MulticastJoin strategy!");
        }
        if (logger.isFineEnabled()) {
            logger.fine("Sending '" + this.approvedAsMaster + "' for master claim of node: " + this.getCallerAddress());
        }
    }

    @Override
    public Object getResponse() {
        return this.approvedAsMaster;
    }

    @Override
    public int getId() {
        return 16;
    }
}

