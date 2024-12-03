/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.countdownlatch.operation;

import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.datastructures.countdownlatch.RaftCountDownLatchDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public abstract class AbstractCountDownLatchOp
extends RaftOp
implements IdentifiedDataSerializable {
    protected String name;

    public AbstractCountDownLatchOp() {
    }

    AbstractCountDownLatchOp(String name) {
        this.name = name;
    }

    @Override
    public final String getServiceName() {
        return "hz:raft:countDownLatchService";
    }

    @Override
    public final int getFactoryId() {
        return RaftCountDownLatchDataSerializerHook.F_ID;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", name=").append(this.name);
    }
}

