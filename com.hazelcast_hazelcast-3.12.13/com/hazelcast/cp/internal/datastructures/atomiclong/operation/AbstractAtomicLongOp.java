/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.datastructures.atomiclong.RaftAtomicLong;
import com.hazelcast.cp.internal.datastructures.atomiclong.RaftAtomicLongDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.atomiclong.RaftAtomicLongService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public abstract class AbstractAtomicLongOp
extends RaftOp
implements IdentifiedDataSerializable {
    private String name;

    AbstractAtomicLongOp() {
    }

    AbstractAtomicLongOp(String name) {
        this.name = name;
    }

    RaftAtomicLong getAtomicLong(CPGroupId groupId) {
        RaftAtomicLongService service = (RaftAtomicLongService)this.getService();
        return service.getAtomicLong(groupId, this.name);
    }

    @Override
    public final String getServiceName() {
        return "hz:raft:atomicLongService";
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
    public final int getFactoryId() {
        return RaftAtomicLongDataSerializerHook.F_ID;
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", name=").append(this.name);
    }
}

