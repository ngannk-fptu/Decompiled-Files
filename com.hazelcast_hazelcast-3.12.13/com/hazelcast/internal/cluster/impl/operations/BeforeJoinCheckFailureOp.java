/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.NodeEngineImpl;
import java.io.IOException;

public class BeforeJoinCheckFailureOp
extends AbstractClusterOperation {
    private String failReasonMsg;

    public BeforeJoinCheckFailureOp() {
    }

    public BeforeJoinCheckFailureOp(String failReasonMsg) {
        this.failReasonMsg = failReasonMsg;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.failReasonMsg);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.failReasonMsg = in.readUTF();
    }

    @Override
    public void run() {
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        Node node = nodeEngine.getNode();
        if (node.getClusterService().isJoined()) {
            throw new IllegalStateException("Node is already joined but received a termination message! Reason: " + this.failReasonMsg);
        }
        ILogger logger = nodeEngine.getLogger("com.hazelcast.security");
        logger.severe("Node could not join cluster. Before join check failed node is going to shutdown now!");
        logger.severe("Reason of failure for node join: " + this.failReasonMsg);
        node.shutdown(true);
    }

    @Override
    public int getId() {
        return 9;
    }
}

