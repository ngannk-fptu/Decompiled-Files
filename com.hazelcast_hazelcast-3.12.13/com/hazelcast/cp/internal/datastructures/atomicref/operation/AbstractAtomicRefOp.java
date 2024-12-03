/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.datastructures.atomicref.RaftAtomicRef;
import com.hazelcast.cp.internal.datastructures.atomicref.RaftAtomicRefService;
import com.hazelcast.cp.internal.datastructures.atomicref.RaftAtomicReferenceDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public abstract class AbstractAtomicRefOp
extends RaftOp
implements IdentifiedDataSerializable {
    private String name;

    public AbstractAtomicRefOp() {
    }

    AbstractAtomicRefOp(String name) {
        this.name = name;
    }

    RaftAtomicRef getAtomicRef(CPGroupId groupId) {
        RaftAtomicRefService service = (RaftAtomicRefService)this.getService();
        return service.getAtomicRef(groupId, this.name);
    }

    @Override
    public final String getServiceName() {
        return "hz:raft:atomicRefService";
    }

    @Override
    public final int getFactoryId() {
        return RaftAtomicReferenceDataSerializerHook.F_ID;
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

