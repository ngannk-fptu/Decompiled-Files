/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.JoinMessage;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class WhoisMasterOp
extends AbstractClusterOperation {
    private JoinMessage joinMessage;

    public WhoisMasterOp() {
    }

    public WhoisMasterOp(JoinMessage joinMessage) {
        this.joinMessage = joinMessage;
    }

    @Override
    public void run() {
        ClusterServiceImpl cm = (ClusterServiceImpl)this.getService();
        cm.getClusterJoinManager().answerWhoisMasterQuestion(this.joinMessage, this.getConnection());
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.joinMessage = new JoinMessage();
        this.joinMessage.readData(in);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        this.joinMessage.writeData(out);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", message=").append(this.joinMessage);
    }

    @Override
    public int getId() {
        return 18;
    }
}

