/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.cluster.MemberAttributeOperationType;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class MemberAttributeChangedOp
extends AbstractClusterOperation {
    private MemberAttributeOperationType operationType;
    private String key;
    private Object value;

    public MemberAttributeChangedOp() {
    }

    public MemberAttributeChangedOp(MemberAttributeOperationType operationType, String key, Object value) {
        this.operationType = operationType;
        this.key = key;
        this.value = value;
    }

    @Override
    public void run() throws Exception {
        ClusterServiceImpl cs = (ClusterServiceImpl)this.getService();
        cs.updateMemberAttribute(this.getCallerUuid(), this.operationType, this.key, this.value);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.key);
        out.writeByte(this.operationType.getId());
        if (this.operationType == MemberAttributeOperationType.PUT) {
            out.writeObject(this.value);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.key = in.readUTF();
        this.operationType = MemberAttributeOperationType.getValue(in.readByte());
        if (this.operationType == MemberAttributeOperationType.PUT) {
            this.value = in.readObject();
        }
    }

    @Override
    public int getId() {
        return 19;
    }
}

