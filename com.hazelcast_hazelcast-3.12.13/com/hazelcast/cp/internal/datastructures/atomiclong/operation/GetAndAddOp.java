/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.datastructures.atomiclong.RaftAtomicLong;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.AbstractAtomicLongOp;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class GetAndAddOp
extends AbstractAtomicLongOp
implements IndeterminateOperationStateAware {
    private long delta;

    public GetAndAddOp() {
    }

    public GetAndAddOp(String name, long delta) {
        super(name);
        this.delta = delta;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftAtomicLong atomic = this.getAtomicLong(groupId);
        return atomic.getAndAdd(this.delta);
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return this.delta == 0L;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeLong(this.delta);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.delta = in.readLong();
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", delta=").append(this.delta);
    }
}

