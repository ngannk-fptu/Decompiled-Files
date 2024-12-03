/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.datastructures.atomiclong.RaftAtomicLong;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.AbstractAtomicLongOp;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class GetAndSetOp
extends AbstractAtomicLongOp {
    private long value;

    public GetAndSetOp() {
    }

    public GetAndSetOp(String name, long value) {
        super(name);
        this.value = value;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftAtomicLong atomic = this.getAtomicLong(groupId);
        return atomic.getAndSet(this.value);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeLong(this.value);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.value = in.readLong();
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", value=").append(this.value);
    }
}

