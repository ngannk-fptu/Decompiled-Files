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

public class CompareAndSetOp
extends AbstractAtomicLongOp {
    private long currentValue;
    private long newValue;

    public CompareAndSetOp() {
    }

    public CompareAndSetOp(String name, long currentValue, long newValue) {
        super(name);
        this.currentValue = currentValue;
        this.newValue = newValue;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftAtomicLong atomic = this.getAtomicLong(groupId);
        return atomic.compareAndSet(this.currentValue, this.newValue);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeLong(this.currentValue);
        out.writeLong(this.newValue);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.currentValue = in.readLong();
        this.newValue = in.readLong();
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", currentValue=").append(this.currentValue);
        sb.append(", newValue=").append(this.newValue);
    }
}

