/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.datastructures.atomicref.RaftAtomicRef;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.AbstractAtomicRefOp;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class SetOp
extends AbstractAtomicRefOp
implements IdentifiedDataSerializable {
    private Data newValue;
    private boolean returnOldValue;

    public SetOp() {
    }

    public SetOp(String name, Data newValue, boolean returnOldValue) {
        super(name);
        this.newValue = newValue;
        this.returnOldValue = returnOldValue;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftAtomicRef ref = this.getAtomicRef(groupId);
        Data oldValue = ref.get();
        ref.set(this.newValue);
        return this.returnOldValue ? oldValue : null;
    }

    @Override
    public int getId() {
        return 6;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeData(this.newValue);
        out.writeBoolean(this.returnOldValue);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.newValue = in.readData();
        this.returnOldValue = in.readBoolean();
    }
}

