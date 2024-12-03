/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.util.ThreadUtil;

public class ShutdownNodeOp
extends AbstractClusterOperation
implements AllowedDuringPassiveState {
    @Override
    public void run() {
        ClusterServiceImpl clusterService = (ClusterServiceImpl)this.getService();
        ILogger logger = this.getLogger();
        ClusterState clusterState = clusterService.getClusterState();
        if (clusterState == ClusterState.PASSIVE) {
            final NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
            if (nodeEngine.isRunning()) {
                logger.info("Shutting down node in cluster passive state. Requested by: " + this.getCallerAddress());
                new Thread(new Runnable(){

                    @Override
                    public void run() {
                        Node node = nodeEngine.getNode();
                        node.hazelcastInstance.getLifecycleService().shutdown();
                    }
                }, ThreadUtil.createThreadName(nodeEngine.getHazelcastInstance().getName(), ".clusterShutdown")).start();
            } else {
                logger.info("Node is already shutting down. NodeState: " + (Object)((Object)nodeEngine.getNode().getState()));
            }
        } else {
            logger.severe("Can not shut down node because cluster is in " + (Object)((Object)clusterState) + " state. Requested by: " + this.getCallerAddress());
        }
    }

    @Override
    public int getId() {
        return 25;
    }
}

