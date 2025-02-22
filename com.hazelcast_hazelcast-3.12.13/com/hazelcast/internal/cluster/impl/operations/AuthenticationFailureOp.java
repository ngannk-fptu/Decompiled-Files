/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.impl.NodeEngineImpl;

public class AuthenticationFailureOp
extends AbstractClusterOperation {
    @Override
    public void run() {
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        Node node = nodeEngine.getNode();
        ILogger logger = nodeEngine.getLogger("com.hazelcast.security");
        logger.severe("Node could not join cluster. Authentication failed on master node! Node is going to shutdown now!");
        node.shutdown(true);
    }

    @Override
    public int getId() {
        return 0;
    }
}

