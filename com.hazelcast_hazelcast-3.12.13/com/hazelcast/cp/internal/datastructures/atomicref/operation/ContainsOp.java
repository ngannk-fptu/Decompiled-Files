/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.datastructures.atomicref.RaftAtomicRef;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.AbstractAtomicRefOp;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class ContainsOp
extends AbstractAtomicRefOp
implements IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    private Data value;

    public ContainsOp() {
    }

    public ContainsOp(String name, Data value) {
        super(name);
        this.value = value;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftAtomicRef ref = this.getAtomicRef(groupId);
        return ref.contains(this.value);
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return true;
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeData(this.value);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.value = in.readData();
    }
}

