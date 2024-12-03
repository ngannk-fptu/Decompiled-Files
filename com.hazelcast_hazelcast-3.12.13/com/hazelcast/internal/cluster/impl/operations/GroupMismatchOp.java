/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.impl.NodeEngineImpl;

public class GroupMismatchOp
extends AbstractClusterOperation {
    @Override
    public void run() {
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        Connection connection = this.getConnection();
        String message = "Node could not join cluster at node: " + connection.getEndPoint() + " Cause: the target cluster has a different group-name";
        connection.close(message, null);
        ILogger logger = nodeEngine.getLogger("com.hazelcast.cluster");
        logger.warning(message);
        Node node = nodeEngine.getNode();
        node.getJoiner().blacklist(this.getCallerAddress(), true);
    }

    @Override
    public int getId() {
        return 12;
    }
}

