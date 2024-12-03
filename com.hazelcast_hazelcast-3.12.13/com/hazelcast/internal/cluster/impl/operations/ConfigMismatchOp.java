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

public class ConfigMismatchOp
extends AbstractClusterOperation {
    private String msg;

    public ConfigMismatchOp() {
    }

    public ConfigMismatchOp(String msg) {
        this.msg = msg;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.msg);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.msg = in.readUTF();
    }

    @Override
    public void run() {
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        Node node = nodeEngine.getNode();
        ILogger logger = nodeEngine.getLogger("com.hazelcast.cluster");
        logger.severe("Node could not join cluster. A Configuration mismatch was detected: " + this.msg + " Node is going to shutdown now!");
        node.shutdown(true);
    }

    @Override
    public int getId() {
        return 11;
    }
}

