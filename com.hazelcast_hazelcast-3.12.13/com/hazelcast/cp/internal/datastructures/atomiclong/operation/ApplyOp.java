/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong.operation;

import com.hazelcast.core.IFunction;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.datastructures.atomiclong.RaftAtomicLong;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.AbstractAtomicLongOp;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class ApplyOp<R>
extends AbstractAtomicLongOp {
    private IFunction<Long, R> function;

    public ApplyOp() {
    }

    public ApplyOp(String name, IFunction<Long, R> function) {
        super(name);
        this.function = function;
    }

    @Override
    public int getId() {
        return 6;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftAtomicLong atomic = this.getAtomicLong(groupId);
        long val = atomic.getAndAdd(0L);
        return this.function.apply(val);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeObject(this.function);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.function = (IFunction)in.readObject();
    }
}

