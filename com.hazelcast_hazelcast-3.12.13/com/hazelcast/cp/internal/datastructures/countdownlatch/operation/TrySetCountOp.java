/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.countdownlatch.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.datastructures.countdownlatch.RaftCountDownLatchService;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.AbstractCountDownLatchOp;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class TrySetCountOp
extends AbstractCountDownLatchOp {
    private int count;

    public TrySetCountOp() {
    }

    public TrySetCountOp(String name, int count) {
        super(name);
        this.count = count;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftCountDownLatchService service = (RaftCountDownLatchService)this.getService();
        return service.trySetCount(groupId, this.name, this.count);
    }

    @Override
    public int getId() {
        return 8;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeInt(this.count);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.count = in.readInt();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", count=").append(this.count);
    }
}

