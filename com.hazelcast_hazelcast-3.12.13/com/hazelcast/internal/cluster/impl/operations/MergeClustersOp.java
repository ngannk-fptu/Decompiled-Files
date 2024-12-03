/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.NodeEngineImpl;
import java.io.IOException;

public class MergeClustersOp
extends AbstractClusterOperation {
    private Address newTargetAddress;

    public MergeClustersOp() {
    }

    public MergeClustersOp(Address newTargetAddress) {
        this.newTargetAddress = newTargetAddress;
    }

    @Override
    public void run() {
        boolean local;
        Address caller = this.getCallerAddress();
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        Node node = nodeEngine.getNode();
        final ClusterServiceImpl clusterService = node.getClusterService();
        Address masterAddress = clusterService.getMasterAddress();
        ILogger logger = node.loggingService.getLogger(this.getClass().getName());
        boolean bl = local = caller == null;
        if (!local && !caller.equals(masterAddress)) {
            logger.warning("Ignoring merge instruction sent from non-master endpoint: " + caller);
            return;
        }
        logger.warning(node.getThisAddress() + " is merging to " + this.newTargetAddress + ", because: instructed by master " + masterAddress);
        nodeEngine.getExecutionService().execute("hz:cluster:splitbrain", new Runnable(){

            @Override
            public void run() {
                clusterService.merge(MergeClustersOp.this.newTargetAddress);
            }
        });
    }

    @Override
    public boolean returnsResponse() {
        return true;
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.newTargetAddress = new Address();
        this.newTargetAddress.readData(in);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        this.newTargetAddress.writeData(out);
    }

    @Override
    public int getId() {
        return 21;
    }
}

